package com.example.claims.repo;

import com.example.claims.domain.ClaimAudit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimAuditRepository extends JpaRepository<ClaimAudit, Long> {
    List<ClaimAudit> findByClaimIdOrderByChangedAtAsc(Long claimId);
}
