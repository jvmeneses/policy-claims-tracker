package com.example.claims;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.claims.domain.ClaimStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ClaimStatusTest {

    @ParameterizedTest(name = "{0} -> {1} is allowed")
    @CsvSource({
            "SUBMITTED,UNDER_REVIEW",
            "UNDER_REVIEW,APPROVED",
            "UNDER_REVIEW,REJECTED"
    })
    void allowsDocumentedTransitions(ClaimStatus from, ClaimStatus to) {
        assertThat(from.canTransitionTo(to)).isTrue();
    }

    @ParameterizedTest(name = "{0} -> {1} is not allowed")
    @CsvSource({
            "SUBMITTED,APPROVED",
            "SUBMITTED,REJECTED",
            "SUBMITTED,SUBMITTED",
            "UNDER_REVIEW,SUBMITTED",
            "APPROVED,UNDER_REVIEW",
            "APPROVED,REJECTED",
            "REJECTED,UNDER_REVIEW",
            "REJECTED,APPROVED"
    })
    void rejectsEverythingElse(ClaimStatus from, ClaimStatus to) {
        assertThat(from.canTransitionTo(to)).isFalse();
    }
}