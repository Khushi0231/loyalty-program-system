package com.rewardplus.loyalty.controller;

import com.rewardplus.loyalty.dto.ApiResponse;
import com.rewardplus.loyalty.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for Analytics and Reporting.
 * Provides endpoints for manager-level analytics and summaries.
 */
@RestController
@RequestMapping("/v1/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics & Reports", description = "Manager analytics and business intelligence APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    @Operation(summary = "Get program summary", description = "Get overall loyalty program summary and metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProgramSummary() {
        log.info("Generating program summary");
        Map<String, Object> summary = analyticsService.getProgramSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/customers")
    @Operation(summary = "Get customer activity", description = "Get customer activity summary and trends")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCustomerActivitySummary() {
        Map<String, Object> summary = analyticsService.getCustomerActivitySummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/redemptions")
    @Operation(summary = "Get redemption trends", description = "Get redemption analytics and trends")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRedemptionTrends() {
        Map<String, Object> trends = analyticsService.getRedemptionTrends();
        return ResponseEntity.ok(ApiResponse.success(trends));
    }

    @GetMapping("/sales")
    @Operation(summary = "Get sales analytics", description = "Get sales and transaction analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSalesAnalytics() {
        Map<String, Object> analytics = analyticsService.getSalesAnalytics();
        return ResponseEntity.ok(ApiResponse.success(analytics));
    }

    @GetMapping("/promotions")
    @Operation(summary = "Get promotion performance", description = "Get promotion performance metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPromotionPerformance() {
        Map<String, Object> performance = analyticsService.getPromotionPerformance();
        return ResponseEntity.ok(ApiResponse.success(performance));
    }

    @GetMapping("/daily")
    @Operation(summary = "Get daily statistics", description = "Get daily statistics for dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDailyStats() {
        Map<String, Object> stats = analyticsService.getDailyStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/tiers")
    @Operation(summary = "Get tier distribution", description = "Get customer tier distribution data")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getTierDistribution() {
        Map<String, Long> distribution = analyticsService.getTierProgression();
        return ResponseEntity.ok(ApiResponse.success(distribution));
    }
}

