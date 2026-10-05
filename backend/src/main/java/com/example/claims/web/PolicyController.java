package com.example.claims.web;

import static com.example.claims.dto.Dtos.*;

import com.example.claims.domain.PolicyStatus;
import com.example.claims.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/policies") @RequiredArgsConstructor
public class PolicyController {
    private final PolicyService service;

    @GetMapping
    public Page<PolicyResponse> list(@RequestParam(required = false) PolicyStatus status, Pageable pageable) {
        return service.list(status, pageable);
    }

    @GetMapping("/{id}")
    public PolicyResponse get(@PathVariable Long id) { return service.get(id); }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public PolicyResponse create(@Valid @RequestBody CreatePolicyRequest req) { return service.create(req); }
}
