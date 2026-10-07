package com.example.claims.repo;

import com.example.claims.domain.Claim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClaimRepository extends JpaRepository<Claim, Long>, JpaSpecificationExecutor<Claim> {

    @Override
    @EntityGraph(attributePaths = "policy")
    Page<Claim> findAll(Specification<Claim> spec, Pageable pageable);
}
