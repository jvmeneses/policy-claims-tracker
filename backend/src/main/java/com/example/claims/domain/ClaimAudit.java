package com.example.claims.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;

/** Read-only: rows are written by the DB trigger, never by Java. */
@Entity @Getter @Table(name = "claim_audit")
public class ClaimAudit {
    @Id private Long id;
    private Long claimId;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime changedAt;
    private String changedBy;
}
