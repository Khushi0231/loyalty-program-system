package com.rewardplus.loyalty.service;

import com.rewardplus.loyalty.dto.PromotionDTO;
import com.rewardplus.loyalty.entity.Promotion;
import com.rewardplus.loyalty.exception.ResourceNotFoundException;
import com.rewardplus.loyalty.repository.PromotionRepository;
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
class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    private PromotionService promotionService;

    private Promotion testPromotion;

    @BeforeEach
    void setUp() {
        testPromotion = new Promotion();
        testPromotion.setId(1L);
        testPromotion.setName("New Year Bonus");
        testPromotion.setDescription("Double points for all purchases in January");
        testPromotion.setPromotionCode("NEWYEAR2024");
        testPromotion.setPromotionType(Promotion.PromotionType.DOUBLE_POINTS);
        testPromotion.setStatus(Promotion.PromotionStatus.ACTIVE);
        testPromotion.setStartDate(LocalDate.of(2024, 1, 1));
        testPromotion.setEndDate(LocalDate.of(2024, 1, 31));
        testPromotion.setBonusPointsMultiplier(BigDecimal.valueOf(2.0));
    }

    @Test
    void createPromotion_Success() {
        PromotionDTO inputDTO = new PromotionDTO();
        inputDTO.setName("Spring Sale");
        inputDTO.setDescription("20% off + 2x points");
        inputDTO.setPromotionCode("SPRING2024");
        inputDTO.setPromotionType(Promotion.PromotionType.LOYALTY_BOOST);
        inputDTO.setBonusPointsMultiplier(BigDecimal.valueOf(2.0));
        inputDTO.setStartDate(LocalDate.of(2024, 3, 1));
        inputDTO.setEndDate(LocalDate.of(2024, 3, 31));

        when(promotionRepository.save(any(Promotion.class))).thenAnswer(invocation -> {
            Promotion saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        PromotionDTO result = promotionService.createPromotion(inputDTO);

        assertNotNull(result);
        assertEquals("Spring Sale", result.getName());
        assertEquals("SPRING2024", result.getPromotionCode());

        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

    @Test
    void getPromotionById_Success() {
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(testPromotion));

        PromotionDTO result = promotionService.getPromotionById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Year Bonus", result.getName());
    }

    @Test
    void getPromotionById_NotFound() {
        when(promotionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            promotionService.getPromotionById(999L);
        });
    }

    @Test
    void getActivePromotions_Success() {
        Promotion promotion2 = new Promotion();
        promotion2.setId(2L);
        promotion2.setName("Weekend Flash Sale");
        promotion2.setStatus(Promotion.PromotionStatus.ACTIVE);

        when(promotionRepository.findByStatus(Promotion.PromotionStatus.ACTIVE))
            .thenReturn(Arrays.asList(testPromotion, promotion2));

        List<PromotionDTO> results = promotionService.getActivePromotions();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> p.getStatus().equals(Promotion.PromotionStatus.ACTIVE)));
    }

    @Test
    void activatePromotion_Success() {
        testPromotion.setStatus(Promotion.PromotionStatus.DRAFT);
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(testPromotion));
        when(promotionRepository.save(any(Promotion.class))).thenReturn(testPromotion);

        PromotionDTO result = promotionService.activatePromotion(1L);

        assertNotNull(result);
        assertEquals(Promotion.PromotionStatus.ACTIVE, result.getStatus());
    }

    @Test
    void pausePromotion_Success() {
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(testPromotion));
        when(promotionRepository.save(any(Promotion.class))).thenReturn(testPromotion);

        PromotionDTO result = promotionService.pausePromotion(1L);

        assertNotNull(result);
        assertEquals(Promotion.PromotionStatus.PAUSED, result.getStatus());
    }

    @Test
    void getPromotionsByType_Success() {
        when(promotionRepository.findByPromotionType(Promotion.PromotionType.DOUBLE_POINTS))
            .thenReturn(Arrays.asList(testPromotion));

        List<PromotionDTO> results = promotionService.getPromotionsByType(Promotion.PromotionType.DOUBLE_POINTS);

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    void getExpiringPromotions_Success() {
        Promotion promotion2 = new Promotion();
        promotion2.setId(2L);
        promotion2.setName("Expiring Soon");
        promotion2.setEndDate(LocalDate.now().plusDays(3));

        when(promotionRepository.findByEndDateBetween(any(), any()))
            .thenReturn(Arrays.asList(testPromotion, promotion2));

        List<PromotionDTO> results = promotionService.getExpiringPromotions(7);

        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    void updatePromotionStatus_Success() {
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(testPromotion));
        when(promotionRepository.save(any(Promotion.class))).thenReturn(testPromotion);

        PromotionDTO result = promotionService.updatePromotionStatus(1L, Promotion.PromotionStatus.EXPIRED);

        assertNotNull(result);
        assertEquals(Promotion.PromotionStatus.EXPIRED, result.getStatus());
    }
}

