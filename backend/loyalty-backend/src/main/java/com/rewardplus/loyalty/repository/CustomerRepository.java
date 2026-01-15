package com.rewardplus.loyalty.repository;

import com.rewardplus.loyalty.entity.Customer;
import com.rewardplus.loyalty.entity.Customer.CustomerStatus;
import com.rewardplus.loyalty.entity.Customer.CustomerTier;
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
 * Repository interface for Customer entity operations.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find customer by email address.
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Find customer by customer code.
     */
    Optional<Customer> findByCustomerCode(String customerCode);

    /**
     * Check if customer exists by email.
     */
    boolean existsByEmail(String email);

    /**
     * Check if customer exists by customer code.
     */
    boolean existsByCustomerCode(String customerCode);

    /**
     * Find all customers by status.
     */
    List<Customer> findByStatus(CustomerStatus status);

    /**
     * Find all customers by status with pagination.
     */
    Page<Customer> findByStatus(CustomerStatus status, Pageable pageable);

    /**
     * Find all customers by tier.
     */
    List<Customer> findByTier(CustomerTier tier);

    /**
     * Find customers by city.
     */
    List<Customer> findByCity(String city);

    /**
     * Find customers by state.
     */
    List<Customer> findByState(String state);

    /**
     * Find customers by gender.
     */
    List<Customer> findByGender(String gender);

    /**
     * Find customers within age range.
     */
    @Query("SELECT c FROM Customer c WHERE c.dateOfBirth BETWEEN :startDate AND :endDate")
    List<Customer> findByAgeRange(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);

    /**
     * Find customers by occupation.
     */
    List<Customer> findByOccupation(String occupation);

    /**
     * Find customers by company.
     */
    List<Customer> findByCompany(String company);

    /**
     * Find customers enrolled within date range.
     */
    @Query("SELECT c FROM Customer c WHERE c.enrollmentDate BETWEEN :startDate AND :endDate")
    List<Customer> findByEnrollmentDateRange(@Param("startDate") LocalDate startDate, 
                                              @Param("endDate") LocalDate endDate);

    /**
     * Find active customers with specific tier.
     */
    List<Customer> findByStatusAndTier(CustomerStatus status, CustomerTier tier);

    /**
     * Find customers with no activity since given date.
     */
    @Query("SELECT c FROM Customer c WHERE c.lastActivityDate < :date OR c.lastActivityDate IS NULL")
    List<Customer> findInactiveCustomers(@Param("date") LocalDate date);

    /**
     * Search customers by name (first or last).
     */
    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "c.customerCode LIKE CONCAT('%', :searchTerm, '%') OR " +
           "c.email LIKE CONCAT('%', :searchTerm, '%')")
    Page<Customer> searchCustomers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Count customers by status.
     */
    long countByStatus(CustomerStatus status);

    /**
     * Count customers by tier.
     */
    long countByTier(CustomerTier tier);

    /**
     * Find customers eligible for tier upgrade.
     */
    @Query("SELECT c FROM Customer c WHERE c.tier != 'DIAMOND' AND " +
           "(SELECT COALESCE(SUM(t.netAmount), 0) FROM Transaction t WHERE t.customer = c) >= :spendThreshold")
    List<Customer> findCustomersEligibleForTierUpgrade(@Param("spendThreshold") Double spendThreshold);

    /**
     * Find customers by multiple IDs.
     */
    List<Customer> findByIdIn(List<Long> ids);
}

