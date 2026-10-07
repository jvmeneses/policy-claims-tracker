package com.example.claims;

import static com.example.claims.dto.Dtos.CreatePolicyRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.claims.domain.PolicyType;
import com.example.claims.exception.BusinessRuleException;
import com.example.claims.repo.PolicyRepository;
import com.example.claims.repo.PolicyholderRepository;
import com.example.claims.service.PolicyService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock PolicyRepository policies;
    @Mock PolicyholderRepository holders;
    @InjectMocks PolicyService service;

    @Test
    void createRejectsEndDateThatIsNotAfterStartDate() {
        var req = new CreatePolicyRequest(1L, PolicyType.AUTO, new BigDecimal("1000"), new BigDecimal("50000"),
            LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 1));

        assertThatThrownBy(() -> service.create(req)).isInstanceOf(BusinessRuleException.class);
        verify(policies, never()).save(any());
    }
}
