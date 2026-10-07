package com.example.claims.service;

import static com.example.claims.dto.Dtos.*;
import static com.example.claims.repo.ClaimSpecs.*;

import com.example.claims.domain.*;
import com.example.claims.exception.BusinessRuleException;
import com.example.claims.exception.NotFoundException;
import com.example.claims.repo.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class ClaimService {
    private final ClaimRepository claims;
    private final PolicyRepository policies;
    private final ClaimAuditRepository audits;
    private final JdbcTemplate jdbc;

    private SimpleJdbcCall approveClaimCall;

    @PostConstruct
    void init() {
        approveClaimCall = new SimpleJdbcCall(jdbc)
                .withSchemaName("dbo")
                .withProcedureName("usp_ApproveClaim");
    }

    @Transactional(readOnly = true)
    public ClaimResponse get(Long id) {
        return ClaimResponse.from(find(id));
    }

    @Transactional(readOnly = true)
    public List<AuditResponse> history(Long id) {
        find(id);
        return audits.findByClaimIdOrderByChangedAtAsc(id).stream().map(AuditResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Page<ClaimResponse> search(ClaimStatus status, Long policyId, LocalDate incidentFrom, LocalDate incidentTo, Pageable pageable) {
        if (incidentFrom != null && incidentTo != null && incidentFrom.isAfter(incidentTo)) {
            throw new BusinessRuleException("incidentFrom must not be after incidentTo.");
        }
        Specification<Claim> spec = Specification.where(hasStatus(status))
                .and(forPolicy(policyId))
                .and(incidentOnOrAfter(incidentFrom))
                .and(incidentOnOrBefore(incidentTo));
        return claims.findAll(spec, pageable).map(ClaimResponse::from);
    }

    @Transactional
    public ClaimResponse file(FileClaimRequest req) {
        Policy policy = policies.findById(req.policyId())
                .orElseThrow(() -> new NotFoundException("Policy " + req.policyId() + " not found"));

        // Rule 1: only ACTIVE policies can have claims filed against them.
        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new BusinessRuleException("Policy " + policy.getPolicyNumber()
                    + " is " + policy.getStatus() + "; claims can only be filed against ACTIVE policies.");
        }

        // Rule 2: the incident must fall inside the policy period (inclusive).
        LocalDate incident = req.incidentDate();
        if (incident.isBefore(policy.getStartDate()) || incident.isAfter(policy.getEndDate())) {
            throw new BusinessRuleException("Incident date " + incident + " is outside the policy period ("
                    + policy.getStartDate() + " to " + policy.getEndDate() + ").");
        }
        // Rule 3 (amount > 0) is enforced by @Positive on the DTO.

        Claim c = new Claim();
        c.setClaimNumber("CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        c.setPolicy(policy);
        c.setDescription(req.description());
        c.setClaimAmount(req.claimAmount());
        c.setIncidentDate(req.incidentDate());
        c.setFiledAt(LocalDateTime.now());
        c.setStatus(ClaimStatus.SUBMITTED);
        return ClaimResponse.from(claims.save(c));
    }

    @Transactional
    public ClaimResponse startReview(Long id) {
        Claim claim = transition(id, ClaimStatus.UNDER_REVIEW);
        return ClaimResponse.from(claims.save(claim));
    }

    @Transactional
    public ClaimResponse reject(Long id, RejectClaimRequest req) {
        Claim claim = transition(id, ClaimStatus.REJECTED);
        claim.setReviewerNote(req.note());
        return ClaimResponse.from(claims.save(claim));
    }

    @Transactional
    public ClaimResponse approve(Long id, ApproveClaimRequest req) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ClaimId", id)
                .addValue("ApprovedAmount", req.approvedAmount())
                .addValue("ReviewerNote", req.note());

        try {
            approveClaimCall.execute(params);
        } catch (DataAccessException e) {
            throw translate(e);
        }
        // The procedure changed the row behind JPA's back. The claim was not loaded earlier in
        // this transaction, so this query reads the fresh row instead of a stale cached entity.
        return ClaimResponse.from(find(id));
    }

    private RuntimeException translate(DataAccessException e) {
        Throwable root = NestedExceptionUtils.getMostSpecificCause(e);
        if (root instanceof SQLException sql) {
            return switch (sql.getErrorCode()) {
                case 50001 -> new NotFoundException(sql.getMessage());
                case 50002, 50003, 50004 -> new BusinessRuleException(sql.getMessage());
                default -> e;
            };
        }
        return e;
    }

    /** Loads the claim and moves it to {@code next}, enforcing rule 6. The caller saves it. */
    private Claim transition(Long id, ClaimStatus next) {
        Claim claim = find(id);
        if (!claim.getStatus().canTransitionTo(next)) {
            throw new BusinessRuleException("Claim " + claim.getClaimNumber()
                    + " cannot move from " + claim.getStatus() + " to " + next + ".");
        }
        claim.setStatus(next);
        return claim;
    }

    private Claim find(Long id) {
        return claims.findById(id).orElseThrow(() -> new NotFoundException("Claim " + id + " not found"));
    }
}