package com.example.claims;

import static com.example.claims.dto.Dtos.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.claims.domain.*;
import com.example.claims.exception.BusinessRuleException;
import com.example.claims.repo.ClaimAuditRepository;
import com.example.claims.repo.ClaimRepository;
import com.example.claims.repo.PolicyRepository;
import com.example.claims.service.ClaimService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock ClaimRepository claims;
    @Mock PolicyRepository policies;
    @Mock ClaimAuditRepository audits;
    @Mock JdbcTemplate jdbc;
    @InjectMocks ClaimService service;

    // ---- helpers ----
    private Policy policy(PolicyStatus status) {
        Policy p = new Policy();
        p.setId(1L);
        p.setPolicyNumber("POL-TEST");
        p.setStatus(status);
        p.setStartDate(LocalDate.of(2026, 1, 1));
        p.setEndDate(LocalDate.of(2026, 12, 31));
        return p;
    }

    private Claim claim(ClaimStatus status) {
        Claim c = new Claim();
        c.setId(5L);
        c.setClaimNumber("CLM-TEST");
        c.setPolicy(policy(PolicyStatus.ACTIVE));
        c.setStatus(status);
        return c;
    }

    private FileClaimRequest fileRequest(LocalDate incidentDate) {
        return new FileClaimRequest(1L, "Test damage", new BigDecimal("100.00"), incidentDate);
    }

    // ---- file() ----
    @Test
    void fileRejectsPolicyThatIsNotActive() {
        when(policies.findById(1L)).thenReturn(Optional.of(policy(PolicyStatus.EXPIRED)));

        assertThatThrownBy(() -> service.file(fileRequest(LocalDate.of(2026, 6, 1))))
                .isInstanceOf(BusinessRuleException.class);
        verify(claims, never()).save(any());
    }

    @Test
    void fileRejectsIncidentOutsidePolicyPeriod() {
        when(policies.findById(1L)).thenReturn(Optional.of(policy(PolicyStatus.ACTIVE)));

        assertThatThrownBy(() -> service.file(fileRequest(LocalDate.of(2027, 1, 1))))
                .isInstanceOf(BusinessRuleException.class);
        verify(claims, never()).save(any());
    }

    @Test
    void fileSavesSubmittedClaimOnHappyPath() {
        when(policies.findById(1L)).thenReturn(Optional.of(policy(PolicyStatus.ACTIVE)));
        when(claims.save(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));

        ClaimResponse response = service.file(fileRequest(LocalDate.of(2026, 6, 1)));

        ArgumentCaptor<Claim> saved = ArgumentCaptor.forClass(Claim.class);
        verify(claims).save(saved.capture());
        assertThat(saved.getValue().getStatus()).isEqualTo(ClaimStatus.SUBMITTED);
        assertThat(saved.getValue().getClaimNumber()).startsWith("CLM-");
        assertThat(response.status()).isEqualTo(ClaimStatus.SUBMITTED);
    }

    // ---- status transitions ----
    @Test
    void startReviewMovesSubmittedClaimToUnderReview() {
        Claim c = claim(ClaimStatus.SUBMITTED);
        when(claims.findById(5L)).thenReturn(Optional.of(c));
        when(claims.save(c)).thenReturn(c);

        ClaimResponse response = service.startReview(5L);

        assertThat(response.status()).isEqualTo(ClaimStatus.UNDER_REVIEW);
    }

    @Test
    void startReviewRejectsClaimThatIsAlreadyApproved() {
        when(claims.findById(5L)).thenReturn(Optional.of(claim(ClaimStatus.APPROVED)));

        assertThatThrownBy(() -> service.startReview(5L))
                .isInstanceOf(BusinessRuleException.class);
        verify(claims, never()).save(any());
    }

    @Test
    void rejectStoresReviewerNoteAndSetsRejected() {
        Claim c = claim(ClaimStatus.UNDER_REVIEW);
        when(claims.findById(5L)).thenReturn(Optional.of(c));
        when(claims.save(c)).thenReturn(c);

        ClaimResponse response = service.reject(5L, new RejectClaimRequest("Not covered"));

        assertThat(response.status()).isEqualTo(ClaimStatus.REJECTED);
        assertThat(response.reviewerNote()).isEqualTo("Not covered");
    }
}