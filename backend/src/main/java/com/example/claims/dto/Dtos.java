package com.example.claims.dto;

import com.example.claims.domain.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class Dtos {
    private Dtos() {}

    public record CreatePolicyRequest(
        @NotNull Long policyholderId, @NotNull PolicyType type,
        @NotNull @Positive BigDecimal premiumAmount, @NotNull @Positive BigDecimal coverageLimit,
        @NotNull LocalDate startDate, @NotNull LocalDate endDate) {}

    public record PolicyResponse(Long id, String policyNumber, String holderName, PolicyType type,
        BigDecimal premiumAmount, BigDecimal coverageLimit, LocalDate startDate, LocalDate endDate, PolicyStatus status) {
        public static PolicyResponse from(Policy p) {
            return new PolicyResponse(p.getId(), p.getPolicyNumber(), p.getPolicyholder().getFullName(), p.getType(),
                p.getPremiumAmount(), p.getCoverageLimit(), p.getStartDate(), p.getEndDate(), p.getStatus());
        }
    }

    public record FileClaimRequest(
        @NotNull Long policyId, @NotBlank @Size(max = 1000) String description,
        @NotNull @Positive BigDecimal claimAmount, @NotNull LocalDate incidentDate) {}

    public record ApproveClaimRequest(@NotNull @Positive BigDecimal approvedAmount, @Size(max = 1000) String note) {}
    public record RejectClaimRequest(@NotBlank @Size(max = 1000) String note) {}

    public record ClaimResponse(Long id, String claimNumber, Long policyId, String policyNumber, String description,
        BigDecimal claimAmount, LocalDate incidentDate, LocalDateTime filedAt, ClaimStatus status,
        BigDecimal approvedAmount, String reviewerNote) {
        public static ClaimResponse from(Claim c) {
            return new ClaimResponse(c.getId(), c.getClaimNumber(), c.getPolicy().getId(), c.getPolicy().getPolicyNumber(),
                c.getDescription(), c.getClaimAmount(), c.getIncidentDate(), c.getFiledAt(), c.getStatus(),
                c.getApprovedAmount(), c.getReviewerNote());
        }
    }

    public record AuditResponse(String oldStatus, String newStatus, LocalDateTime changedAt, String changedBy) {
        public static AuditResponse from(ClaimAudit a) {
            return new AuditResponse(a.getOldStatus(), a.getNewStatus(), a.getChangedAt(), a.getChangedBy());
        }
    }
}
