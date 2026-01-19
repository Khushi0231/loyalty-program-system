package com.rewardplus.loyalty.controller;

import com.rewardplus.loyalty.dto.ApiResponse;
import com.rewardplus.loyalty.dto.CustomerDTO;
import com.rewardplus.loyalty.dto.LoyaltyPointsDTO;
import com.rewardplus.loyalty.entity.Customer;
import com.rewardplus.loyalty.service.CustomerService;
import com.rewardplus.loyalty.service.LoyaltyPointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Customer operations.
 * Provides endpoints for customer enrollment, management, and queries.
 */
@RestController
@RequestMapping("/v1/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Management", description = "Customer enrollment and management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class CustomerController {

    private final CustomerService customerService;
    private final LoyaltyPointsService loyaltyPointsService;

    /**
     * Enroll a new customer in the loyalty program.
     * POST /api/v1/customers/enroll
     */
    @PostMapping("/enroll")
    @Operation(summary = "Enroll new customer", description = "Enroll a new customer in the loyalty program with welcome bonus points")
    public ResponseEntity<ApiResponse<CustomerDTO>> enrollCustomer(
            @Valid @RequestBody CustomerDTO customerDTO) {
        log.info("Enrollment request received for email: {}", customerDTO.getEmail());
        CustomerDTO enrolledCustomer = customerService.enrollCustomer(customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(enrolledCustomer, "Customer enrolled successfully with welcome bonus"));
    }

    /**
     * Get customer by ID.
     * GET /api/v1/customers/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieve customer details by their unique ID")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerById(@PathVariable Long id) {
        CustomerDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }

    /**
     * Get customer by customer code.
     * GET /api/v1/customers/code/{customerCode}
     */
    @GetMapping("/code/{customerCode}")
    @Operation(summary = "Get customer by code", description = "Retrieve customer details by their customer code")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerByCode(
            @PathVariable String customerCode) {
        CustomerDTO customer = customerService.getCustomerByCode(customerCode);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }

    /**
     * Get customer by email.
     * GET /api/v1/customers/email/{email}
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Get customer by email", description = "Retrieve customer details by their email address")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerByEmail(
            @PathVariable String email) {
        CustomerDTO customer = customerService.getCustomerByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(customer));
    }

    /**
     * Get all customers with pagination.
     * GET /api/v1/customers
     */
    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieve all customers with pagination support")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<CustomerDTO> customers = customerService.getAllCustomers(
            PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.paginated(
            customers.getContent(), page, size, customers.getTotalElements()));
    }

    /**
     * Get customers by status.
     * GET /api/v1/customers/status/{status}
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get customers by status", description = "Retrieve customers filtered by status")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getCustomersByStatus(
            @PathVariable Customer.CustomerStatus status) {
        List<CustomerDTO> customers = customerService.getCustomersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    /**
     * Get customers by tier.
     * GET /api/v1/customers/tier/{tier}
     */
    @GetMapping("/tier/{tier}")
    @Operation(summary = "Get customers by tier", description = "Retrieve customers filtered by loyalty tier")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getCustomersByTier(
            @PathVariable Customer.CustomerTier tier) {
        List<CustomerDTO> customers = customerService.getCustomersByTier(tier);
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    /**
     * Search customers.
     * GET /api/v1/customers/search
     */
    @GetMapping("/search")
    @Operation(summary = "Search customers", description = "Search customers by name, email, or customer code")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> searchCustomers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<CustomerDTO> customers = customerService.searchCustomers(
            query, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.paginated(
            customers.getContent(), page, size, customers.getTotalElements()));
    }

    /**
     * Get customers by age range.
     * GET /api/v1/customers/age-range
     */
    @GetMapping("/age-range")
    @Operation(summary = "Get customers by age range", description = "Retrieve customers within specified age range")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getCustomersByAgeRange(
            @RequestParam int minAge,
            @RequestParam int maxAge) {
        List<CustomerDTO> customers = customerService.getCustomersByAgeRange(minAge, maxAge);
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    /**
     * Update customer information.
     * PUT /api/v1/customers/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Update customer information")
    public ResponseEntity<ApiResponse<CustomerDTO>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedCustomer, "Customer updated successfully"));
    }

    /**
     * Update customer status.
     * PATCH /api/v1/customers/{id}/status
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update customer status", description = "Update customer account status")
    public ResponseEntity<ApiResponse<CustomerDTO>> updateCustomerStatus(
            @PathVariable Long id,
            @RequestParam Customer.CustomerStatus status) {
        CustomerDTO updatedCustomer = customerService.updateCustomerStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(updatedCustomer, "Customer status updated successfully"));
    }

    /**
     * Update customer tier.
     * PATCH /api/v1/customers/{id}/tier
     */
    @PatchMapping("/{id}/tier")
    @Operation(summary = "Update customer tier", description = "Update customer loyalty tier")
    public ResponseEntity<ApiResponse<CustomerDTO>> updateCustomerTier(
            @PathVariable Long id,
            @RequestParam Customer.CustomerTier tier) {
        CustomerDTO updatedCustomer = customerService.updateCustomerTier(id, tier);
        return ResponseEntity.ok(ApiResponse.success(updatedCustomer, "Customer tier updated successfully"));
    }

    /**
     * Get customer's points balance.
     * GET /api/v1/customers/{id}/points
     */
    @GetMapping("/{id}/points")
    @Operation(summary = "Get customer points", description = "Retrieve customer's current points balance and history")
    public ResponseEntity<ApiResponse<LoyaltyPointsDTO>> getCustomerPoints(
            @PathVariable Long id) {
        LoyaltyPointsDTO points = loyaltyPointsService.getPointsBalance(id);
        return ResponseEntity.ok(ApiResponse.success(points));
    }

    /**
     * Get customer's points balance by customer code.
     * GET /api/v1/customers/code/{customerCode}/points
     */
    @GetMapping("/code/{customerCode}/points")
    @Operation(summary = "Get customer points by code", description = "Retrieve customer's points balance using customer code")
    public ResponseEntity<ApiResponse<LoyaltyPointsDTO>> getCustomerPointsByCode(
            @PathVariable String customerCode) {
        LoyaltyPointsDTO points = loyaltyPointsService.getPointsBalanceByCode(customerCode);
        return ResponseEntity.ok(ApiResponse.success(points));
    }

    /**
     * Get inactive customers.
     * GET /api/v1/customers/inactive
     */
    @GetMapping("/inactive")
    @Operation(summary = "Get inactive customers", description = "Retrieve customers who have been inactive for specified days")
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getInactiveCustomers(
            @RequestParam(defaultValue = "30") int daysInactive) {
        List<CustomerDTO> customers = customerService.getInactiveCustomers(daysInactive);
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    /**
     * Get customer count by status.
     * GET /api/v1/customers/count/status/{status}
     */
    @GetMapping("/count/status/{status}")
    @Operation(summary = "Get customer count", description = "Get count of customers by status")
    public ResponseEntity<ApiResponse<Long>> getCustomerCount(
            @PathVariable Customer.CustomerStatus status) {
        long count = customerService.getCustomerCountByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}

