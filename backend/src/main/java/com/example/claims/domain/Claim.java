package com.example.claims.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class Claim {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String claimNumber;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Policy policy;
    private String description;
    private BigDecimal claimAmount;
    private LocalDate incidentDate;
    private LocalDateTime filedAt;
    @Enumerated(EnumType.STRING) private ClaimStatus status;
    private BigDecimal approvedAmount;
    private String reviewerNote;
}
