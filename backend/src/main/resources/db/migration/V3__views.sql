-- vw_PolicyLossRatio
-- One row per policy: premium, total approved claim payouts, loss ratio
-- (payouts / premium), and the policy's rank within its type (1 = worst ratio).
-- Backs GET /api/reports/loss-ratio and the dashboard.
CREATE OR ALTER VIEW vw_PolicyLossRatio
AS
WITH approved AS (
    -- Total paid out per policy. Only APPROVED claims count as payouts.
    SELECT policy_id,
           SUM(approved_amount) AS total_approved
    FROM claim
    WHERE status = 'APPROVED'
    GROUP BY policy_id
),
ratios AS (
    -- LEFT JOIN keeps policies with no approved claims (they get 0, not NULL).
    -- premium_amount is guaranteed > 0 by a CHECK constraint, so no divide-by-zero.
    SELECT p.id AS policy_id,
           p.policy_number,
           p.type,
           p.premium_amount,
           COALESCE(a.total_approved, 0) AS total_approved,
           CAST(COALESCE(a.total_approved, 0) / p.premium_amount AS DECIMAL(10,4)) AS loss_ratio
    FROM policy p
    LEFT JOIN approved a ON a.policy_id = p.id
)
SELECT policy_id,
       policy_number,
       type,
       premium_amount,
       total_approved,
       loss_ratio,
       -- Window function: rank policies against others of the same type.
       -- RANK() gives ties the same rank and leaves a gap after them.
       RANK() OVER (PARTITION BY type ORDER BY loss_ratio DESC) AS rank_in_type
FROM ratios;