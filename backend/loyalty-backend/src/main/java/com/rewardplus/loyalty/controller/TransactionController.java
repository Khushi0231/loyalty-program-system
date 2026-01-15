package com.rewardplus.loyalty.controller;

import com.rewardplus.loyalty.dto.ApiResponse;
import com.rewardplus.loyalty.dto.TransactionDTO;
import com.rewardplus.loyalty.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transaction Management", description = "Purchase transaction and point earning APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Record transaction", description = "Record a new purchase transaction and earn loyalty points")
    public ResponseEntity<ApiResponse<TransactionDTO>> recordTransaction(
            @RequestParam Long customerId,
            @Valid @RequestBody TransactionDTO transactionDTO) {
        log.info("Recording transaction for customer: {}", customerId);
        TransactionDTO transaction = transactionService.recordTransaction(customerId, transactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(transaction, "Transaction recorded with " + 
                transaction.getPointsEarned() + " points earned"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID", description = "Retrieve transaction details by ID")
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionById(@PathVariable Long id) {
        TransactionDTO transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(ApiResponse.success(transaction));
    }

    @GetMapping("/code/{transactionCode}")
    @Operation(summary = "Get transaction by code", description = "Retrieve transaction by transaction code")
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionByCode(
            @PathVariable String transactionCode) {
        TransactionDTO transaction = transactionService.getTransactionByCode(transactionCode);
        return ResponseEntity.ok(ApiResponse.success(transaction));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get customer transactions", description = "Retrieve all transactions for a customer")
    public ResponseEntity<ApiResponse<Page<TransactionDTO>>> getCustomerTransactions(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<TransactionDTO> transactions = transactionService.getCustomerTransactions(
            customerId, PageRequest.of(page, size, sort));
        return ResponseEntity.ok(ApiResponse.paginated(
            transactions.getContent(), page, size, transactions.getTotalElements()));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent transactions", description = "Retrieve most recent transactions")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getRecentTransactions(
            @RequestParam(defaultValue = "10") int limit) {
        List<TransactionDTO> transactions = transactionService.getRecentTransactions(limit);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get transactions by date range", description = "Retrieve transactions within specified date range")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat LocalDateTime startDate,
            @RequestParam @DateTimeFormat LocalDateTime endDate) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @GetMapping("/customer/{customerId}/count")
    @Operation(summary = "Get transaction count", description = "Get total number of transactions for a customer")
    public ResponseEntity<ApiResponse<Long>> getCustomerTransactionCount(@PathVariable Long customerId) {
        long count = transactionService.getCustomerTransactionCount(customerId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/customer/{customerId}/total")
    @Operation(summary = "Get customer total spending", description = "Get total amount spent by a customer")
    public ResponseEntity<ApiResponse<BigDecimal>> getCustomerTotalSpending(@PathVariable Long customerId) {
        BigDecimal total = transactionService.getCustomerTotalSpending(customerId);
        return ResponseEntity.ok(ApiResponse.success(total));
    }
}

