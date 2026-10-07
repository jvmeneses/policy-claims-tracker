package com.example.claims.domain;

public enum ClaimStatus {
    SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED;

    /**
     * Rule 6: allowed transitions only.
     * SUBMITTED -> UNDER_REVIEW, UNDER_REVIEW -> APPROVED | REJECTED.
     * APPROVED and REJECTED are final.
     */
    public boolean canTransitionTo(ClaimStatus next) {
        return switch (this) {
            case SUBMITTED -> next == UNDER_REVIEW;
            case UNDER_REVIEW -> next == APPROVED || next == REJECTED;
            case APPROVED, REJECTED -> false;
        };
    }
}
