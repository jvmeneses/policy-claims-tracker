package com.example.claims.web;

import static com.example.claims.dto.Dtos.*;

import com.example.claims.domain.ClaimStatus;
import com.example.claims.service.ClaimService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/claims") @RequiredArgsConstructor
public class ClaimController {
    private final ClaimService service;

    /** incidentFrom / incidentTo filter on the incident date (inclusive). Default sort: newest filed first. */
    @GetMapping
    public Page<ClaimResponse> search(
            @RequestParam(required = false) ClaimStatus status,
            @RequestParam(required = false) Long policyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate incidentFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate incidentTo,
            @PageableDefault(size = 20, sort = "filedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.search(status, policyId, incidentFrom, incidentTo, pageable);
    }

    @GetMapping("/{id}") public ClaimResponse get(@PathVariable Long id) { return service.get(id); }
    @GetMapping("/{id}/history") public List<AuditResponse> history(@PathVariable Long id) { return service.history(id); }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ClaimResponse file(@Valid @RequestBody FileClaimRequest req) { return service.file(req); }

    @PostMapping("/{id}/review")
    public ClaimResponse review(@PathVariable Long id) { return service.startReview(id); }

    @PostMapping("/{id}/approve")
    public ClaimResponse approve(@PathVariable Long id, @Valid @RequestBody ApproveClaimRequest req) {
        return service.approve(id, req);
    }

    @PostMapping("/{id}/reject")
    public ClaimResponse reject(@PathVariable Long id, @Valid @RequestBody RejectClaimRequest req) {
        return service.reject(id, req);
    }
}