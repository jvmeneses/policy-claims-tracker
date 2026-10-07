package com.example.claims.service;

import com.example.claims.domain.PolicyType;
import com.example.claims.dto.LossRatioRow;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class ReportService {
    private final JdbcTemplate jdbc;

    /** Reads vw_PolicyLossRatio, worst loss ratio first. */
    @Transactional(readOnly = true)
    public List<LossRatioRow> lossRatio() {
        return jdbc.query("""
                SELECT policy_id, policy_number, type, premium_amount,
                       total_approved, loss_ratio, rank_in_type
                FROM vw_PolicyLossRatio
                ORDER BY loss_ratio DESC, policy_number
                """,
            (rs, rowNum) -> new LossRatioRow(
                rs.getLong("policy_id"),
                rs.getString("policy_number"),
                PolicyType.valueOf(rs.getString("type")),
                rs.getBigDecimal("premium_amount"),
                rs.getBigDecimal("total_approved"),
                rs.getBigDecimal("loss_ratio"),
                rs.getInt("rank_in_type")));
    }
}
