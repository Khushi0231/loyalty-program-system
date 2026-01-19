package com.rewardplus.loyalty.service;

import com.rewardplus.loyalty.dto.RewardDTO;
import com.rewardplus.loyalty.entity.Reward;
import com.rewardplus.loyalty.exception.ResourceNotFoundException;
import com.rewardplus.loyalty.repository.RewardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

    @Mock
    private RewardRepository rewardRepository;

    @InjectMocks
    private RewardService rewardService;

    private Reward testReward;

    @BeforeEach
    void setUp() {
        testReward = new Reward();
        testReward.setId(1L);
        testReward.setName("10% Off Next Purchase");
        testReward.setDescription("Get 10% discount on your next purchase");
        testReward.setRewardCode("RWD10PCT");
        testReward.setType(Reward.RewardType.DISCOUNT);
        testReward.setCategory(Reward.RewardCategory.PRODUCT);
        testReward.setPointsRequired(500L);
        testReward.setDiscountPercentage(BigDecimal.valueOf(10.0));
        testReward.setStatus(Reward.RewardStatus.ACTIVE);
        testReward.setStartDate(LocalDate.now());
        testReward.setExpiryDate(LocalDate.now().plusMonths(1));
    }

    @Test
    void createReward_Success() {
        RewardDTO inputDTO = new RewardDTO();
        inputDTO.setName("New Reward");
        inputDTO.setDescription("Test description");
        inputDTO.setRewardCode("NEWRWD");
        inputDTO.setType(Reward.RewardType.DISCOUNT);
        inputDTO.setCategory(Reward.RewardCategory.PRODUCT);
        inputDTO.setPointsRequired(1000L);
        inputDTO.setDiscountPercentage(BigDecimal.valueOf(15.0));

        when(rewardRepository.save(any(Reward.class))).thenAnswer(invocation -> {
            Reward saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        RewardDTO result = rewardService.createReward(inputDTO);

        assertNotNull(result);
        assertEquals("New Reward", result.getName());
        assertEquals("NEWRWD", result.getRewardCode());
        assertEquals(1000L, result.getPointsRequired());

        verify(rewardRepository, times(1)).save(any(Reward.class));
    }

    @Test
    void getRewardById_Success() {
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(testReward));

        RewardDTO result = rewardService.getRewardById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("10% Off Next Purchase", result.getName());
        assertEquals("RWD10PCT", result.getRewardCode());
    }

    @Test
    void getRewardById_NotFound() {
        when(rewardRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            rewardService.getRewardById(999L);
        });
    }

    @Test
    void getRewardByCode_Success() {
        when(rewardRepository.findByRewardCode("RWD10PCT")).thenReturn(Optional.of(testReward));

        RewardDTO result = rewardService.getRewardByCode("RWD10PCT");

        assertNotNull(result);
        assertEquals("RWD10PCT", result.getRewardCode());
    }

    @Test
    void getActiveRewards_Success() {
        Reward reward2 = new Reward();
        reward2.setId(2L);
        reward2.setName("Free Shipping");
        reward2.setRewardCode("FREESHIP");
        reward2.setStatus(Reward.RewardStatus.ACTIVE);
        reward2.setPointsRequired(400L);

        when(rewardRepository.findByStatus(Reward.RewardStatus.ACTIVE))
            .thenReturn(Arrays.asList(testReward, reward2));

        List<RewardDTO> results = rewardService.getActiveRewards();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r -> r.getStatus().equals(Reward.RewardStatus.ACTIVE)));
    }

    @Test
    void getRewardsByCategory_Success() {
        when(rewardRepository.findByCategory(Reward.RewardCategory.PRODUCT))
            .thenReturn(Arrays.asList(testReward));

        List<RewardDTO> results = rewardService.getRewardsByCategory(Reward.RewardCategory.PRODUCT);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(Reward.RewardCategory.PRODUCT, results.get(0).getCategory());
    }

    @Test
    void updateRewardStatus_Success() {
        when(rewardRepository.findById(1L)).thenReturn(Optional.of(testReward));
        when(rewardRepository.save(any(Reward.class))).thenReturn(testReward);

        RewardDTO result = rewardService.updateRewardStatus(1L, Reward.RewardStatus.PAUSED);

        assertNotNull(result);
        assertEquals(Reward.RewardStatus.PAUSED, result.getStatus());
    }

    @Test
    void getAffordableRewards_Success() {
        Reward reward2 = new Reward();
        reward2.setId(2L);
        reward2.setName("$10 Discount");
        reward2.setRewardCode("DISCOUNT10");
        reward2.setPointsRequired(1000L);
        reward2.setStatus(Reward.RewardStatus.ACTIVE);

        when(rewardRepository.findByStatusAndPointsRequiredLessThanEqual(
            Reward.RewardStatus.ACTIVE, 1000))
            .thenReturn(Arrays.asList(testReward, reward2));

        List<RewardDTO> results = rewardService.getAffordableRewards(1000);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(r -> r.getPointsRequired() <= 1000));
    }
}

