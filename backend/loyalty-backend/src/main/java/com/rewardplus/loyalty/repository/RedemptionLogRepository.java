package com.rewardplus.loyalty.repository;

import com.rewardplus.loyalty.entity.RedemptionLog;
import com.rewardplus.loyalty.entity.RedemptionLog.RedemptionChannel;
import com.rewardplus.loyalty.entity.RedemptionLog.RedemptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RedemptionLog entity operations.
 */
@Repository
public interface RedemptionLogRepository extends JpaRepository<RedemptionLog, Long> {

    /**
     * Find redemption by redemption code.
     */
    Optional<RedemptionLog> findByRedemptionCode(String redemptionCode);

    /**
     * Check if redemption exists by redemption code.
     */
    boolean existsByRedemptionCode(String redemptionCode);

    /**
     * Find all redemptions for a customer.
     */
    List<RedemptionLog> findByCustomerId(Long customerId);

    /**
     * Find all redemptions for a customer with pagination.
     */
    Page<RedemptionLog> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * Find all redemptions for a reward.
     */
    List<RedemptionLog> findByRewardId(Long rewardId);

    /**
     * Find redemptions by status.
     */
    List<RedemptionLog> findByStatus(RedemptionStatus status);

    /**
     * Find redemptions by status with pagination.
     */
    Page<RedemptionLog> findByStatus(RedemptionStatus status, Pageable pageable);

    /**
     * Find redemptions by channel.
     */
    List<RedemptionLog> findByChannel(RedemptionChannel channel);

    /**
     * Find redemptions within date range.
     */
    @Query("SELECT r FROM RedemptionLog r WHERE r.redemptionDate BETWEEN :startDate AND :endDate")
    List<RedemptionLog> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Find redemptions for customer within date range.
     */
    @Query("SELECT r FROM RedemptionLog r WHERE r.customer.id = :customerId " +
           "AND r.redemptionDate BETWEEN :startDate AND :endDate")
    List<RedemptionLog> findByCustomerIdAndDateRange(@Param("customerId") Long customerId,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);

    /**
     * Find expired but not used redemptions.
     */
    @Query("SELECT r FROM RedemptionLog r WHERE r.status = 'COMPLETED' " +
           "AND r.expiryDate IS NOT NULL AND r.expiryDate < CURRENT_TIMESTAMP " +
           "AND r.usedDate IS NULL")
    List<RedemptionLog> findExpiredRedemptions();

    /**
     * Find pending redemptions.
     */
    List<RedemptionLog> findByStatusOrderByRedemptionDateDesc(RedemptionStatus status);

    /**
     * Count redemptions by customer.
     */
    long countByCustomerId(Long customerId);

    /**
     * Count redemptions by status.
     */
    long countByStatus(RedemptionStatus status);

    /**
     * Sum of points redeemed by a customer.
     */
    @Query("SELECT COALESCE(SUM(r.pointsRedeemed), 0) FROM RedemptionLog r " +
           "WHERE r.customer.id = :customerId AND r.status NOT IN ('CANCELLED', 'REFUNDED')")
    Long sumPointsRedeemedByCustomerId(@Param("customerId") Long customerId);

    /**
     * Find recent redemptions.
     */
    @Query("SELECT r FROM RedemptionLog r ORDER BY r.redemptionDate DESC")
    List<RedemptionLog> findRecentRedemptions(Pageable pageable);

    /**
     * Find redemptions by store.
     */
    List<RedemptionLog> findByStoreCode(String storeCode);

    /**
     * Find redemptions by processed by user.
     */
    List<RedemptionLog> findByProcessedBy(String processedBy);

    /**
     * Find redemptions by voucher code.
     */
    Optional<RedemptionLog> findByVoucherCode(String voucherCode);
}

