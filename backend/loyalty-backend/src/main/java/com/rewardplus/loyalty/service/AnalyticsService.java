package com.rewardplus.loyalty.service;

import com.rewardplus.loyalty.entity.Customer;
import com.rewardplus.loyalty.entity.Reward;
import com.rewardplus.loyalty.entity.Promotion;
import com.rewardplus.loyalty.entity.RedemptionLog;
import com.rewardplus.loyalty.entity.Transaction;
import com.rewardplus.loyalty.repository.CustomerRepository;
import com.rewardplus.loyalty.repository.RewardRepository;
import com.rewardplus.loyalty.repository.PromotionRepository;
import com.rewardplus.loyalty.repository.RedemptionLogRepository;
import com.rewardplus.loyalty.repository.TransactionRepository;
import com.rewardplus.loyalty.repository.LoyaltyPointsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for Analytics and Reporting.
 * Provides business intelligence and summary data for managers.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final LoyaltyPointsRepository loyaltyPointsRepository;
    private final RewardRepository rewardRepository;
    private final PromotionRepository promotionRepository;
    private final RedemptionLogRepository redemptionLogRepository;

    /**
     * Get overall program summary.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getProgramSummary() {
        log.info("Generating program summary");

        Map<String, Object> summary = new HashMap<>();

        // Customer metrics
        summary.put("totalCustomers", customerRepository.count());
        summary.put("activeCustomers", customerRepository.countByStatus(Customer.CustomerStatus.ACTIVE));
        summary.put("suspendedCustomers", customerRepository.countByStatus(Customer.CustomerStatus.SUSPENDED));

        // Tier distribution
        Map<String, Long> tierDistribution = new HashMap<>();
        for (Customer.CustomerTier tier : Customer.CustomerTier.values()) {
            tierDistribution.put(tier.name(), customerRepository.countByTier(tier));
        }
        summary.put("tierDistribution", tierDistribution);

        // Points metrics
        summary.put("activeLoyaltyAccounts", loyaltyPointsRepository.countByStatus(
            com.rewardplus.loyalty.entity.LoyaltyPoints.PointsStatus.ACTIVE));

        // Transaction metrics
        summary.put("totalTransactions", transactionRepository.count());
        summary.put("completedTransactions", transactionRepository.countByStatus(
            Transaction.TransactionStatus.COMPLETED));

        BigDecimal totalRevenue = transactionRepository.findAll().stream()
            .map(Transaction::getNetAmount)
            .filter(amount -> amount != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.put("totalRevenue", totalRevenue);

        // Reward metrics
        summary.put("totalRewards", rewardRepository.count());
        summary.put("activeRewards", rewardRepository.countByStatus(Reward.RewardStatus.ACTIVE));

        // Promotion metrics
        summary.put("totalPromotions", promotionRepository.count());
        summary.put("activePromotions", promotionRepository.countByStatus(Promotion.PromotionStatus.ACTIVE));

        // Redemption metrics
        summary.put("totalRedemptions", redemptionLogRepository.count());
        summary.put("completedRedemptions", redemptionLogRepository.countByStatus(
            RedemptionLog.RedemptionStatus.COMPLETED));

        return summary;
    }

    /**
     * Get customer activity summary.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCustomerActivitySummary() {
        Map<String, Object> summary = new HashMap<>();

        // New customers this month
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        summary.put("newCustomersThisMonth", 
            customerRepository.findByEnrollmentDateRange(startOfMonth, LocalDate.now()).size());

        // Customers by status
        summary.put("activeCustomers", customerRepository.countByStatus(Customer.CustomerStatus.ACTIVE));
        summary.put("inactiveCustomers", customerRepository.countByStatus(Customer.CustomerStatus.INACTIVE));

        // Top spending customers
        var topSpenders = transactionRepository.findTopSpendingCustomers(
            org.springframework.data.domain.PageRequest.of(0, 10));
        
        Map<String, BigDecimal> topSpenderMap = new HashMap<>();
        for (Object[] row : topSpenders) {
            Long customerId = (Long) row[0];
            customerRepository.findById(customerId).ifPresent(customer -> {
                topSpenderMap.put(customer.getFullName(), (BigDecimal) row[1]);
            });
        }
        summary.put("topSpenders", topSpenderMap);

        return summary;
    }

    /**
     * Get redemption trends.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getRedemptionTrends() {
        Map<String, Object> trends = new HashMap<>();

        // Total redemptions
        trends.put("totalRedemptions", redemptionLogRepository.count());

        // Redemptions by status
        Map<String, Long> statusCounts = new HashMap<>();
        for (RedemptionLog.RedemptionStatus status : RedemptionLog.RedemptionStatus.values()) {
            statusCounts.put(status.name(), redemptionLogRepository.countByStatus(status));
        }
        trends.put("byStatus", statusCounts);

        // Redemptions by channel
        Map<String, Long> channelCounts = new HashMap<>();
        for (RedemptionLog.RedemptionChannel channel : RedemptionLog.RedemptionChannel.values()) {
            channelCounts.put(channel.name(), 
                redemptionLogRepository.findByChannel(channel).size());
        }
        trends.put("byChannel", channelCounts);

        // Top redeemed rewards
        trends.put("topRewards", rewardRepository.findTopRedemedRewards(
            org.springframework.data.domain.PageRequest.of(0, 5)));

        return trends;
    }

    /**
     * Get sales analytics.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSalesAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        // Total transactions
        analytics.put("totalTransactions", transactionRepository.count());

        // Completed transactions
        analytics.put("completedTransactions", 
            transactionRepository.countByStatus(Transaction.TransactionStatus.COMPLETED));

        // Total revenue
        BigDecimal totalRevenue = transactionRepository.findAll().stream()
            .filter(t -> t.getStatus() == Transaction.TransactionStatus.COMPLETED)
            .map(Transaction::getNetAmount)
            .filter(amount -> amount != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        analytics.put("totalRevenue", totalRevenue);

        // Average transaction value
        long completedCount = transactionRepository.countByStatus(Transaction.TransactionStatus.COMPLETED);
        if (completedCount > 0) {
            analytics.put("averageTransactionValue", 
                totalRevenue.divide(BigDecimal.valueOf(completedCount), 2, java.math.RoundingMode.HALF_UP));
        } else {
            analytics.put("averageTransactionValue", BigDecimal.ZERO);
        }

        // Transactions by type
        Map<String, Long> transactionTypeCounts = new HashMap<>();
        for (Transaction.TransactionType type : Transaction.TransactionType.values()) {
            transactionTypeCounts.put(type.name(), 
                transactionRepository.findByTransactionType(type).size());
        }
        analytics.put("byTransactionType", transactionTypeCounts);

        // Recent transactions
        analytics.put("recentTransactions", transactionRepository.findRecentTransactions(
            org.springframework.data.domain.PageRequest.of(0, 10)));

        return analytics;
    }

    /**
     * Get promotion performance.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPromotionPerformance() {
        Map<String, Object> performance = new HashMap<>();

        // Total promotions
        performance.put("totalPromotions", promotionRepository.count());

        // By status
        Map<String, Long> statusCounts = new HashMap<>();
        for (Promotion.PromotionStatus status : Promotion.PromotionStatus.values()) {
            statusCounts.put(status.name(), promotionRepository.countByStatus(status));
        }
        performance.put("byStatus", statusCounts);

        // Active promotions
        performance.put("activePromotions", promotionRepository.findActivePromotions());

        // Expiring soon
        performance.put("expiringSoon", promotionRepository.findExpiringSoon(
            java.time.LocalDate.now().plusDays(7)));

        return performance;
    }

    /**
     * Get daily statistics for dashboard.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDailyStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        // Transactions today
        stats.put("transactionsToday", 
            transactionRepository.findByDateRange(startOfDay, endOfDay).size());

        // Redemptions today
        stats.put("redemptionsToday", 
            redemptionLogRepository.findByDateRange(startOfDay, endOfDay).size());

        // New customers today
        stats.put("newCustomersToday", 
            customerRepository.findByEnrollmentDateRange(LocalDate.now(), LocalDate.now()).size());

        return stats;
    }

    /**
     * Get tier progression data.
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getTierProgression() {
        Map<String, Long> progression = new HashMap<>();
        for (Customer.CustomerTier tier : Customer.CustomerTier.values()) {
            progression.put(tier.name(), customerRepository.countByTier(tier));
        }
        return progression;
    }
}

