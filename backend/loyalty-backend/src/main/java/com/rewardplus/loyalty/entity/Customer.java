package com.rewardplus.loyalty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Customer entity representing a loyalty program member.
 * Links to transactions, points, rewards, and promotions through various relationships.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_code", nullable = false, unique = true, length = 50)
    private String customerCode;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status;

    @Enumerated(EnumType.STRING)
    private CustomerTier tier;

    @Column(length = 10)
    private String gender;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 50)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(length = 100)
    private String country;

    @Column(length = 50)
    private String occupation;

    @Column(length = 100)
    private String company;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;

    @Column(columnDefinition = "TEXT")
    private String preferences;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // Relationships
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
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
    private List<Promotion> applicablePromotions = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RedemptionLog> redemptions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (enrollmentDate == null) {
            enrollmentDate = LocalDate.now();
        }
        if (status == null) {
            status = CustomerStatus.ACTIVE;
        }
        if (tier == null) {
            tier = CustomerTier.BRONZE;
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
        return "CUST" + String.format("%06d", System.currentTimeMillis() % 1000000) + 
               String.format("%04d", new Random().nextInt(10000));
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
        if (dateOfBirth == null) return 0;
        return (int) ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
    }

    /**
     * Enums for Customer Status and Tier.
     */
    public enum CustomerStatus {
        ACTIVE, INACTIVE, SUSPENDED, DELETED
    }

    public enum CustomerTier {
        BRONZE, SILVER, GOLD, PLATINUM, DIAMOND
    }
}
