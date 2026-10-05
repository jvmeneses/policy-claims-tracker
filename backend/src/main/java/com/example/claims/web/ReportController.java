package com.example.claims.web;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/reports") @RequiredArgsConstructor
public class ReportController {
    private final JdbcTemplate jdbc;

    @GetMapping("/loss-ratio")
    public List<Map<String, Object>> lossRatio() {
        // TODO(you): query vw_PolicyLossRatio. Start with jdbc.queryForList(...).
        //   Stretch: map to a typed LossRatioRow record in Dtos with a RowMapper, ordered by loss_ratio DESC.
        return List.of();
    }
}
