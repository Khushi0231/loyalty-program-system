package com.rewardplus.loyalty.dto;

import com.rewardplus.loyalty.entity.RedemptionLog;
import com.rewardplus.loyalty.entity.Reward;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for RedemptionLog entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedemptionDTO {

    private Long id;

    @NotBlank(message = "Redemption code is required")
    private String redemptionCode;

    @NotNull(message = "Customer ID is required")
    private Long customerId;
    private String customerName;
    private String customerCode;

    @NotNull(message = "Reward ID is required")
    private Long rewardId;
    private String rewardName;
    private String rewardCode;

    @NotNull(message = "Points redeemed is required")
    @Positive(message = "Points redeemed must be positive")
    private Long pointsRedeemed;

    private RedemptionLog.RedemptionStatus status;
    private RedemptionLog.RedemptionChannel channel;

    private LocalDateTime redemptionDate;
    private LocalDateTime expiryDate;
    private LocalDateTime fulfillmentDate;
    private LocalDateTime usedDate;

    private String storeCode;
    private String storeName;
    private String cashierCode;
    private String processedBy;

    private String redemptionCodeGenerated;
    private String voucherCode;
    private String redemptionUrl;

    private String notes;
    private String cancellationReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed fields
    private Boolean isExpired;
    private Boolean isValidForUse;
    private String formattedValue;
}

