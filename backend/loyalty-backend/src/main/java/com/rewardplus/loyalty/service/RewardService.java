package com.rewardplus.loyalty.service;

import com.rewardplus.loyalty.dto.RewardDTO;
import com.rewardplus.loyalty.entity.Customer;
import com.rewardplus.loyalty.entity.LoyaltyPoints;
import com.rewardplus.loyalty.entity.Reward;
import com.rewardplus.loyalty.exception.BadRequestException;
import com.rewardplus.loyalty.exception.InsufficientPointsException;
import com.rewardplus.loyalty.exception.ResourceNotFoundException;
import com.rewardplus.loyalty.repository.CustomerRepository;
import com.rewardplus.loyalty.repository.LoyaltyPointsRepository;
import com.rewardplus.loyalty.repository.RewardRepository;
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
 * Service class for Reward business logic.
 * Handles reward management and redemption operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RewardService {

    private final RewardRepository rewardRepository;
    private final CustomerRepository customerRepository;
    private final LoyaltyPointsRepository loyaltyPointsRepository;
    private final ModelMapper modelMapper;

    /**
     * Create a new reward.
     */
    @Transactional
    public RewardDTO createReward(RewardDTO rewardDTO) {
        log.info("Creating new reward: {}", rewardDTO.getName());

        if (rewardRepository.existsByRewardCode(rewardDTO.getRewardCode())) {
            throw new BadRequestException("Reward code already exists: " + rewardDTO.getRewardCode());
        }

        Reward reward = modelMapper.map(rewardDTO, Reward.class);
        reward.setStatus(Reward.RewardStatus.ACTIVE);
        reward = rewardRepository.save(reward);

        log.info("Reward created successfully: {} ({})", reward.getName(), reward.getId());
        return mapToDTO(reward);
    }

    /**
     * Get reward by ID.
     */
    @Transactional(readOnly = true)
    public RewardDTO getRewardById(Long id) {
        Reward reward = rewardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reward", "id", id));
        return mapToDTO(reward);
    }

    /**
     * Get reward by reward code.
     */
    @Transactional(readOnly = true)
    public RewardDTO getRewardByCode(String rewardCode) {
        Reward reward = rewardRepository.findByRewardCode(rewardCode)
            .orElseThrow(() -> new ResourceNotFoundException("Reward", "rewardCode", rewardCode));
        return mapToDTO(reward);
    }

    /**
     * Get all active rewards.
     */
    @Transactional(readOnly = true)
    public List<RewardDTO> getActiveRewards() {
        return rewardRepository.findByStatus(Reward.RewardStatus.ACTIVE)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get available rewards (active, not expired, with quantity).
     */
    @Transactional(readOnly = true)
    public List<RewardDTO> getAvailableRewards() {
        return rewardRepository.findAvailableRewards()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get available rewards with pagination.
     */
    @Transactional(readOnly = true)
    public Page<RewardDTO> getAvailableRewards(Pageable pageable) {
        return rewardRepository.findAvailableRewards(pageable)
            .map(this::mapToDTO);
    }

    /**
     * Get rewards affordable with given points.
     */
    @Transactional(readOnly = true)
    public List<RewardDTO> getAffordableRewards(Long availablePoints) {
        return rewardRepository.findAffordableRewards(availablePoints)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get rewards by category.
     */
    @Transactional(readOnly = true)
    public List<RewardDTO> getRewardsByCategory(Reward.RewardCategory category) {
        return rewardRepository.findByCategory(category)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get rewards by type.
     */
    @Transactional(readOnly = true)
    public List<RewardDTO> getRewardsByType(Reward.RewardType type) {
        return rewardRepository.findByType(type)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Search rewards by name.
     */
    @Transactional(readOnly = true)
    public Page<RewardDTO> searchRewards(String searchTerm, Pageable pageable) {
        return rewardRepository.searchByName(searchTerm, pageable)
            .map(this::mapToDTO);
    }

    /**
     * Get top redeemed rewards.
     */
    @Transactional(readOnly = true)
    public List<RewardDTO> getTopRedeemedRewards(int limit) {
        return rewardRepository.findTopRedemedRewards(Pageable.ofSize(limit))
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get rewards expiring soon.
     */
    @Transactional(readOnly = true)
    public List<RewardDTO> getExpiringRewards(int daysUntilExpiry) {
        java.time.LocalDate endDate = java.time.LocalDate.now().plusDays(daysUntilExpiry);
        return rewardRepository.findExpiringSoon(endDate)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Update reward.
     */
    @Transactional
    public RewardDTO updateReward(Long id, RewardDTO rewardDTO) {
        log.info("Updating reward: {}", id);

        Reward reward = rewardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reward", "id", id));

        // Update fields
        reward.setName(rewardDTO.getName());
        reward.setDescription(rewardDTO.getDescription());
        reward.setType(rewardDTO.getType());
        reward.setCategory(rewardDTO.getCategory());
        reward.setPointsRequired(rewardDTO.getPointsRequired());
        reward.setDiscountPercentage(rewardDTO.getDiscountPercentage());
        reward.setDiscountAmount(rewardDTO.getDiscountAmount());
        reward.setCashValue(rewardDTO.getCashValue());
        reward.setQuantity(rewardDTO.getQuantity());
        reward.setQuantityPerCustomer(rewardDTO.getQuantityPerCustomer());
        reward.setStartDate(rewardDTO.getStartDate());
        reward.setExpiryDate(rewardDTO.getExpiryDate());
        reward.setTermsAndConditions(rewardDTO.getTermsAndConditions());
        reward.setImageUrl(rewardDTO.getImageUrl());
        reward.setStatus(rewardDTO.getStatus());

        reward = rewardRepository.save(reward);
        log.info("Reward updated successfully: {}", id);

        return mapToDTO(reward);
    }

    /**
     * Update reward status.
     */
    @Transactional
    public RewardDTO updateRewardStatus(Long id, Reward.RewardStatus status) {
        log.info("Updating reward status: {} to {}", id, status);

        Reward reward = rewardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reward", "id", id));

        reward.setStatus(status);
        reward = rewardRepository.save(reward);

        return mapToDTO(reward);
    }

    /**
     * Archive a reward.
     */
    @Transactional
    public RewardDTO archiveReward(Long id) {
        log.info("Archiving reward: {}", id);
        return updateRewardStatus(id, Reward.RewardStatus.ARCHIVED);
    }

    /**
     * Get active rewards count.
     */
    @Transactional(readOnly = true)
    public long getActiveRewardsCount() {
        return rewardRepository.countByStatus(Reward.RewardStatus.ACTIVE);
    }

    /**
     * Map Reward entity to RewardDTO.
     */
    private RewardDTO mapToDTO(Reward reward) {
        RewardDTO dto = modelMapper.map(reward, RewardDTO.class);
        dto.setIsAvailable(reward.isAvailable());
        dto.setRemainingQuantity(reward.getRemainingQuantity());
        return dto;
    }
}

