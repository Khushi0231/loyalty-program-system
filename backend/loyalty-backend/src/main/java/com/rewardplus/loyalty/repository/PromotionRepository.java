package com.rewardplus.loyalty.repository;

import com.rewardplus.loyalty.entity.Customer.CustomerTier;
import com.rewardplus.loyalty.entity.Promotion;
import com.rewardplus.loyalty.entity.Promotion.PromotionStatus;
import com.rewardplus.loyalty.entity.Promotion.PromotionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Promotion entity operations.
 */
@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    /**
     * Find promotion by promotion code.
     */
    Optional<Promotion> findByPromotionCode(String promotionCode);

    /**
     * Check if promotion exists by promotion code.
     */
    boolean existsByPromotionCode(String promotionCode);

    /**
     * Find promotions by status.
     */
    List<Promotion> findByStatus(PromotionStatus status);

    /**
     * Find promotions by status with pagination.
     */
    Page<Promotion> findByStatus(PromotionStatus status, Pageable pageable);

    /**
     * Find promotions by type.
     */
    List<Promotion> findByPromotionType(PromotionType promotionType);

    /**
     * Find currently active promotions.
     */
    @Query("SELECT p FROM Promotion p WHERE p.status = 'ACTIVE' " +
           "AND (p.startDate IS NULL OR CURRENT_DATE >= p.startDate) " +
           "AND (p.endDate IS NULL OR CURRENT_DATE <= p.endDate) " +
           "AND (p.usageLimit = 0 OR p.usageCount < p.usageLimit)")
    List<Promotion> findActivePromotions();

    /**
     * Find active promotions with pagination.
     */
    @Query("SELECT p FROM Promotion p WHERE p.status = 'ACTIVE' " +
           "AND (p.startDate IS NULL OR CURRENT_DATE >= p.startDate) " +
           "AND (p.endDate IS NULL OR CURRENT_DATE <= p.endDate) " +
           "AND (p.usageLimit = 0 OR p.usageCount < p.usageLimit)")
    Page<Promotion> findActivePromotions(Pageable pageable);

    /**
     * Find promotions by target tier.
     */
    List<Promotion> findByMinimumTier(CustomerTier minimumTier);

    /**
     * Find promotions for specific customer.
     */
    @Query("SELECT p FROM Promotion p JOIN p.targetCustomers c WHERE c.id = :customerId")
    List<Promotion> findPromotionsForCustomer(@Param("customerId") Long customerId);

    /**
     * Find promotions not yet started.
     */
    @Query("SELECT p FROM Promotion p WHERE p.status = 'SCHEDULED' " +
           "AND p.startDate IS NOT NULL AND CURRENT_DATE < p.startDate")
    List<Promotion> findScheduledPromotions();

    /**
     * Find expired promotions.
     */
    @Query("SELECT p FROM Promotion p WHERE p.status = 'EXPIRED' " +
           "OR (p.endDate IS NOT NULL AND CURRENT_DATE > p.endDate)")
    List<Promotion> findExpiredPromotions();

    /**
     * Find promotions by name containing search term.
     */
    @Query("SELECT p FROM Promotion p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Promotion> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Count active promotions.
     */
    long countByStatus(PromotionStatus status);

    /**
     * Find promotions expiring soon.
     */
    @Query("SELECT p FROM Promotion p WHERE p.status = 'ACTIVE' " +
           "AND p.endDate IS NOT NULL " +
           "AND p.endDate BETWEEN CURRENT_DATE AND :endDate")
    List<Promotion> findExpiringSoon(@Param("endDate") LocalDate endDate);

    /**
     * Find promotions by target gender.
     */
    List<Promotion> findByTargetGender(String targetGender);

    /**
     * Find promotions by target city.
     */
    List<Promotion> findByTargetCity(String targetCity);

    /**
     * Find promotions by end date range.
     */
    List<Promotion> findByEndDateBetween(LocalDate start, LocalDate end);
}

