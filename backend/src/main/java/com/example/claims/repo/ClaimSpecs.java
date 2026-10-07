package com.example.claims.repo;

import com.example.claims.domain.Claim;
import com.example.claims.domain.ClaimStatus;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Reusable filter pieces for Claim. Each returns null when its parameter is null,
 * and Specification.and(...) ignores null, so callers can chain them unconditionally.
 */
public final class ClaimSpecs {
    private ClaimSpecs() {}

    public static Specification<Claim> hasStatus(ClaimStatus status) {
        if (status == null) return null;
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Claim> forPolicy(Long policyId) {
        if (policyId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("policy").get("id"), policyId);
    }

    public static Specification<Claim> incidentOnOrAfter(LocalDate from) {
        if (from == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("incidentDate"), from);
    }

    public static Specification<Claim> incidentOnOrBefore(LocalDate to) {
        if (to == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("incidentDate"), to);
    }
}
