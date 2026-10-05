package com.example.claims.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class Policy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String policyNumber;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Policyholder policyholder;
    @Enumerated(EnumType.STRING) private PolicyType type;
    private BigDecimal premiumAmount;
    private BigDecimal coverageLimit;
    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING) private PolicyStatus status;
}
