package com.rewardplus.loyalty.repository;

import com.rewardplus.loyalty.entity.LoyaltyPoints;
import com.rewardplus.loyalty.entity.LoyaltyPoints.PointsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for LoyaltyPoints entity operations.
 */
@Repository
public interface LoyaltyPointsRepository extends JpaRepository<LoyaltyPoints, Long> {

    /**
     * Find loyalty points by customer ID.
     */
    Optional<LoyaltyPoints> findByCustomerId(Long customerId);

    /**
     * Check if customer has loyalty points record.
     */
    boolean existsByCustomerId(Long customerId);

    /**
     * Find loyalty points by transaction ID.
     */
    Optional<LoyaltyPoints> findByTransactionId(Long transactionId);

    /**
     * Find loyalty points by status.
     */
    Optional<LoyaltyPoints> findByCustomerIdAndStatus(Long customerId, PointsStatus status);

    /**
     * Get current balance for a customer.
     */
    @Query("SELECT COALESCE(lp.currentBalance, 0) FROM LoyaltyPoints lp WHERE lp.customer.id = :customerId")
    Long getCurrentBalanceByCustomerId(@Param("customerId") Long customerId);

    /**
     * Get lifetime points for a customer.
     */
    @Query("SELECT COALESCE(lp.lifetimePoints, 0) FROM LoyaltyPoints lp WHERE lp.customer.id = :customerId")
    Long getLifetimePointsByCustomerId(@Param("customerId") Long customerId);

    /**
     * Get total points redeemed by a customer.
     */
    @Query("SELECT COALESCE(lp.pointsRedeemed, 0) FROM LoyaltyPoints lp WHERE lp.customer.id = :customerId")
    Long getTotalRedeemedByCustomerId(@Param("customerId") Long customerId);

    /**
     * Find customers with minimum balance.
     */
    @Query("SELECT lp FROM LoyaltyPoints lp WHERE lp.currentBalance >= :minBalance")
    java.util.List<LoyaltyPoints> findByMinimumBalance(@Param("minBalance") Long minBalance);

    /**
     * Count active loyalty accounts.
     */
    long countByStatus(PointsStatus status);
}

