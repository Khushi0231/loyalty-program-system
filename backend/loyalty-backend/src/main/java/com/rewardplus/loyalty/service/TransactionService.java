package com.rewardplus.loyalty.service;

import com.rewardplus.loyalty.dto.TransactionDTO;
import com.rewardplus.loyalty.entity.Customer;
import com.rewardplus.loyalty.entity.LoyaltyPoints;
import com.rewardplus.loyalty.entity.Promotion;
import com.rewardplus.loyalty.entity.Transaction;
import com.rewardplus.loyalty.exception.ResourceNotFoundException;
import com.rewardplus.loyalty.repository.CustomerRepository;
import com.rewardplus.loyalty.repository.LoyaltyPointsRepository;
import com.rewardplus.loyalty.repository.PromotionRepository;
import com.rewardplus.loyalty.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Transaction business logic.
 * Handles purchase recording and automatic points calculation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final LoyaltyPointsRepository loyaltyPointsRepository;
    private final PromotionRepository promotionRepository;
    private final ModelMapper modelMapper;

    @Value("${app.points.earn-rate:10}")
    private int pointsEarnRate; // Points per dollar

    /**
     * Record a new purchase transaction and calculate loyalty points.
     */
    @Transactional
    public TransactionDTO recordTransaction(Long customerId, TransactionDTO transactionDTO) {
        log.info("Recording transaction for customer ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        // Create transaction
        Transaction transaction = modelMapper.map(transactionDTO, Transaction.class);
        transaction.setCustomer(customer);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);

        // Calculate net amount
        if (transaction.getAmount() != null && transaction.getDiscountApplied() != null) {
            transaction.setNetAmount(transaction.getAmount().subtract(transaction.getDiscountApplied()));
        } else {
            transaction.setNetAmount(transaction.getAmount());
        }

        // Save transaction
        transaction = transactionRepository.save(transaction);

        // Calculate and award loyalty points
        Long pointsEarned = calculatePoints(transaction.getNetAmount(), customer);
        awardPoints(customer, transaction, pointsEarned);

        // Update customer's last activity date
        customer.setLastActivityDate(LocalDate.now());
        customerRepository.save(customer);

        log.info("Transaction recorded successfully: {} with {} points earned",
            transaction.getTransactionCode(), pointsEarned);

        TransactionDTO result = mapToDTO(transaction);
        result.setPointsEarned(pointsEarned);
        result.setEligibleForPoints(true);

        return result;
    }

    /**
     * Calculate points based on transaction amount and applicable promotions.
     */
    private Long calculatePoints(BigDecimal netAmount, Customer customer) {
        if (netAmount == null || netAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0L;
        }

        // Base points calculation (amount / 1) * earn rate
        long basePoints = netAmount.divide(BigDecimal.ONE, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(pointsEarnRate))
            .longValue();

        // Check for applicable promotions
        Promotion activePromotion = findApplicablePromotion(customer, netAmount);
        if (activePromotion != null) {
            if (activePromotion.getBonusPointsMultiplier() != null) {
                basePoints = (long) (basePoints * activePromotion.getBonusPointsMultiplier());
            }
            if (activePromotion.getBonusPointsFixed() != null) {
                basePoints += activePromotion.getBonusPointsFixed();
            }
            activePromotion.incrementUsage();
            promotionRepository.save(activePromotion);
        }

        return basePoints;
    }

    /**
     * Find applicable promotion for the customer.
     */
    private Promotion findApplicablePromotion(Customer customer, BigDecimal amount) {
        List<Promotion> activePromotions = promotionRepository.findActivePromotions();

        return activePromotions.stream()
            .filter(promo -> isPromotionApplicable(promo, customer, amount))
            .findFirst()
            .orElse(null);
    }

    /**
     * Check if a promotion is applicable to the customer.
     */
    private boolean isPromotionApplicable(Promotion promo, Customer customer, BigDecimal amount) {
        // Check minimum purchase amount
        if (promo.getMinimumPurchaseAmount() != null && 
            amount.compareTo(BigDecimal.valueOf(promo.getMinimumPurchaseAmount())) < 0) {
            return false;
        }

        // Check tier requirement
        if (promo.getMinimumTier() != null) {
            if (customer.getTier() == null) {
                return false;
            }
            int customerTierValue = getTierValue(customer.getTier());
            int promoTierValue = getTierValue(promo.getMinimumTier());
            if (customerTierValue < promoTierValue) {
                return false;
            }
        }

        // Check age range
        if (promo.getMinimumAge() != null && customer.getAge() < promo.getMinimumAge()) {
            return false;
        }
        if (promo.getMaximumAge() != null && customer.getAge() > promo.getMaximumAge()) {
            return false;
        }

        // Check gender
        if (promo.getTargetGender() != null && 
            !promo.getTargetGender().equalsIgnoreCase(customer.getGender())) {
            return false;
        }

        // Check city
        if (promo.getTargetCity() != null && 
            !promo.getTargetCity().equalsIgnoreCase(customer.getCity())) {
            return false;
        }

        // Check for new customer exclusivity
        if (Boolean.TRUE.equals(promo.getExclusiveToNewCustomers())) {
            // Could check enrollment date or transaction count
            return false;
        }

        return true;
    }

    /**
     * Get tier numeric value for comparison.
     */
    private int getTierValue(Customer.CustomerTier tier) {
        return switch (tier) {
            case BRONZE -> 1;
            case SILVER -> 2;
            case GOLD -> 3;
            case PLATINUM -> 4;
            case DIAMOND -> 5;
        };
    }

    /**
     * Award points to customer.
     */
    private void awardPoints(Customer customer, Transaction transaction, Long points) {
        LoyaltyPoints loyaltyPoints = loyaltyPointsRepository.findByCustomerId(customer.getId())
            .orElseGet(() -> {
                LoyaltyPoints newPoints = LoyaltyPoints.builder()
                    .customer(customer)
                    .currentBalance(0L)
                    .lifetimePoints(0L)
                    .pointsEarned(0L)
                    .pointsRedeemed(0L)
                    .pointsAdjusted(0L)
                    .status(LoyaltyPoints.PointsStatus.ACTIVE)
                    .build();
                return loyaltyPointsRepository.save(newPoints);
            });

        loyaltyPoints.addPoints(points);
        loyaltyPoints.setTransaction(transaction);
        loyaltyPointsRepository.save(loyaltyPoints);

        log.info("Awarded {} points to customer {}", points, customer.getId());
    }

    /**
     * Get transaction by ID.
     */
    @Transactional(readOnly = true)
    public TransactionDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
        return mapToDTO(transaction);
    }

    /**
     * Get transaction by transaction code.
     */
    @Transactional(readOnly = true)
    public TransactionDTO getTransactionByCode(String transactionCode) {
        Transaction transaction = transactionRepository.findByTransactionCode(transactionCode)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction", "transactionCode", transactionCode));
        return mapToDTO(transaction);
    }

    /**
     * Get all transactions for a customer.
     */
    @Transactional(readOnly = true)
    public Page<TransactionDTO> getCustomerTransactions(Long customerId, Pageable pageable) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer", "id", customerId);
        }
        return transactionRepository.findByCustomerId(customerId, pageable)
            .map(this::mapToDTO);
    }

    /**
     * Get recent transactions.
     */
    @Transactional(readOnly = true)
    public List<TransactionDTO> getRecentTransactions(int limit) {
        return transactionRepository.findRecentTransactions(Pageable.ofSize(limit))
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get transactions within date range.
     */
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByDateRange(startDate, endDate)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get customer transaction count.
     */
    @Transactional(readOnly = true)
    public long getCustomerTransactionCount(Long customerId) {
        return transactionRepository.countByCustomerId(customerId);
    }

    /**
     * Get customer total spending.
     */
    @Transactional(readOnly = true)
    public BigDecimal getCustomerTotalSpending(Long customerId) {
        return transactionRepository.sumNetAmountByCustomerId(customerId);
    }

    /**
     * Map Transaction entity to TransactionDTO.
     */
    private TransactionDTO mapToDTO(Transaction transaction) {
        TransactionDTO dto = modelMapper.map(transaction, TransactionDTO.class);
        if (transaction.getCustomer() != null) {
            dto.setCustomerId(transaction.getCustomer().getId());
            dto.setCustomerName(transaction.getCustomer().getFullName());
            dto.setCustomerCode(transaction.getCustomer().getCustomerCode());
        }
        return dto;
    }
}

