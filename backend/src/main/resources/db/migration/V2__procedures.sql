-- usp_ApproveClaim
-- Approves a claim that is UNDER_REVIEW, enforcing business rules 4 and 5.
-- Error numbers (read by the Java layer and mapped to HTTP 409):
--   50001 claim not found
--   50002 claim is not UNDER_REVIEW
--   50003 approved amount invalid (<= 0 or > claim amount)
--   50004 approval would exceed the policy's coverage limit
CREATE OR ALTER PROCEDURE usp_ApproveClaim
    @ClaimId        BIGINT,
    @ApprovedAmount DECIMAL(12,2),
    @ReviewerNote   NVARCHAR(1000) = NULL
    AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;  -- any runtime error aborts and rolls back the transaction

    DECLARE @PolicyId        BIGINT,
            @Status          NVARCHAR(15),
            @ClaimAmount     DECIMAL(12,2),
            @CoverageLimit   DECIMAL(12,2),
            @AlreadyApproved DECIMAL(12,2),
            @Msg             NVARCHAR(400);

BEGIN TRY
BEGIN TRANSACTION;

        -- A. Load the claim and lock its row so concurrent approvals of the
        --    same claim queue up instead of both passing the status check.
SELECT @PolicyId    = policy_id,
       @Status      = status,
       @ClaimAmount = claim_amount
FROM claim WITH (UPDLOCK, ROWLOCK)
WHERE id = @ClaimId;

IF @PolicyId IS NULL
BEGIN
            SET @Msg = CONCAT('Claim ', @ClaimId, ' not found.');
            THROW 50001, @Msg, 1;
END;

        -- B. Only claims under review can be approved.
        IF @Status <> 'UNDER_REVIEW'
BEGIN
            SET @Msg = CONCAT('Claim ', @ClaimId, ' must be UNDER_REVIEW to approve (current status: ', @Status, ').');
            THROW 50002, @Msg, 1;
END;

        -- C. Rule 5: approved amount must be > 0 and <= the claimed amount.
        IF @ApprovedAmount IS NULL OR @ApprovedAmount <= 0 OR @ApprovedAmount > @ClaimAmount
BEGIN
            SET @Msg = CONCAT('Approved amount must be greater than 0 and at most the claimed amount (', @ClaimAmount, ').');
            THROW 50003, @Msg, 1;
END;

        -- D. Rule 4: total approved on the policy must stay within coverage_limit.
        --    Lock the POLICY row too: without it, two different claims on the same
        --    policy could be approved concurrently, each passing the check on its own
        --    and together exceeding the limit. The second caller waits here and then
        --    sees the first approval in the SUM below.
SELECT @CoverageLimit = coverage_limit
FROM policy WITH (UPDLOCK, ROWLOCK)
WHERE id = @PolicyId;

SELECT @AlreadyApproved = COALESCE(SUM(approved_amount), 0)
FROM claim
WHERE policy_id = @PolicyId
  AND status = 'APPROVED'
  AND id <> @ClaimId;

IF @AlreadyApproved + @ApprovedAmount > @CoverageLimit
BEGIN
            SET @Msg = CONCAT('Approval exceeds coverage limit. Limit: ', @CoverageLimit,
                              ', already approved: ', @AlreadyApproved,
                              ', remaining: ', @CoverageLimit - @AlreadyApproved, '.');
            THROW 50004, @Msg, 1;
END;

        -- E. Apply the approval. The audit row is written by the status trigger (V5).
UPDATE claim
SET status          = 'APPROVED',
    approved_amount = @ApprovedAmount,
    reviewer_note   = @ReviewerNote
WHERE id = @ClaimId;

COMMIT TRANSACTION;
END TRY
BEGIN CATCH
IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;  -- rethrow the original error (number and message) to the caller
END CATCH
END;