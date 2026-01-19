package com.rewardplus.loyalty.service;

import com.rewardplus.loyalty.dto.PromotionDTO;
import com.rewardplus.loyalty.entity.Customer;
import com.rewardplus.loyalty.entity.Promotion;
import com.rewardplus.loyalty.exception.BadRequestException;
import com.rewardplus.loyalty.exception.ResourceNotFoundException;
import com.rewardplus.loyalty.repository.CustomerRepository;
import com.rewardplus.loyalty.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Promotion business logic.
 * Handles promotion creation, targeting, and management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    /**
     * Create a new promotion.
     */
    @Transactional
    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        log.info("Creating new promotion: {}", promotionDTO.getName());

        if (promotionRepository.existsByPromotionCode(promotionDTO.getPromotionCode())) {
            throw new BadRequestException("Promotion code already exists: " + promotionDTO.getPromotionCode());
        }

        Promotion promotion = modelMapper.map(promotionDTO, Promotion.class);
        promotion.setStatus(Promotion.PromotionStatus.DRAFT);
        promotion = promotionRepository.save(promotion);

        log.info("Promotion created successfully: {} ({})", promotion.getName(), promotion.getId());
        return mapToDTO(promotion);
    }

    /**
     * Get promotion by ID.
     */
    @Transactional(readOnly = true)
    public PromotionDTO getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));
        return mapToDTO(promotion);
    }

    /**
     * Get promotion by promotion code.
     */
    @Transactional(readOnly = true)
    public PromotionDTO getPromotionByCode(String promotionCode) {
        Promotion promotion = promotionRepository.findByPromotionCode(promotionCode)
            .orElseThrow(() -> new ResourceNotFoundException("Promotion", "promotionCode", promotionCode));
        return mapToDTO(promotion);
    }

    /**
     * Get all active promotions.
     */
    @Transactional(readOnly = true)
    public List<PromotionDTO> getActivePromotions() {
        return promotionRepository.findActivePromotions()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get active promotions with pagination.
     */
    @Transactional(readOnly = true)
    public Page<PromotionDTO> getActivePromotions(Pageable pageable) {
        return promotionRepository.findActivePromotions(pageable)
            .map(this::mapToDTO);
    }

    /**
     * Get promotions by status.
     */
    @Transactional(readOnly = true)
    public List<PromotionDTO> getPromotionsByStatus(Promotion.PromotionStatus status) {
        return promotionRepository.findByStatus(status)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get promotions by type.
     */
    @Transactional(readOnly = true)
    public List<PromotionDTO> getPromotionsByType(Promotion.PromotionType type) {
        return promotionRepository.findByPromotionType(type)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get promotions for a specific customer.
     */
    @Transactional(readOnly = true)
    public List<PromotionDTO> getPromotionsForCustomer(Long customerId) {
        return promotionRepository.findPromotionsForCustomer(customerId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Search promotions by name.
     */
    @Transactional(readOnly = true)
    public Page<PromotionDTO> searchPromotions(String searchTerm, Pageable pageable) {
        return promotionRepository.searchByName(searchTerm, pageable)
            .map(this::mapToDTO);
    }

    /**
     * Get promotions expiring soon.
     */
    @Transactional(readOnly = true)
    public List<PromotionDTO> getExpiringPromotions(int daysUntilExpiry) {
        java.time.LocalDate endDate = java.time.LocalDate.now().plusDays(daysUntilExpiry);
        return promotionRepository.findExpiringSoon(endDate)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Update promotion.
     */
    @Transactional
    public PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO) {
        log.info("Updating promotion: {}", id);

        Promotion promotion = promotionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));

        // Update fields
        promotion.setName(promotionDTO.getName());
        promotion.setDescription(promotionDTO.getDescription());
        promotion.setPromotionType(promotionDTO.getPromotionType());
        promotion.setStartDate(promotionDTO.getStartDate());
        promotion.setEndDate(promotionDTO.getEndDate());
        promotion.setDiscountPercentage(promotionDTO.getDiscountPercentage());
        promotion.setDiscountAmount(promotionDTO.getDiscountAmount());
        promotion.setBonusPointsMultiplier(promotionDTO.getBonusPointsMultiplier());
        promotion.setBonusPointsFixed(promotionDTO.getBonusPointsFixed());
        promotion.setMinimumPurchaseAmount(promotionDTO.getMinimumPurchaseAmount());
        promotion.setMaximumDiscount(promotionDTO.getMaximumDiscount());
        promotion.setUsageLimit(promotionDTO.getUsageLimit());
        promotion.setUsageLimitPerCustomer(promotionDTO.getUsageLimitPerCustomer());
        promotion.setMinimumTier(promotionDTO.getMinimumTier());
        promotion.setMinimumAge(promotionDTO.getMinimumAge());
        promotion.setMaximumAge(promotionDTO.getMaximumAge());
        promotion.setTargetGender(promotionDTO.getTargetGender());
        promotion.setTargetOccupation(promotionDTO.getTargetOccupation());
        promotion.setTargetCity(promotionDTO.getTargetCity());
        promotion.setTargetState(promotionDTO.getTargetState());
        promotion.setTargetSegmentDescription(promotionDTO.getTargetSegmentDescription());
        promotion.setMinimumLifetimeSpend(promotionDTO.getMinimumLifetimeSpend());
        promotion.setMinimumTransactions(promotionDTO.getMinimumTransactions());
        promotion.setExclusiveToNewCustomers(promotionDTO.getExclusiveToNewCustomers());
        promotion.setTermsAndConditions(promotionDTO.getTermsAndConditions());
        promotion.setImageUrl(promotionDTO.getImageUrl());

        promotion = promotionRepository.save(promotion);
        log.info("Promotion updated successfully: {}", id);

        return mapToDTO(promotion);
    }

    /**
     * Update promotion status.
     */
    @Transactional
    public PromotionDTO updatePromotionStatus(Long id, Promotion.PromotionStatus status) {
        log.info("Updating promotion status: {} to {}", id, status);

        Promotion promotion = promotionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));

        promotion.setStatus(status);
        promotion = promotionRepository.save(promotion);

        return mapToDTO(promotion);
    }

    /**
     * Activate a promotion.
     */
    @Transactional
    public PromotionDTO activatePromotion(Long id) {
        log.info("Activating promotion: {}", id);
        return updatePromotionStatus(id, Promotion.PromotionStatus.ACTIVE);
    }

    /**
     * Pause a promotion.
     */
    @Transactional
    public PromotionDTO pausePromotion(Long id) {
        log.info("Pausing promotion: {}", id);
        return updatePromotionStatus(id, Promotion.PromotionStatus.PAUSED);
    }

    /**
     * Assign promotion to specific customers.
     */
    @Transactional
    public PromotionDTO assignToCustomers(Long promotionId, List<Long> customerIds) {
        log.info("Assigning promotion {} to {} customers", promotionId, customerIds.size());

        Promotion promotion = promotionRepository.findById(promotionId)
            .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", promotionId));

        List<Customer> customers = customerRepository.findByIdIn(customerIds);
        promotion.getTargetCustomers().addAll(customers);

        promotion = promotionRepository.save(promotion);
        log.info("Promotion assigned to customers successfully");

        return mapToDTO(promotion);
    }

    /**
     * Get customers matching promotion criteria.
     */
    @Transactional(readOnly = true)
    public List<Customer> getMatchingCustomers(Promotion promotion) {
        // This would implement complex segmentation logic
        // For now, return all active customers
        return customerRepository.findByStatus(Customer.CustomerStatus.ACTIVE);
    }

    /**
     * Get active promotions count.
     */
    @Transactional(readOnly = true)
    public long getActivePromotionsCount() {
        return promotionRepository.countByStatus(Promotion.PromotionStatus.ACTIVE);
    }

    /**
     * Map Promotion entity to PromotionDTO.
     */
    private PromotionDTO mapToDTO(Promotion promotion) {
        PromotionDTO dto = modelMapper.map(promotion, PromotionDTO.class);
        dto.setIsValid(promotion.isValid());
        if (promotion.getUsageLimit() != null && promotion.getUsageLimit() > 0) {
            dto.setRemainingUsage(promotion.getUsageLimit() - promotion.getUsageCount());
        }
        dto.setTargetedCustomerCount((long) promotion.getTargetCustomers().size());
        return dto;
    }
}

