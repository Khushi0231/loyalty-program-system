package com.rewardplus.loyalty.controller;

import com.rewardplus.loyalty.dto.ApiResponse;
import com.rewardplus.loyalty.dto.RedemptionDTO;
import com.rewardplus.loyalty.dto.RewardDTO;
import com.rewardplus.loyalty.entity.Reward;
import com.rewardplus.loyalty.service.RewardService;
import com.rewardplus.loyalty.service.RedemptionService;
import com.rewardplus.loyalty.service.CustomerService;
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
 * REST Controller for Reward and Redemption operations.
 */
@RestController
@RequestMapping("/v1/rewards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reward Management", description = "Reward catalog and redemption APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class RewardController {

    private final RewardService rewardService;
    private final RedemptionService redemptionService;
    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create reward", description = "Create a new reward in the catalog")
    public ResponseEntity<ApiResponse<RewardDTO>> createReward(
            @Valid @RequestBody RewardDTO rewardDTO) {
        log.info("Creating new reward: {}", rewardDTO.getName());
        RewardDTO reward = rewardService.createReward(rewardDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(reward, "Reward created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reward by ID", description = "Retrieve reward details by ID")
    public ResponseEntity<ApiResponse<RewardDTO>> getRewardById(@PathVariable Long id) {
        RewardDTO reward = rewardService.getRewardById(id);
        return ResponseEntity.ok(ApiResponse.success(reward));
    }

    @GetMapping("/code/{rewardCode}")
    @Operation(summary = "Get reward by code", description = "Retrieve reward details by reward code")
    public ResponseEntity<ApiResponse<RewardDTO>> getRewardByCode(@PathVariable String rewardCode) {
        RewardDTO reward = rewardService.getRewardByCode(rewardCode);
        return ResponseEntity.ok(ApiResponse.success(reward));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active rewards", description = "Retrieve all active rewards in the catalog")
    public ResponseEntity<ApiResponse<List<RewardDTO>>> getActiveRewards() {
        List<RewardDTO> rewards = rewardService.getActiveRewards();
        return ResponseEntity.ok(ApiResponse.success(rewards));
    }

    @GetMapping("/available")
    @Operation(summary = "Get available rewards", description = "Retrieve rewards available for redemption")
    public ResponseEntity<ApiResponse<Page<RewardDTO>>> getAvailableRewards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<RewardDTO> rewards = rewardService.getAvailableRewards(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.paginated(
            rewards.getContent(), page, size, rewards.getTotalElements()));
    }

    @GetMapping("/affordable")
    @Operation(summary = "Get affordable rewards", description = "Retrieve rewards customer can afford with their points")
    public ResponseEntity<ApiResponse<List<RewardDTO>>> getAffordableRewards(
            @RequestParam Long customerId) {
        var points = customerService.getCustomerPointsBalance(customerId);
        List<RewardDTO> rewards = rewardService.getAffordableRewards(points.getCurrentBalance());
        return ResponseEntity.ok(ApiResponse.success(rewards));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get rewards by category", description = "Retrieve rewards filtered by category")
    public ResponseEntity<ApiResponse<List<RewardDTO>>> getRewardsByCategory(
            @PathVariable Reward.RewardCategory category) {
        List<RewardDTO> rewards = rewardService.getRewardsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(rewards));
    }

    @GetMapping("/search")
    @Operation(summary = "Search rewards", description = "Search rewards by name")
    public ResponseEntity<ApiResponse<Page<RewardDTO>>> searchRewards(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<RewardDTO> rewards = rewardService.searchRewards(query, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.paginated(
            rewards.getContent(), page, size, rewards.getTotalElements()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update reward", description = "Update reward details")
    public ResponseEntity<ApiResponse<RewardDTO>> updateReward(
            @PathVariable Long id,
            @Valid @RequestBody RewardDTO rewardDTO) {
        RewardDTO reward = rewardService.updateReward(id, rewardDTO);
        return ResponseEntity.ok(ApiResponse.success(reward, "Reward updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update reward status", description = "Update reward active status")
    public ResponseEntity<ApiResponse<RewardDTO>> updateRewardStatus(
            @PathVariable Long id,
            @RequestParam Reward.RewardStatus status) {
        RewardDTO reward = rewardService.updateRewardStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(reward, "Reward status updated successfully"));
    }

    @PostMapping("/redeem")
    @Operation(summary = "Redeem reward", description = "Redeem a reward for a customer using points")
    public ResponseEntity<ApiResponse<RedemptionDTO>> redeemReward(
            @RequestParam Long customerId,
            @RequestParam Long rewardId,
            @Valid @RequestBody(required = false) RedemptionDTO redemptionDTO) {
        log.info("Processing redemption for customer {} and reward {}", customerId, rewardId);
        if (redemptionDTO == null) {
            redemptionDTO = new RedemptionDTO();
        }
        RedemptionDTO redemption = redemptionService.redeemReward(customerId, rewardId, redemptionDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(redemption, "Reward redeemed successfully. Redemption code: " + 
                redemption.getRedemptionCode()));
    }

    @GetMapping("/redemptions/customer/{customerId}")
    @Operation(summary = "Get customer redemptions", description = "Retrieve redemption history for a customer")
    public ResponseEntity<ApiResponse<Page<RedemptionDTO>>> getCustomerRedemptions(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<RedemptionDTO> redemptions = redemptionService.getCustomerRedemptions(
            customerId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.paginated(
            redemptions.getContent(), page, size, redemptions.getTotalElements()));
    }

    @GetMapping("/redemptions/code/{redemptionCode}")
    @Operation(summary = "Get redemption by code", description = "Retrieve redemption details by redemption code")
    public ResponseEntity<ApiResponse<RedemptionDTO>> getRedemptionByCode(
            @PathVariable String redemptionCode) {
        RedemptionDTO redemption = redemptionService.getRedemptionByCode(redemptionCode);
        return ResponseEntity.ok(ApiResponse.success(redemption));
    }

    @GetMapping("/redemptions/recent")
    @Operation(summary = "Get recent redemptions", description = "Retrieve most recent redemptions")
    public ResponseEntity<ApiResponse<List<RedemptionDTO>>> getRecentRedemptions(
            @RequestParam(defaultValue = "10") int limit) {
        List<RedemptionDTO> redemptions = redemptionService.getRecentRedemptions(limit);
        return ResponseEntity.ok(ApiResponse.success(redemptions));
    }

    @PostMapping("/redemptions/{id}/use")
    @Operation(summary = "Mark redemption as used", description = "Mark a redemption as used/fulfilled")
    public ResponseEntity<ApiResponse<RedemptionDTO>> markAsUsed(@PathVariable Long id) {
        RedemptionDTO redemption = redemptionService.markAsUsed(id);
        return ResponseEntity.ok(ApiResponse.success(redemption, "Redemption marked as used"));
    }

    @PostMapping("/redemptions/{id}/cancel")
    @Operation(summary = "Cancel redemption", description = "Cancel a redemption and refund points")
    public ResponseEntity<ApiResponse<RedemptionDTO>> cancelRedemption(
            @PathVariable Long id,
            @RequestParam String reason) {
        RedemptionDTO redemption = redemptionService.cancelRedemption(id, reason);
        return ResponseEntity.ok(ApiResponse.success(redemption, "Redemption cancelled and points refunded"));
    }
}

