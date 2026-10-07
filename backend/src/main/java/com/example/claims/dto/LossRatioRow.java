package com.example.claims.dto;

import com.example.claims.domain.PolicyType;
import java.math.BigDecimal;

/** One row of vw_PolicyLossRatio. */
public record LossRatioRow(
    Long policyId,
    String policyNumber,
    PolicyType type,
    BigDecimal premiumAmount,
    BigDecimal totalApproved,
    BigDecimal lossRatio,
    int rankInType) {}
