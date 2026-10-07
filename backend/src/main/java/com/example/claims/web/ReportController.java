package com.example.claims.web;

import java.util.List;
import java.util.Map;

import com.example.claims.dto.LossRatioRow;
import com.example.claims.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/reports") @RequiredArgsConstructor
public class ReportController {
    private final ReportService service;

    @GetMapping("/loss-ratio")
    public List<LossRatioRow> lossRatio() {
        return service.lossRatio();
    }
}
