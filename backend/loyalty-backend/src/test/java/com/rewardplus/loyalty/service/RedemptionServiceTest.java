package com.rewardplus.loyalty.service;

import com.rewardplus.loyalty.dto.RedemptionDTO;
import com.rewardplus.loyalty.entity.Customer;
import com.rewardplus.loyalty.entity.LoyaltyPoints;
import com.rewardplus.loyalty.entity.Reward;
import com.rewardplus.loyalty.entity.RedemptionLog;
import com.rewardplus.loyalty.exception.InsufficientPointsException;
import com.rewardplus.loyalty.exception.ResourceNotFoundException;
import com.rewardplus.loyalty.repository.RedemptionLogRepository;
import com.rewardplus.loyalty.repository.LoyaltyPointsRepository;
import com.rewardplus.loyalty.repository.RewardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedemptionServiceTest {

    @Mock
    private RedemptionLogRepository redemptionLogRepository;

    @Mock
    private LoyaltyPointsRepository loyaltyPointsRepository;

    @Mock
    private RewardRepository rewardRepository;

    @Mock
    private com.rewardplus.loyalty.repository.CustomerRepository customerRepository;

    @InjectMocks
    private RedemptionService redemptionService;

    private Customer testCustomer;
    private LoyaltyPoints testPoints;
    private Reward testReward;
    private RedemptionLog testRedemption;

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

        testPoints = new LoyaltyPoints();
        testPoints.setId(1L);
        testPoints.setCustomer(testCustomer);
        testPoints.setCurrentBalance(1000L);
        testPoints.setLifetimePoints(1500L);
        testPoints.setPointsEarned(1500L);
        testPoints.setPointsRedeemed(500L);

        testReward = new Reward();
        testReward.setId(1L);
        testReward.setName("10% Off Next Purchase");
        testReward.setRewardCode("RWD10PCT");
        testReward.setPointsRequired(500L);
        testReward.setStatus(Reward.RewardStatus.ACTIVE);

        testRedemption = new RedemptionLog();
        testRedemption.setId(1L);
        testRedemption.setRedemptionCode("RDM001");
        testRedemption.setCustomer(testCustomer);
        testRedemption.setReward(testReward);
        testRedemption.setPointsRedeemed(500L);
        testRedemption.setStatus(RedemptionLog.RedemptionStatus.COMPLETED);
        testRedemption.setRedemptionDate(LocalDateTime.now());
    }

    @Test
    void redeemReward_Success() {
        RedemptionDTO inputDTO = new RedemptionDTO();
        inputDTO.setChannel("ONLINE");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(loyaltyPointsRepository.findByCustomerId(1L)).thenReturn(Optional.of(testPoints));
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(testReward));
        when(redemptionLogRepository.save(any(RedemptionLog.class))).thenAnswer(invocation -> {
            RedemptionLog saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(loyaltyPointsRepository.save(any(LoyaltyPoints.class))).thenReturn(testPoints);

        RedemptionDTO result = redemptionService.redeemReward(1L, 1L, inputDTO);

        assertNotNull(result);
        assertEquals(500L, result.getPointsRedeemed());
        assertEquals(RedemptionLog.RedemptionStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getRedemptionCode());

        verify(redemptionLogRepository, times(1)).save(any(RedemptionLog.class));
    }

    @Test
    void redeemReward_InsufficientPoints() {
        testPoints.setCurrentBalance(100L);

        RedemptionDTO inputDTO = new RedemptionDTO();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(loyaltyPointsRepository.findByCustomerId(1L)).thenReturn(Optional.of(testPoints));
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(testReward));

        assertThrows(InsufficientPointsException.class, () -> {
            redemptionService.redeemReward(1L, 1L, inputDTO);
        });
    }

    @Test
    void redeemReward_CustomerNotFound() {
        RedemptionDTO inputDTO = new RedemptionDTO();

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            redemptionService.redeemReward(999L, 1L, inputDTO);
        });
    }

    @Test
    void redeemReward_RewardNotFound() {
        RedemptionDTO inputDTO = new RedemptionDTO();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(loyaltyPointsRepository.findByCustomerId(1L)).thenReturn(Optional.of(testPoints));
        when(rewardRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            redemptionService.redeemReward(1L, 999L, inputDTO);
        });
    }

    @Test
    void getRedemptionByCode_Success() {
        when(redemptionLogRepository.findByRedemptionCode("RDM001")).thenReturn(Optional.of(testRedemption));

        RedemptionDTO result = redemptionService.getRedemptionByCode("RDM001");

        assertNotNull(result);
        assertEquals("RDM001", result.getRedemptionCode());
        assertEquals(500L, result.getPointsRedeemed());
    }

    @Test
    void markAsUsed_Success() {
        testRedemption.setStatus(RedemptionLog.RedemptionStatus.COMPLETED);
        when(redemptionLogRepository.findById(1L)).thenReturn(Optional.of(testRedemption));
        when(redemptionLogRepository.save(any(RedemptionLog.class))).thenReturn(testRedemption);

        RedemptionDTO result = redemptionService.markAsUsed(1L);

        assertNotNull(result);
        assertEquals(RedemptionLog.RedemptionStatus.USED, result.getStatus());
    }

    @Test
    void cancelRedemption_Success() {
        testPoints.setCurrentBalance(1000);
        when(redemptionLogRepository.findById(1L)).thenReturn(Optional.of(testRedemption));
        when(redemptionLogRepository.save(any(RedemptionLog.class))).thenReturn(testRedemption);
        when(loyaltyPointsRepository.save(any(LoyaltyPoints.class))).thenReturn(testPoints);

        RedemptionDTO result = redemptionService.cancelRedemption(1L, "Customer requested");

        assertNotNull(result);
        assertEquals(RedemptionLog.RedemptionStatus.CANCELLED, result.getStatus());
        assertEquals("Customer requested", result.getCancellationReason());
    }
}

