package com.example.claims.service;

import static com.example.claims.dto.Dtos.*;

import com.example.claims.domain.*;
import com.example.claims.exception.BusinessRuleException;
import com.example.claims.exception.NotFoundException;
import com.example.claims.repo.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class ClaimService {
    private final ClaimRepository claims;
    private final PolicyRepository policies;
    private final ClaimAuditRepository audits;
    private final JdbcTemplate jdbc;

    @Transactional(readOnly = true)
    public ClaimResponse get(Long id) {
        return ClaimResponse.from(find(id));
    }

    @Transactional(readOnly = true)
    public List<AuditResponse> history(Long id) {
        find(id);
        return audits.findByClaimIdOrderByChangedAtAsc(id).stream().map(AuditResponse::from).toList();
    }

    /** Works unfiltered until you implement the TODO. */
    @Transactional(readOnly = true)
    public Page<ClaimResponse> search(ClaimStatus status, Long policyId, LocalDate from, LocalDate to, Pageable pageable) {
        // TODO(you): build a Specification<Claim> from the optional filters (status, policyId, filedAt/incidentDate range)
        //   Hint: Specification.where(null) is fine as a start; combine with .and(...). Use root.get("policy").get("id").
        return claims.findAll(pageable).map(ClaimResponse::from);
    }

    @Transactional
    public ClaimResponse file(FileClaimRequest req) {
        Policy policy = policies.findById(req.policyId())
            .orElseThrow(() -> new NotFoundException("Policy " + req.policyId() + " not found"));
        // TODO(you): rule 1 -> policy.getStatus() must be ACTIVE
        // TODO(you): rule 2 -> incidentDate must be within [startDate, endDate]
        // (rule 3, amount > 0, is already enforced by @Positive on the DTO)
        // Throw BusinessRuleException with a clear message for each violation.
        Claim c = new Claim();
        c.setClaimNumber("CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        c.setPolicy(policy);
        c.setDescription(req.description());
        c.setClaimAmount(req.claimAmount());
        c.setIncidentDate(req.incidentDate());
        c.setFiledAt(java.time.LocalDateTime.now());
        c.setStatus(ClaimStatus.SUBMITTED);
        return ClaimResponse.from(claims.save(c));
    }

    @Transactional
    public ClaimResponse startReview(Long id) {
        // TODO(you): load claim, check status.canTransitionTo(UNDER_REVIEW) else BusinessRuleException, set status, save.
        // (The audit row is written by the DB trigger. Don't write it here.)
        throw new UnsupportedOperationException("TODO(you)");
    }

    @Transactional
    public ClaimResponse reject(Long id, RejectClaimRequest req) {
        // TODO(you): same pattern as startReview, but set REJECTED and reviewerNote.
        throw new UnsupportedOperationException("TODO(you)");
    }

    @Transactional
    public ClaimResponse approve(Long id, ApproveClaimRequest req) {
        // TODO(you): call usp_ApproveClaim with SimpleJdbcCall (new SimpleJdbcCall(jdbc).withProcedureName("usp_ApproveClaim"))
        //   * pass claimId / approvedAmount / reviewerNote as named params matching the procedure's parameter names
        //   * catch DataAccessException; the THROW message from SQL Server is in getMostSpecificCause().getMessage()
        //     and the error number is on the SQLServerException (50002/50003/50004) -> translate to BusinessRuleException
        //   * since JPA may have cached the claim, reload it after the call (claims.findById) and return it
        throw new UnsupportedOperationException("TODO(you)");
    }

    private Claim find(Long id) {
        return claims.findById(id).orElseThrow(() -> new NotFoundException("Claim " + id + " not found"));
    }
}
