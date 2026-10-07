package com.example.claims;

import static com.example.claims.dto.Dtos.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.claims.domain.ClaimStatus;
import com.example.claims.exception.BusinessRuleException;
import com.example.claims.exception.NotFoundException;
import com.example.claims.service.ClaimService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Runs the real stack against SQL Server in Testcontainers: Spring service ->
 * usp_ApproveClaim -> trg_claim_status_audit. Flyway applies V1-V6 to the container first.
 * Each test creates its own policy and claims, so tests don't depend on seed data or on each other.
 */
class ClaimApprovalIT extends AbstractIntegrationTest {

    @Autowired ClaimService service;
    @Autowired JdbcTemplate jdbc;

    // ---- helpers ----
    private long newPolicy(String coverageLimit) {
        String number = "POL-" + UUID.randomUUID().toString().substring(0, 8);
        jdbc.update("""
            INSERT INTO policy (policy_number, policyholder_id, type, premium_amount, coverage_limit,
                                start_date, end_date, status)
            VALUES (?, 1, 'AUTO', 1000, ?, '2026-01-01', '2026-12-31', 'ACTIVE')
            """, number, new BigDecimal(coverageLimit));
        return jdbc.queryForObject("SELECT id FROM policy WHERE policy_number = ?", Long.class, number);
    }

    private long newClaim(long policyId, String amount, String status) {
        String number = "CLM-" + UUID.randomUUID().toString().substring(0, 8);
        jdbc.update("""
            INSERT INTO claim (claim_number, policy_id, description, claim_amount, incident_date, status)
            VALUES (?, ?, 'IT claim', ?, '2026-06-01', ?)
            """, number, policyId, new BigDecimal(amount), status);
        return jdbc.queryForObject("SELECT id FROM claim WHERE claim_number = ?", Long.class, number);
    }

    private ApproveClaimRequest approval(String amount) {
        return new ApproveClaimRequest(new BigDecimal(amount), "approved in test");
    }

    // ---- tests ----
    @Test
    void approveUnderReviewClaimSucceedsAndIsAudited() {
        long claimId = newClaim(newPolicy("10000"), "1000", "UNDER_REVIEW");

        ClaimResponse response = service.approve(claimId, approval("800"));

        assertThat(response.status()).isEqualTo(ClaimStatus.APPROVED);
        assertThat(response.approvedAmount()).isEqualByComparingTo("800");

        // The trigger should have written one audit row for UNDER_REVIEW -> APPROVED.
        Integer auditRows = jdbc.queryForObject("""
            SELECT COUNT(*) FROM claim_audit
            WHERE claim_id = ? AND old_status = 'UNDER_REVIEW' AND new_status = 'APPROVED'
            """, Integer.class, claimId);
        assertThat(auditRows).isEqualTo(1);
    }

    @Test
    void approveFailsWhenTotalWouldExceedCoverageLimit() {
        long policyId = newPolicy("1000");
        long first = newClaim(policyId, "800", "UNDER_REVIEW");
        long second = newClaim(policyId, "500", "UNDER_REVIEW");
        service.approve(first, approval("800"));   // 800 of 1000 used

        assertThatThrownBy(() -> service.approve(second, approval("500")))   // 800 + 500 > 1000
                .isInstanceOf(BusinessRuleException.class);

        String status = jdbc.queryForObject("SELECT status FROM claim WHERE id = ?", String.class, second);
        assertThat(status).isEqualTo("UNDER_REVIEW");   // unchanged: the procedure rolled back
    }

    @Test
    void approveFailsWhenClaimIsNotUnderReview() {
        long claimId = newClaim(newPolicy("10000"), "1000", "SUBMITTED");

        assertThatThrownBy(() -> service.approve(claimId, approval("500")))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void approveFailsWhenClaimDoesNotExist() {
        assertThatThrownBy(() -> service.approve(999_999L, approval("500")))
                .isInstanceOf(NotFoundException.class);
    }
}