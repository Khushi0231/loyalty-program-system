package com.rewardplus.loyalty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * LoyaltyPoints entity representing customer's loyalty point balance.
 * Linked one-to-one with customer and one-to-one with transaction.
 */
@Entity
@Table(name = "loyalty_points", indexes = {
    @Index(name = "idx_loyalty_customer", columnList = "customer_id"),
    @Index(name = "idx_loyalty_transaction", columnList = "transaction_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyPoints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Column(nullable = false)
    @Builder.Default
    private Long pointsEarned = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long pointsRedeemed = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long pointsExpired = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long pointsAdjusted = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long currentBalance = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long lifetimePoints = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PointsStatus status = PointsStatus.ACTIVE;

    @Column
    private LocalDateTime lastEarnedDate;

    @Column
    private LocalDateTime lastRedeemedDate;

    @Column
    private LocalDateTime lastAdjustedDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime pointsExpirationDate;

    @Column(length = 500)
    private String notes;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Calculate available balance (current balance minus expired points).
     */
    public Long getAvailableBalance() {
        return currentBalance - pointsExpired;
    }

    /**
     * Add points to the balance.
     */
    public void addPoints(Long points) {
        if (points > 0) {
            this.pointsEarned += points;
            this.currentBalance += points;
            this.lifetimePoints += points;
            this.lastEarnedDate = LocalDateTime.now();
        }
    }

    /**
     * Redeem points from the balance.
     */
    public void redeemPoints(Long points) {
        if (points > 0 && currentBalance >= points) {
            this.pointsRedeemed += points;
            this.currentBalance -= points;
            this.lastRedeemedDate = LocalDateTime.now();
        }
    }

    /**
     * Adjust points (can be positive or negative).
     */
    public void adjustPoints(Long points) {
        this.pointsAdjusted += Math.abs(points);
        this.currentBalance += points;
        this.lastAdjustedDate = LocalDateTime.now();
    }

    /**
     * Expire some points.
     */
    public void expirePoints(Long points) {
        if (points > 0 && (currentBalance - pointsExpired) >= points) {
            this.pointsExpired += points;
            this.currentBalance -= points;
        }
    }

    /**
     * Points status enumeration.
     */
    public enum PointsStatus {
        ACTIVE,
        FROZEN,
        EXPIRED,
        CLOSED
    }
}

