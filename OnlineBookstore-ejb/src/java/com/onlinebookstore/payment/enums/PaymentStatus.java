package com.onlinebookstore.payment.enums;

public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED;

    public static PaymentStatus fromString(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return null;
        }
        String normalized = statusStr.trim().toUpperCase();
        if ("COMPLETED".equals(normalized) || "PAID".equals(normalized)) {
            return SUCCESS;
        }
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.name().equalsIgnoreCase(normalized)) {
                return status;
            }
        }
        return null;
    }

    public boolean isFinalized() {
        return this == SUCCESS || this == FAILED || this == REFUNDED;
    }

    public boolean isValidTransitionTo(PaymentStatus targetStatus) {
        if (targetStatus == null) {
            return false;
        }
        if (this == targetStatus) {
            return true;
        }

        switch (this) {
            case PENDING:
                return targetStatus == SUCCESS || targetStatus == FAILED;
            case SUCCESS:
                return targetStatus == REFUNDED;
            case FAILED:
            case REFUNDED:
            default:
                return false;
        }
    }
}
