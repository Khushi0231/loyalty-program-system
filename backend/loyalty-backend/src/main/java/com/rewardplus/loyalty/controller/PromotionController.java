package com.rewardplus.loyalty.controller;

import com.rewardplus.loyalty.dto.ApiResponse;
import com.rewardplus.loyalty.dto.PromotionDTO;
import com.rewardplus.loyalty.entity.Promotion;
import com.rewardplus.loyalty.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Promotion operations.
 * Provides endpoints for marketing campaign management.
 */
@RestController
@RequestMapping("/v1/promotions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Promotion Management", description = "Marketing promotion and campaign APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    @Operation(summary = "Create promotion", description = "Create a new marketing promotion")
    public ResponseEntity<ApiResponse<PromotionDTO>> createPromotion(
            @Valid @RequestBody PromotionDTO promotionDTO) {
        log.info("Creating new promotion: {}", promotionDTO.getName());
        PromotionDTO promotion = promotionService.createPromotion(promotionDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(promotion, "Promotion created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get promotion by ID", description = "Retrieve promotion details by ID")
    public ResponseEntity<ApiResponse<PromotionDTO>> getPromotionById(@PathVariable Long id) {
        PromotionDTO promotion = promotionService.getPromotionById(id);
        return ResponseEntity.ok(ApiResponse.success(promotion));
    }

    @GetMapping("/code/{promotionCode}")
    @Operation(summary = "Get promotion by code", description = "Retrieve promotion details by promotion code")
    public ResponseEntity<ApiResponse<PromotionDTO>> getPromotionByCode(
            @PathVariable String promotionCode) {
        PromotionDTO promotion = promotionService.getPromotionByCode(promotionCode);
        return ResponseEntity.ok(ApiResponse.success(promotion));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active promotions", description = "Retrieve all currently active promotions")
    public ResponseEntity<ApiResponse<List<PromotionDTO>>> getActivePromotions() {
        List<PromotionDTO> promotions = promotionService.getActivePromotions();
        return ResponseEntity.ok(ApiResponse.success(promotions));
    }

    @GetMapping("/active/paged")
    @Operation(summary = "Get active promotions", description = "Retrieve active promotions with pagination")
    public ResponseEntity<ApiResponse<List<PromotionDTO>>> getActivePromotionsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PromotionDTO> promotions = promotionService.getActivePromotions(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.paginated(
            promotions.getContent(), page, size, promotions.getTotalElements()));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get promotions by status", description = "Retrieve promotions filtered by status")
    public ResponseEntity<ApiResponse<List<PromotionDTO>>> getPromotionsByStatus(
            @PathVariable Promotion.PromotionStatus status) {
        List<PromotionDTO> promotions = promotionService.getPromotionsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(promotions));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get promotions by type", description = "Retrieve promotions filtered by type")
    public ResponseEntity<ApiResponse<List<PromotionDTO>>> getPromotionsByType(
            @PathVariable Promotion.PromotionType type) {
        List<PromotionDTO> promotions = promotionService.getPromotionsByType(type);
        return ResponseEntity.ok(ApiResponse.success(promotions));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get promotions for customer", description = "Retrieve promotions applicable to a specific customer")
    public ResponseEntity<ApiResponse<List<PromotionDTO>>> getPromotionsForCustomer(
            @PathVariable Long customerId) {
        List<PromotionDTO> promotions = promotionService.getPromotionsForCustomer(customerId);
        return ResponseEntity.ok(ApiResponse.success(promotions));
    }

    @GetMapping("/search")
    @Operation(summary = "Search promotions", description = "Search promotions by name")
    public ResponseEntity<ApiResponse<List<PromotionDTO>>> searchPromotions(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PromotionDTO> promotions = promotionService.searchPromotions(query, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.paginated(
            promotions.getContent(), page, size, promotions.getTotalElements()));
    }

    @GetMapping("/expiring")
    @Operation(summary = "Get expiring promotions", description = "Retrieve promotions expiring soon")
    public ResponseEntity<ApiResponse<List<PromotionDTO>>> getExpiringPromotions(
            @RequestParam(defaultValue = "7") int daysUntilExpiry) {
        List<PromotionDTO> promotions = promotionService.getExpiringPromotions(daysUntilExpiry);
        return ResponseEntity.ok(ApiResponse.success(promotions));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update promotion", description = "Update promotion details")
    public ResponseEntity<ApiResponse<PromotionDTO>> updatePromotion(
            @PathVariable Long id,
            @Valid @RequestBody PromotionDTO promotionDTO) {
        PromotionDTO promotion = promotionService.updatePromotion(id, promotionDTO);
        return ResponseEntity.ok(ApiResponse.success(promotion, "Promotion updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update promotion status", description = "Update promotion status (draft, active, paused, etc.)")
    public ResponseEntity<ApiResponse<PromotionDTO>> updatePromotionStatus(
            @PathVariable Long id,
            @RequestParam Promotion.PromotionStatus status) {
        PromotionDTO promotion = promotionService.updatePromotionStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(promotion, "Promotion status updated successfully"));
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate promotion", description = "Activate a draft or paused promotion")
    public ResponseEntity<ApiResponse<PromotionDTO>> activatePromotion(@PathVariable Long id) {
        PromotionDTO promotion = promotionService.activatePromotion(id);
        return ResponseEntity.ok(ApiResponse.success(promotion, "Promotion activated successfully"));
    }

    @PostMapping("/{id}/pause")
    @Operation(summary = "Pause promotion", description = "Pause an active promotion")
    public ResponseEntity<ApiResponse<PromotionDTO>> pausePromotion(@PathVariable Long id) {
        PromotionDTO promotion = promotionService.pausePromotion(id);
        return ResponseEntity.ok(ApiResponse.success(promotion, "Promotion paused successfully"));
    }

    @PostMapping("/{id}/assign")
    @Operation(summary = "Assign promotion to customers", description = "Assign promotion to specific customers")
    public ResponseEntity<ApiResponse<PromotionDTO>> assignToCustomers(
            @PathVariable Long id,
            @RequestParam List<Long> customerIds) {
        PromotionDTO promotion = promotionService.assignToCustomers(id, customerIds);
        return ResponseEntity.ok(ApiResponse.success(promotion, 
            "Promotion assigned to " + customerIds.size() + " customers"));
    }

    @GetMapping("/count/active")
    @Operation(summary = "Get active promotions count", description = "Get count of active promotions")
    public ResponseEntity<ApiResponse<Long>> getActivePromotionsCount() {
        long count = promotionService.getActivePromotionsCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}

