CREATE NONCLUSTERED INDEX IX_claim_status_filed_at
    ON claim (status, filed_at DESC)
    INCLUDE (policy_id, claim_amount);