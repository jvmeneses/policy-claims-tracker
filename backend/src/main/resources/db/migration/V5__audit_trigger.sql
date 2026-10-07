-- trg_claim_status_audit
-- Writes a claim_audit row whenever a claim's status changes, no matter who or
-- what changed it (the Java app, usp_ApproveClaim, or a manual UPDATE).
-- Triggers fire once per statement, not per row, so this is written set-based using
-- the inserted (new values) and deleted (old values) pseudo-tables.
CREATE OR ALTER TRIGGER trg_claim_status_audit
ON claim
AFTER UPDATE
                          AS
BEGIN
    SET NOCOUNT ON;

    -- Skip quickly when the UPDATE didn't touch the status column.
    IF NOT UPDATE(status) RETURN;

INSERT INTO claim_audit (claim_id, old_status, new_status)
SELECT i.id, d.status, i.status
FROM inserted i
         JOIN deleted d ON d.id = i.id
WHERE i.status <> d.status;  -- only rows whose status actually changed
-- changed_at and changed_by come from the column defaults (SYSUTCDATETIME, SUSER_SNAME)
END;