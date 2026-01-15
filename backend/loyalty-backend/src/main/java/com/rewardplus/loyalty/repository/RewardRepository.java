package com.rewardplus.loyalty.repository;

import com.rewardplus.loyalty.entity.Reward;
import com.rewardplus.loyalty.entity.Reward.RewardCategory;
import com.rewardplus.loyalty.entity.Reward.RewardStatus;
import com.rewardplus.loyalty.entity.Reward.RewardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Reward entity operations.
 */
@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {

    /**
     * Find reward by reward code.
     */
    Optional<Reward> findByRewardCode(String rewardCode);

    /**
     * Check if reward exists by reward code.
     */
    boolean existsByRewardCode(String rewardCode);

    /**
     * Find all active rewards.
     */
    List<Reward> findByStatus(RewardStatus status);

    /**
     * Find all active rewards with pagination.
     */
    Page<Reward> findByStatus(RewardStatus status, Pageable pageable);

    /**
     * Find rewards by type.
     */
    List<Reward> findByType(RewardType type);

    /**
     * Find rewards by category.
     */
    List<Reward> findByCategory(RewardCategory category);

    /**
     * Find active rewards within points range.
     */
    @Query("SELECT r FROM Reward r WHERE r.status = 'ACTIVE' " +
           "AND r.pointsRequired BETWEEN :minPoints AND :maxPoints")
    List<Reward> findByPointsRange(@Param("minPoints") Long minPoints, 
                                    @Param("maxPoints") Long maxPoints);

    /**
     * Find available rewards (active, not expired, with quantity).
     */
    @Query("SELECT r FROM Reward r WHERE r.status = 'ACTIVE' " +
           "AND (r.quantity IS NULL OR r.quantityRedeemed < r.quantity) " +
           "AND (r.startDate IS NULL OR CURRENT_DATE >= r.startDate) " +
           "AND (r.expiryDate IS NULL OR CURRENT_DATE <= r.expiryDate)")
    List<Reward> findAvailableRewards();

    /**
     * Find available rewards with pagination.
     */
    @Query("SELECT r FROM Reward r WHERE r.status = 'ACTIVE' " +
           "AND (r.quantity IS NULL OR r.quantityRedeemed < r.quantity) " +
           "AND (r.startDate IS NULL OR CURRENT_DATE >= r.startDate) " +
           "AND (r.expiryDate IS NULL OR CURRENT_DATE <= r.expiryDate)")
    Page<Reward> findAvailableRewards(Pageable pageable);

    /**
     * Find rewards affordable with given points.
     */
    @Query("SELECT r FROM Reward r WHERE r.status = 'ACTIVE' " +
           "AND r.pointsRequired <= :availablePoints " +
           "AND (r.quantity IS NULL OR r.quantityRedeemed < r.quantity)")
    List<Reward> findAffordableRewards(@Param("availablePoints") Long availablePoints);

    /**
     * Find rewards by name containing search term.
     */
    @Query("SELECT r FROM Reward r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Reward> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find top rewards by redemption count.
     */
    @Query("SELECT r FROM Reward r ORDER BY r.quantityRedeemed DESC")
    List<Reward> findTopRedemedRewards(Pageable pageable);

    /**
     * Count active rewards.
     */
    long countByStatus(RewardStatus status);

    /**
     * Find rewards expiring soon.
     */
    @Query("SELECT r FROM Reward r WHERE r.status = 'ACTIVE' " +
           "AND r.expiryDate IS NOT NULL " +
           "AND r.expiryDate BETWEEN CURRENT_DATE AND :endDate")
    List<Reward> findExpiringSoon(@Param("endDate") java.time.LocalDate endDate);
}

