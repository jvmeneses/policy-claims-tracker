package com.example.claims.service;

import static com.example.claims.dto.Dtos.*;

import com.example.claims.domain.*;
import com.example.claims.exception.BusinessRuleException;
import com.example.claims.exception.NotFoundException;
import com.example.claims.repo.*;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository policies;
    private final PolicyholderRepository holders;

    @Transactional(readOnly = true)
    public Page<PolicyResponse> list(PolicyStatus status, Pageable pageable) {
        Page<Policy> page = status == null ? policies.findAll(pageable) : policies.findByStatus(status, pageable);
        return page.map(PolicyResponse::from);
    }

    @Transactional(readOnly = true)
    public PolicyResponse get(Long id) {
        return PolicyResponse.from(find(id));
    }

    @Transactional
    public PolicyResponse create(CreatePolicyRequest req) {
        if (!req.endDate().isAfter(req.startDate())) {
            throw new BusinessRuleException("End date must be after start date.");
        }

        Policyholder holder = holders.findById(req.policyholderId())
            .orElseThrow(() -> new NotFoundException("Policyholder " + req.policyholderId() + " not found"));
        Policy p = new Policy();
        p.setPolicyNumber("POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        p.setPolicyholder(holder);
        p.setType(req.type());
        p.setPremiumAmount(req.premiumAmount());
        p.setCoverageLimit(req.coverageLimit());
        p.setStartDate(req.startDate());
        p.setEndDate(req.endDate());
        p.setStatus(PolicyStatus.ACTIVE);
        return PolicyResponse.from(policies.save(p));
    }

    Policy find(Long id) {
        return policies.findById(id).orElseThrow(() -> new NotFoundException("Policy " + id + " not found"));
    }
}
