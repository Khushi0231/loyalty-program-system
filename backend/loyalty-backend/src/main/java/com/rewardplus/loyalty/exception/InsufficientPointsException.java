package com.rewardplus.loyalty.exception;

/**
 * Exception thrown when customer doesn't have enough points for an operation.
 * HTTP Status: 400 Bad Request
 */
public class InsufficientPointsException extends RuntimeException {

    private final Long customerId;
    private final Long availablePoints;
    private final Long requiredPoints;

    public InsufficientPointsException(Long customerId, Long availablePoints, Long requiredPoints) {
        super(String.format("Customer %d has insufficient points. Available: %d, Required: %d",
            customerId, availablePoints, requiredPoints));
        this.customerId = customerId;
        this.availablePoints = availablePoints;
        this.requiredPoints = requiredPoints;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getAvailablePoints() {
        return availablePoints;
    }

    public Long getRequiredPoints() {
        return requiredPoints;
    }
}

