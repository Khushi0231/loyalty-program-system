package com.rewardplus.loyalty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * RedemptionLog entity for tracking reward redemptions.
 * Records each redemption event with all relevant details.
 */
@Entity
@Table(name = "redemption_logs", indexes = {
    @Index(name = "idx_redemption_customer", columnList = "customer_id"),
    @Index(name = "idx_redemption_reward", columnList = "reward_id"),
    @Index(name = "idx_redemption_date", columnList = "redemptionDate"),
    @Index(name = "idx_redemption_code", columnList = "redemptionCode")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedemptionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String redemptionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    @Column(nullable = false)
    private Long pointsRedeemed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RedemptionStatus status = RedemptionStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private RedemptionChannel channel;

    @Column(nullable = false)
    private LocalDateTime redemptionDate;

    private LocalDateTime expiryDate;

    private LocalDateTime fulfillmentDate;

    private LocalDateTime usedDate;

    @Column(length = 100)
    private String storeCode;

    @Column(length = 100)
    private String storeName;

    @Column(length = 50)
    private String cashierCode;

    @Column(length = 100)
    private String processedBy;

    @Column(length = 500)
    private String redemptionCodeGenerated;

    @Column(length = 255)
    private String voucherCode;

    @Column(columnDefinition = "TEXT")
    private String redemptionUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (redemptionCode == null) {
            redemptionCode = "RDM" + System.currentTimeMillis();
        }
        if (redemptionDate == null) {
            redemptionDate = LocalDateTime.now();
        }
        if (channel == null) {
            channel = RedemptionChannel.ONLINE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check if the redemption has expired.
     */
    public boolean isExpired() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

    /**
     * Check if the redemption is still valid for use.
     */
    public boolean isValidForUse() {
        return status == RedemptionStatus.COMPLETED && 
               !isExpired() && 
               usedDate == null;
    }

    /**
     * Mark as used.
     */
    public void markAsUsed() {
        this.usedDate = LocalDateTime.now();
        this.status = RedemptionStatus.USED;
    }

    /**
     * Cancel the redemption.
     */
    public void cancel(String reason) {
        this.status = RedemptionStatus.CANCELLED;
        this.cancellationReason = reason;
        this.notes = "Cancelled: " + reason;
    }

    /**
     * Redemption status enumeration.
     */
    public enum RedemptionStatus {
        PENDING,
        COMPLETED,
        USED,
        EXPIRED,
        CANCELLED,
        REFUNDED
    }

    /**
     * Redemption channel enumeration.
     */
    public enum RedemptionChannel {
        ONLINE,
        IN_STORE,
        MOBILE_APP,
        PHONE,
        KIOSK
    }
}

