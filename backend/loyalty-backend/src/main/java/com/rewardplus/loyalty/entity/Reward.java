package com.rewardplus.loyalty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Reward entity representing available rewards in the loyalty program.
 * Can be redeemed by customers using their loyalty points.
 */
@Entity
@Table(name = "rewards", indexes = {
    @Index(name = "idx_reward_category", columnList = "category"),
    @Index(name = "idx_reward_status", columnList = "status"),
    @Index(name = "idx_reward_expiry", columnList = "expiryDate")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String rewardCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private RewardType type = RewardType.DISCOUNT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private RewardCategory category = RewardCategory.PRODUCT;

    @Column(nullable = false)
    private Long pointsRequired;

    @Column(precision = 10, scale = 2)
    private Double discountPercentage;

    @Column(precision = 10, scale = 2)
    private Double discountAmount;

    @Column(precision = 10, scale = 2)
    private Double cashValue;

    @Column(length = 100)
    private String imageUrl;

    @Column(length = 255)
    private String termsAndConditions;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantityRedeemed = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantityPerCustomer = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RewardStatus status = RewardStatus.ACTIVE;

    private LocalDate startDate;

    private LocalDate expiryDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime publishedDate;

    @Column(length = 500)
    private String redemptionInstructions;

    @Column(length = 100)
    private String vendorName;

    @Column(length = 50)
    private String vendorCode;

    @Column(length = 50)
    private String applicableStores;

    @Column
    private Integer minimumPurchaseAmount;

    @ManyToMany(mappedBy = "redeemedRewards", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Customer> redeemedByCustomers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (rewardCode == null) {
            rewardCode = "RWD" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check if the reward is available for redemption.
     */
    public boolean isAvailable() {
        if (status != RewardStatus.ACTIVE) {
            return false;
        }
        if (quantity != null && quantity > 0 && quantityRedeemed >= quantity) {
            return false;
        }
        if (startDate != null && LocalDate.now().isBefore(startDate)) {
            return false;
        }
        if (expiryDate != null && LocalDate.now().isAfter(expiryDate)) {
            return false;
        }
        return true;
    }

    /**
     * Get remaining quantity available.
     */
    public Integer getRemainingQuantity() {
        if (quantity == null || quantity == 0) {
            return null; // Unlimited
        }
        return quantity - quantityRedeemed;
    }

    /**
     * Increment redemption count.
     */
    public void incrementRedemptionCount() {
        this.quantityRedeemed++;
    }

    /**
     * Reward type enumeration.
     */
    public enum RewardType {
        DISCOUNT,
        FREE_PRODUCT,
        CASHBACK,
        GIFT_CARD,
        EXPERIENCE,
        MERCHANDISE,
        VOUCHER
    }

    /**
     * Reward category enumeration.
     */
    public enum RewardCategory {
        PRODUCT,
        SERVICE,
        EXPERIENCE,
        GIFT,
        TRAVEL,
        ENTERTAINMENT,
        FOOD_AND_BEVERAGE
    }

    /**
     * Reward status enumeration.
     */
    public enum RewardStatus {
        ACTIVE,
        INACTIVE,
        EXPIRED,
        OUT_OF_STOCK,
        ARCHIVED
    }
}

