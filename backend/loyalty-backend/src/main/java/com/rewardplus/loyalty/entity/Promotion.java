package com.rewardplus.loyalty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Promotion entity representing marketing campaigns and promotional offers.
 * Can be targeted to specific customer segments based on various criteria.
 */
@Entity
@Table(name = "promotions", indexes = {
    @Index(name = "idx_promotion_status", columnList = "status"),
    @Index(name = "idx_promotion_dates", columnList = "start_date, end_date"),
    @Index(name = "idx_promotion_type", columnList = "promotion_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "promotion_code", nullable = false, length = 50)
    private String promotionCode;

    @Column(name = "promotion_type", nullable = false, length = 30)
    @Builder.Default
    private PromotionType promotionType = PromotionType.DISCOUNT;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private PromotionStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "bonus_points_multiplier", precision = 10, scale = 2)
    private BigDecimal bonusPointsMultiplier;

    @Column(name = "bonus_points_fixed")
    private Integer bonusPointsFixed;

    @Column(name = "minimum_purchase_amount", precision = 10, scale = 2)
    private BigDecimal minimumPurchaseAmount;

    @Column(name = "maximum_discount", precision = 10, scale = 2)
    private BigDecimal maximumDiscount;

    @Column(name = "usage_limit", nullable = false)
    @Builder.Default
    private Integer usageLimit = 0;

    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private Integer usageCount = 0;

    @Column(name = "usage_limit_per_customer", nullable = false)
    @Builder.Default
    private Integer usageLimitPerCustomer = 1;

    // Targeting criteria
    @Enumerated(EnumType.STRING)
    @Column(name = "minimum_tier", length = 20)
    private Customer.CustomerTier minimumTier;

    @Column(name = "minimum_age")
    private Integer minimumAge;

    @Column(name = "maximum_age")
    private Integer maximumAge;

    @Column(name = "target_gender", length = 100)
    private String targetGender;

    @Column(name = "target_occupation", length = 50)
    private String targetOccupation;

    @Column(name = "target_city", length = 100)
    private String targetCity;

    @Column(name = "target_state", length = 100)
    private String targetState;

    @Column(name = "target_segment_description", length = 500)
    private String targetSegmentDescription;

    @Column(name = "minimum_lifetime_spend")
    private Integer minimumLifetimeSpend;

    @Column(name = "minimum_transactions")
    private Integer minimumTransactions;

    @Column(name = "target_product_category", length = 100)
    private String targetProductCategory;

    @Column(name = "target_customer_ids", length = 255)
    private String targetCustomerIds;

    @Column(name = "exclusive_to_new_customers")
    private Boolean exclusiveToNewCustomers;

    @Column(name = "terms_and_conditions", length = 500)
    private String termsAndConditions;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "promotion_customers",
        joinColumns = @JoinColumn(name = "promotion_id"),
        inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    @Builder.Default
    private List<Customer> targetCustomers = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (promotionCode == null) {
            promotionCode = "PROMO" + System.currentTimeMillis();
        }
        if (status == null) {
            status = PromotionStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check if the promotion is currently active and valid.
     */
    public boolean isValid() {
        LocalDate today = LocalDate.now();
        
        if (status != PromotionStatus.ACTIVE) {
            return false;
        }
        if (startDate != null && today.isBefore(startDate)) {
            return false;
        }
        if (endDate != null && today.isAfter(endDate)) {
            return false;
        }
        if (usageLimit > 0 && usageCount >= usageLimit) {
            return false;
        }
        return true;
    }

    /**
     * Increment usage count.
     */
    public void incrementUsage() {
        this.usageCount++;
    }

    /**
     * Promotion type enumeration.
     */
    public enum PromotionType {
        DISCOUNT,
        BONUS_POINTS,
        DOUBLE_POINTS,
        CASHBACK,
        BUY_ONE_GET_ONE,
        FREE_SHIPPING,
        EARLY_ACCESS,
        FLASH_SALE,
        LOYALTY_BOOST,
        TIER_BONUS
    }

    /**
     * Promotion status enumeration.
     */
    public enum PromotionStatus {
        DRAFT,
        SCHEDULED,
        ACTIVE,
        PAUSED,
        EXPIRED,
        CANCELLED
    }
}

