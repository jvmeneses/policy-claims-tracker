package com.example.claims;

import org.junit.jupiter.api.Test;

class ClaimApprovalIT extends AbstractIntegrationTest {
    // Flyway runs against the container, so the seed data (V6) is available.
    // TODO(you): integration tests
    //   1. approve CLM-0002 (UNDER_REVIEW) -> status APPROVED, approved_amount set
    //   2. approve CLM-0004 with an amount that pushes the policy over coverage_limit -> BusinessRuleException (rule 4)
    //   3. approve a SUBMITTED claim -> rejected (wrong status)
    //   4. after approval, claim_audit has a row with new_status = 'APPROVED' (proves the trigger works)
    @Test void placeholder() {}
}
