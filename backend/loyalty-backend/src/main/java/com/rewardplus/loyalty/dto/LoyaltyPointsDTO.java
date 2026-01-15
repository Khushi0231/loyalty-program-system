package com.rewardplus.loyalty.dto;

import com.rewardplus.loyalty.entity.LoyaltyPoints;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for LoyaltyPoints entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyPointsDTO {

    private Long id;

    private Long customerId;
    private String customerName;
    private String customerCode;

    private Long transactionId;
    private String transactionCode;

    private Long pointsEarned;
    private Long pointsRedeemed;
    private Long pointsExpired;
    private Long pointsAdjusted;

    private Long currentBalance;
    private Long lifetimePoints;
    private Long availableBalance;

    private LoyaltyPoints.PointsStatus status;

    private LocalDateTime lastEarnedDate;
    private LocalDateTime lastRedeemedDate;
    private LocalDateTime lastAdjustedDate;
    private LocalDateTime pointsExpirationDate;

    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

