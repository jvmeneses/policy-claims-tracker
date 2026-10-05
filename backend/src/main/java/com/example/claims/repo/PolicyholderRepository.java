package com.example.claims.repo;

import com.example.claims.domain.Policyholder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyholderRepository extends JpaRepository<Policyholder, Long> {}
