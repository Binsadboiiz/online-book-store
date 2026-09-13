package com.onlinebookstore.order.enums;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public static OrderStatus fromString(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return null;
        }
        for (OrderStatus status : OrderStatus.values()) {
            if (status.name().equalsIgnoreCase(statusStr.trim())) {
                return status;
            }
        }
        return null;
    }

    public boolean isTerminalState() {
        return this == DELIVERED || this == CANCELLED;
    }

    public boolean isValidTransitionTo(OrderStatus targetStatus) {
        if (targetStatus == null) {
            return false;
        }
        if (this == targetStatus) {
            return true;
        }

        switch (this) {
            case PENDING:
                return targetStatus == PROCESSING || targetStatus == CANCELLED;
            case PROCESSING:
                return targetStatus == SHIPPED || targetStatus == CANCELLED;
            case SHIPPED:
                return targetStatus == DELIVERED || targetStatus == CANCELLED;
            case DELIVERED:
            case CANCELLED:
            default:
                return false;
        }
    }
}
