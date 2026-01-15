package com.rewardplus.loyalty.service;

import com.rewardplus.loyalty.dto.TransactionDTO;
import com.rewardplus.loyalty.entity.Customer;
import com.rewardplus.loyalty.entity.LoyaltyPoints;
import com.rewardplus.loyalty.entity.Transaction;
import com.rewardplus.loyalty.exception.ResourceNotFoundException;
import com.rewardplus.loyalty.repository.TransactionRepository;
import com.rewardplus.loyalty.repository.LoyaltyPointsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private LoyaltyPointsRepository loyaltyPointsRepository;

    @Mock
    private LoyaltyPointsService loyaltyPointsService;

    @InjectMocks
    private TransactionService transactionService;

    private Customer testCustomer;
    private LoyaltyPoints testPoints;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setCustomerCode("CUST000001");
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john.doe@email.com");
        testCustomer.setDateOfBirth(LocalDate.of(1990, 5, 15));
        testCustomer.setStatus(Customer.CustomerStatus.ACTIVE);
        testCustomer.setTier(Customer.CustomerTier.GOLD);
        testCustomer.setEnrollmentDate(LocalDate.now());

        testPoints = new LoyaltyPoints();
        testPoints.setId(1L);
        testPoints.setCustomer(testCustomer);
        testPoints.setCurrentBalance(1000);
        testPoints.setLifetimePoints(1500);
        testPoints.setPointsEarned(1500);
        testPoints.setPointsRedeemed(500);
        testPoints.setLastEarnedDate(LocalDateTime.now().minusDays(5));

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setTransactionCode("TXN001");
        testTransaction.setCustomer(testCustomer);
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setNetAmount(new BigDecimal("100.00"));
        testTransaction.setTransactionDate(LocalDateTime.now());
        testTransaction.setTransactionType(Transaction.TransactionType.PURCHASE);
        testTransaction.setStatus(Transaction.TransactionStatus.COMPLETED);
    }

    @Test
    void recordTransaction_Success() {
        TransactionDTO inputDTO = new TransactionDTO();
        inputDTO.setAmount(new BigDecimal("100.00"));
        inputDTO.setTransactionDate(LocalDateTime.now());
        inputDTO.setStoreCode("STORE001");
        inputDTO.setStoreName("Main Street Store");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(loyaltyPointsRepository.findByCustomerId(1L)).thenReturn(Optional.of(testPoints));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(loyaltyPointsRepository.save(any(LoyaltyPoints.class))).thenReturn(testPoints);

        TransactionDTO result = transactionService.recordTransaction(1L, inputDTO);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals(1000, result.getPointsEarned()); // 100 * 10 points per dollar
        assertEquals(Transaction.TransactionStatus.COMPLETED.name(), result.getStatus());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void recordTransaction_GoldTierBonus() {
        testCustomer.setTier(Customer.CustomerTier.GOLD);
        TransactionDTO inputDTO = new TransactionDTO();
        inputDTO.setAmount(new BigDecimal("100.00"));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(loyaltyPointsRepository.findByCustomerId(1L)).thenReturn(Optional.of(testPoints));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(loyaltyPointsRepository.save(any(LoyaltyPoints.class))).thenReturn(testPoints);

        TransactionDTO result = transactionService.recordTransaction(1L, inputDTO);

        assertNotNull(result);
        assertEquals(1000, result.getPointsEarned()); // Gold tier: 100 * 10 = 1000
    }

    @Test
    void recordTransaction_CustomerNotFound() {
        TransactionDTO inputDTO = new TransactionDTO();
        inputDTO.setAmount(new BigDecimal("100.00"));

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.recordTransaction(999L, inputDTO);
        });
    }

    @Test
    void getTransactionById_Success() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        TransactionDTO result = transactionService.getTransactionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("TXN001", result.getTransactionCode());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
    }

    @Test
    void getTransactionById_NotFound() {
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.getTransactionById(999L);
        });
    }

    @Test
    void getTransactionByCode_Success() {
        when(transactionRepository.findByTransactionCode("TXN001")).thenReturn(Optional.of(testTransaction));

        TransactionDTO result = transactionService.getTransactionByCode("TXN001");

        assertNotNull(result);
        assertEquals("TXN001", result.getTransactionCode());
    }

    @Test
    void calculatePoints_GoldTierMultiplier() {
        long points = transactionService.calculatePoints(new BigDecimal("100.00"), Customer.CustomerTier.GOLD);
        assertEquals(1000, points); // 100 * 10 * 1.0 (no gold bonus in base calculation)
    }

    @Test
    void calculatePoints_PlatinumTierMultiplier() {
        long points = transactionService.calculatePoints(new BigDecimal("100.00"), Customer.CustomerTier.PLATINUM);
        assertEquals(1000, points);
    }
}

