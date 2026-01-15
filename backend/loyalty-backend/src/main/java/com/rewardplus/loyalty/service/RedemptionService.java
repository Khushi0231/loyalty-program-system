package com.rewardplus.loyalty.service;

import com.rewardplus.loyalty.dto.RedemptionDTO;
import com.rewardplus.loyalty.entity.Customer;
import com.rewardplus.loyalty.entity.LoyaltyPoints;
import com.rewardplus.loyalty.entity.Reward;
import com.rewardplus.loyalty.entity.RedemptionLog;
import com.rewardplus.loyalty.exception.InsufficientPointsException;
import com.rewardplus.loyalty.exception.ResourceNotFoundException;
import com.rewardplus.loyalty.repository.CustomerRepository;
import com.rewardplus.loyalty.repository.LoyaltyPointsRepository;
import com.rewardplus.loyalty.repository.RewardRepository;
import com.rewardplus.loyalty.repository.RedemptionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Reward Redemption business logic.
 * Handles reward redemption operations and tracking.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedemptionService {

    private final RedemptionLogRepository redemptionLogRepository;
    private final CustomerRepository customerRepository;
    private final RewardRepository rewardRepository;
    private final LoyaltyPointsRepository loyaltyPointsRepository;
    private final ModelMapper modelMapper;

    /**
     * Redeem a reward for a customer.
     */
    @Transactional
    public RedemptionDTO redeemReward(Long customerId, Long rewardId, RedemptionDTO redemptionDTO) {
        log.info("Processing reward redemption for customer {} and reward {}", customerId, rewardId);

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        Reward reward = rewardRepository.findById(rewardId)
            .orElseThrow(() -> new ResourceNotFoundException("Reward", "id", rewardId));

        // Check if reward is available
        if (!reward.isAvailable()) {
            throw new ResourceNotFoundException("Reward", "id", rewardId);
        }

        // Check customer points balance
        LoyaltyPoints loyaltyPoints = loyaltyPointsRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("LoyaltyPoints", "customerId", customerId));

        if (loyaltyPoints.getCurrentBalance() < reward.getPointsRequired()) {
            throw new InsufficientPointsException(
                customerId,
                loyaltyPoints.getCurrentBalance(),
                reward.getPointsRequired()
            );
        }

        // Redeem points
        loyaltyPoints.redeemPoints(reward.getPointsRequired());
        loyaltyPointsRepository.save(loyaltyPoints);

        // Create redemption log
        RedemptionLog redemptionLog = RedemptionLog.builder()
            .customer(customer)
            .reward(reward)
            .pointsRedeemed(reward.getPointsRequired())
            .status(RedemptionLog.RedemptionStatus.COMPLETED)
            .channel(redemptionDTO.getChannel() != null ? 
                redemptionDTO.getChannel() : RedemptionLog.RedemptionChannel.ONLINE)
            .redemptionDate(java.time.LocalDateTime.now())
            .storeCode(redemptionDTO.getStoreCode())
            .storeName(redemptionDTO.getStoreName())
            .processedBy(redemptionDTO.getProcessedBy())
            .notes(redemptionDTO.getNotes())
            .build();

        // Generate redemption code
        String redemptionCode = "RDM" + System.currentTimeMillis();
        redemptionLog.setRedemptionCode(redemptionCode);
        redemptionLog.setVoucherCode("VCHR" + System.currentTimeMillis());
        redemptionLog.setRedemptionUrl("/rewards/redemption/" + redemptionCode);

        redemptionLog = redemptionLogRepository.save(redemptionLog);

        // Update reward redemption count
        reward.incrementRedemptionCount();
        rewardRepository.save(reward);

        // Add reward to customer's redeemed rewards
        customer.getRedeemedRewards().add(reward);
        customerRepository.save(customer);

        log.info("Reward redeemed successfully. Redemption code: {}", redemptionCode);

        return mapToDTO(redemptionLog);
    }

    /**
     * Get redemption by ID.
     */
    @Transactional(readOnly = true)
    public RedemptionDTO getRedemptionById(Long id) {
        RedemptionLog redemptionLog = redemptionLogRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("RedemptionLog", "id", id));
        return mapToDTO(redemptionLog);
    }

    /**
     * Get redemption by redemption code.
     */
    @Transactional(readOnly = true)
    public RedemptionDTO getRedemptionByCode(String redemptionCode) {
        RedemptionLog redemptionLog = redemptionLogRepository.findByRedemptionCode(redemptionCode)
            .orElseThrow(() -> new ResourceNotFoundException("RedemptionLog", "redemptionCode", redemptionCode));
        return mapToDTO(redemptionLog);
    }

    /**
     * Get all redemptions for a customer.
     */
    @Transactional(readOnly = true)
    public Page<RedemptionDTO> getCustomerRedemptions(Long customerId, Pageable pageable) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer", "id", customerId);
        }
        return redemptionLogRepository.findByCustomerId(customerId, pageable)
            .map(this::mapToDTO);
    }

    /**
     * Get redemptions by status.
     */
    @Transactional(readOnly = true)
    public Page<RedemptionDTO> getRedemptionsByStatus(RedemptionLog.RedemptionStatus status, Pageable pageable) {
        return redemptionLogRepository.findByStatus(status, pageable)
            .map(this::mapToDTO);
    }

    /**
     * Get recent redemptions.
     */
    @Transactional(readOnly = true)
    public List<RedemptionDTO> getRecentRedemptions(int limit) {
        return redemptionLogRepository.findRecentRedemptions(Pageable.ofSize(limit))
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get redemptions by date range.
     */
    @Transactional(readOnly = true)
    public List<RedemptionDTO> getRedemptionsByDateRange(java.time.LocalDateTime startDate, 
                                                          java.time.LocalDateTime endDate) {
        return redemptionLogRepository.findByDateRange(startDate, endDate)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Mark redemption as used.
     */
    @Transactional
    public RedemptionDTO markAsUsed(Long redemptionId) {
        log.info("Marking redemption {} as used", redemptionId);

        RedemptionLog redemptionLog = redemptionLogRepository.findById(redemptionId)
            .orElseThrow(() -> new ResourceNotFoundException("RedemptionLog", "id", redemptionId));

        if (!redemptionLog.isValidForUse()) {
            throw new IllegalStateException("Redemption is not valid for use");
        }

        redemptionLog.markAsUsed();
        redemptionLog = redemptionLogRepository.save(redemptionLog);

        return mapToDTO(redemptionLog);
    }

    /**
     * Cancel a redemption.
     */
    @Transactional
    public RedemptionDTO cancelRedemption(Long redemptionId, String reason) {
        log.info("Cancelling redemption {}: {}", redemptionId, reason);

        RedemptionLog redemptionLog = redemptionLogRepository.findById(redemptionId)
            .orElseThrow(() -> new ResourceNotFoundException("RedemptionLog", "id", redemptionId));

        if (redemptionLog.getStatus() == RedemptionLog.RedemptionStatus.USED) {
            throw new IllegalStateException("Cannot cancel a redemption that has already been used");
        }

        // Refund points
        LoyaltyPoints loyaltyPoints = loyaltyPointsRepository.findByCustomerId(
            redemptionLog.getCustomer().getId())
            .orElseThrow(() -> new ResourceNotFoundException("LoyaltyPoints", "customerId", 
                redemptionLog.getCustomer().getId()));

        loyaltyPoints.addPoints(redemptionLog.getPointsRedeemed());
        loyaltyPointsRepository.save(loyaltyPoints);

        // Cancel redemption
        redemptionLog.cancel(reason);
        redemptionLog = redemptionLogRepository.save(redemptionLog);

        // Update reward redemption count
        Reward reward = redemptionLog.getReward();
        if (reward.getQuantityRedeemed() > 0) {
            reward.setQuantityRedeemed(reward.getQuantityRedeemed() - 1);
            rewardRepository.save(reward);
        }

        return mapToDTO(redemptionLog);
    }

    /**
     * Get redemption count by customer.
     */
    @Transactional(readOnly = true)
    public long getCustomerRedemptionCount(Long customerId) {
        return redemptionLogRepository.countByCustomerId(customerId);
    }

    /**
     * Get total points redeemed by customer.
     */
    @Transactional(readOnly = true)
    public Long getCustomerTotalPointsRedeemed(Long customerId) {
        return redemptionLogRepository.sumPointsRedeemedByCustomerId(customerId);
    }

    /**
     * Map RedemptionLog entity to RedemptionDTO.
     */
    private RedemptionDTO mapToDTO(RedemptionLog redemptionLog) {
        RedemptionDTO dto = modelMapper.map(redemptionLog, RedemptionDTO.class);
        if (redemptionLog.getCustomer() != null) {
            dto.setCustomerId(redemptionLog.getCustomer().getId());
            dto.setCustomerName(redemptionLog.getCustomer().getFullName());
            dto.setCustomerCode(redemptionLog.getCustomer().getCustomerCode());
        }
        if (redemptionLog.getReward() != null) {
            dto.setRewardId(redemptionLog.getReward().getId());
            dto.setRewardName(redemptionLog.getReward().getName());
            dto.setRewardCode(redemptionLog.getReward().getRewardCode());
        }
        dto.setIsExpired(redemptionLog.isExpired());
        dto.setIsValidForUse(redemptionLog.isValidForUse());
        return dto;
    }
}

