package com.example.claims.repo;

import com.example.claims.domain.Policy;
import com.example.claims.domain.PolicyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Page<Policy> findByStatus(PolicyStatus status, Pageable pageable);
}
