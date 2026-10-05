package com.example.claims.domain;

public enum ClaimStatus {
    SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED;

    // TODO(you): rule 6. Implement canTransitionTo(ClaimStatus next).
    // Allowed: SUBMITTED->UNDER_REVIEW, UNDER_REVIEW->APPROVED, UNDER_REVIEW->REJECTED. APPROVED/REJECTED are final.
    // Tip: a switch expression makes this 5 lines and easy to unit test.
    public boolean canTransitionTo(ClaimStatus next) {
        throw new UnsupportedOperationException("TODO(you)");
    }
}
