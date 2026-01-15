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
 * Customer entity representing a loyalty program member.
 * Links to transactions, points, rewards, and promotions through various relationships.
 */
@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String customerCode;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CustomerStatus status = CustomerStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CustomerTier tier;

    @Column(length = 10)
    private String gender;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 50)
    private String state;

    @Column(length = 20)
    private String postalCode;

    @Column(length = 100)
    private String country;

    @Column(length = 50)
    private String occupation;

    @Column(length = 100)
    private String company;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDate enrollmentDate;

    private LocalDate lastActivityDate;

    @Column(length = 500)
    private String preferences;

    @Column(length = 255)
    private String profileImageUrl;

    // Relationships
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private LoyaltyPoints loyaltyPoints;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "customer_rewards",
        joinColumns = @JoinColumn(name = "customer_id"),
        inverseJoinColumns = @JoinColumn(name = "reward_id")
    )
    @Builder.Default
    private List<Reward> redeemedRewards = new ArrayList<>();

    @ManyToMany(mappedBy = "targetCustomers", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Promotion> promotions = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RedemptionLog> redemptionLogs = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (enrollmentDate == null) {
            enrollmentDate = LocalDate.now();
        }
        if (customerCode == null) {
            customerCode = generateCustomerCode();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateCustomerCode() {
        return "CUST" + String.format("%06d", System.currentTimeMillis() % 1000000);
    }

    /**
     * Customer status enumeration.
     */
    public enum CustomerStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        PENDING_VERIFICATION
    }

    /**
     * Customer tier enumeration for loyalty levels.
     */
    public enum CustomerTier {
        BRONZE,
        SILVER,
        GOLD,
        PLATINUM,
        DIAMOND
    }

    /**
     * Get full name of the customer.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Calculate customer age.
     */
    public int getAge() {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    /**
     * Add a transaction to the customer.
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setCustomer(this);
    }

    /**
     * Add a redeemed reward to the customer.
     */
    public void addRedeemedReward(Reward reward) {
        redeemedRewards.add(reward);
    }
}

