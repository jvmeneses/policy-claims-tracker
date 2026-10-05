package com.example.claims;

import com.example.claims.domain.ClaimStatus;
import org.junit.jupiter.api.Test;

class ClaimStatusTest {
    @Test
    void allowsOnlyDocumentedTransitions() {
        // TODO(you): assert every allowed transition is true, and a few illegal ones are false
        //   (e.g. SUBMITTED->APPROVED, APPROVED->REJECTED, REJECTED->UNDER_REVIEW).
    }
}
