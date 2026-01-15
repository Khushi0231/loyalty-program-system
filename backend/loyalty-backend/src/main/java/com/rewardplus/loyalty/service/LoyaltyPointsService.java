package com.rewardplus.loyalty.service;

import com.rewardplus.loyalty.dto.LoyaltyPointsDTO;
import com.rewardplus.loyalty.entity.Customer;
import com.rewardplus.loyalty.entity.LoyaltyPoints;
import com.rewardplus.loyalty.exception.InsufficientPointsException;
import com.rewardplus.loyalty.exception.ResourceNotFoundException;
import com.rewardplus.loyalty.repository.CustomerRepository;
import com.rewardplus.loyalty.repository.LoyaltyPointsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for LoyaltyPoints business logic.
 * Handles points balance management and operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoyaltyPointsService {

    private final LoyaltyPointsRepository loyaltyPointsRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Value("${app.points.redemption-rate:100}")
    private int pointsRedemptionRate; // Points needed per dollar redeemed

    /**
     * Get points balance for a customer.
     */
    @Transactional(readOnly = true)
    public LoyaltyPointsDTO getPointsBalance(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        LoyaltyPoints loyaltyPoints = loyaltyPointsRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("LoyaltyPoints", "customerId", customerId));

        LoyaltyPointsDTO dto = modelMapper.map(loyaltyPoints, LoyaltyPointsDTO.class);
        dto.setCustomerName(customer.getFullName());
        dto.setCustomerCode(customer.getCustomerCode());
        dto.setAvailableBalance(loyaltyPoints.getAvailableBalance());

        return dto;
    }

    /**
     * Get points balance by customer code.
     */
    @Transactional(readOnly = true)
    public LoyaltyPointsDTO getPointsBalanceByCode(String customerCode) {
        Customer customer = customerRepository.findByCustomerCode(customerCode)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "customerCode", customerCode));

        return getPointsBalance(customer.getId());
    }

    /**
     * Redeem points for a customer.
     */
    @Transactional
    public LoyaltyPointsDTO redeemPoints(Long customerId, Long pointsToRedeem, String reason) {
        log.info("Processing point redemption for customer {}: {} points", customerId, pointsToRedeem);

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        LoyaltyPoints loyaltyPoints = loyaltyPointsRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("LoyaltyPoints", "customerId", customerId));

        if (loyaltyPoints.getCurrentBalance() < pointsToRedeem) {
            throw new InsufficientPointsException(
                customerId,
                loyaltyPoints.getCurrentBalance(),
                pointsToRedeem
            );
        }

        loyaltyPoints.redeemPoints(pointsToRedeem);
        if (reason != null) {
            String existingNotes = loyaltyPoints.getNotes();
            loyaltyPoints.setNotes(existingNotes != null ? existingNotes + "; " + reason : reason);
        }

        loyaltyPoints = loyaltyPointsRepository.save(loyaltyPoints);
        log.info("Points redeemed successfully. New balance: {}", loyaltyPoints.getCurrentBalance());

        LoyaltyPointsDTO dto = modelMapper.map(loyaltyPoints, LoyaltyPointsDTO.class);
        dto.setCustomerName(customer.getFullName());
        dto.setCustomerCode(customer.getCustomerCode());
        dto.setAvailableBalance(loyaltyPoints.getAvailableBalance());

        return dto;
    }

    /**
     * Adjust points (add or deduct) for a customer.
     */
    @Transactional
    public LoyaltyPointsDTO adjustPoints(Long customerId, Long points, String reason) {
        log.info("Adjusting points for customer {}: {} points", customerId, points);

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        LoyaltyPoints loyaltyPoints = loyaltyPointsRepository.findByCustomerId(customerId)
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

        // If deducting more than available, throw exception
        if (points < 0 && Math.abs(points) > loyaltyPoints.getCurrentBalance()) {
            throw new InsufficientPointsException(
                customerId,
                loyaltyPoints.getCurrentBalance(),
                Math.abs(points)
            );
        }

        loyaltyPoints.adjustPoints(points);
        if (reason != null) {
            String existingNotes = loyaltyPoints.getNotes();
            loyaltyPoints.setNotes(existingNotes != null ? existingNotes + "; " + reason : reason);
        }

        loyaltyPoints = loyaltyPointsRepository.save(loyaltyPoints);
        log.info("Points adjusted successfully. New balance: {}", loyaltyPoints.getCurrentBalance());

        LoyaltyPointsDTO dto = modelMapper.map(loyaltyPoints, LoyaltyPointsDTO.class);
        dto.setCustomerName(customer.getFullName());
        dto.setCustomerCode(customer.getCustomerCode());
        dto.setAvailableBalance(loyaltyPoints.getAvailableBalance());

        return dto;
    }

    /**
     * Calculate dollar value of points.
     */
    @Transactional(readOnly = true)
    public Double calculatePointsValue(Long points) {
        if (points == null || points <= 0) {
            return 0.0;
        }
        return (double) points / pointsRedemptionRate;
    }

    /**
     * Calculate points required for a dollar amount.
     */
    @Transactional(readOnly = true)
    public Long calculateRequiredPoints(Double dollarAmount) {
        if (dollarAmount == null || dollarAmount <= 0) {
            return 0L;
        }
        return (long) Math.ceil(dollarAmount * pointsRedemptionRate);
    }

    /**
     * Get all customers with minimum balance.
     */
    @Transactional(readOnly = true)
    public List<LoyaltyPointsDTO> getCustomersWithMinimumBalance(Long minBalance) {
        return loyaltyPointsRepository.findByMinimumBalance(minBalance)
            .stream()
            .map(lp -> {
                LoyaltyPointsDTO dto = modelMapper.map(lp, LoyaltyPointsDTO.class);
                if (lp.getCustomer() != null) {
                    dto.setCustomerName(lp.getCustomer().getFullName());
                    dto.setCustomerCode(lp.getCustomer().getCustomerCode());
                }
                dto.setAvailableBalance(lp.getAvailableBalance());
                return dto;
            })
            .collect(Collectors.toList());
    }

    /**
     * Get total active loyalty accounts.
     */
    @Transactional(readOnly = true)
    public long getActiveLoyaltyAccountsCount() {
        return loyaltyPointsRepository.countByStatus(LoyaltyPoints.PointsStatus.ACTIVE);
    }

    /**
     * Get lifetime points for a customer.
     */
    @Transactional(readOnly = true)
    public Long getLifetimePoints(Long customerId) {
        return loyaltyPointsRepository.getLifetimePointsByCustomerId(customerId);
    }

    /**
     * Get total points redeemed by a customer.
     */
    @Transactional(readOnly = true)
    public Long getTotalRedeemedPoints(Long customerId) {
        return loyaltyPointsRepository.getTotalRedeemedByCustomerId(customerId);
    }
}

