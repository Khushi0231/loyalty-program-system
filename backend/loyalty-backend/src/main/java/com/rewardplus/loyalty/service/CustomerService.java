package com.rewardplus.loyalty.service;

import com.rewardplus.loyalty.dto.CustomerDTO;
import com.rewardplus.loyalty.dto.LoyaltyPointsDTO;
import com.rewardplus.loyalty.entity.Customer;
import com.rewardplus.loyalty.entity.LoyaltyPoints;
import com.rewardplus.loyalty.exception.DuplicateResourceException;
import com.rewardplus.loyalty.exception.ResourceNotFoundException;
import com.rewardplus.loyalty.repository.CustomerRepository;
import com.rewardplus.loyalty.repository.LoyaltyPointsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Customer business logic.
 * Handles customer enrollment, updates, and queries.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final LoyaltyPointsRepository loyaltyPointsRepository;
    private final ModelMapper modelMapper;

    @Value("${app.points.welcome-bonus:100}")
    private int welcomeBonusPoints;

    /**
     * Enroll a new customer in the loyalty program.
     */
    @Transactional
    public CustomerDTO enrollCustomer(CustomerDTO customerDTO) {
        log.info("Enrolling new customer with email: {}", customerDTO.getEmail());

        // Check for duplicate email
        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new DuplicateResourceException("Customer", "email", customerDTO.getEmail());
        }

        // Create customer entity
        Customer customer = modelMapper.map(customerDTO, Customer.class);
        customer.setStatus(Customer.CustomerStatus.ACTIVE);
        customer.setTier(Customer.CustomerTier.BRONZE);
        customer.setEnrollmentDate(LocalDate.now());

        // Save customer
        customer = customerRepository.save(customer);

        // Create loyalty points account with welcome bonus
        LoyaltyPoints loyaltyPoints = LoyaltyPoints.builder()
            .customer(customer)
            .currentBalance((long) welcomeBonusPoints)
            .lifetimePoints((long) welcomeBonusPoints)
            .pointsEarned((long) welcomeBonusPoints)
            .status(LoyaltyPoints.PointsStatus.ACTIVE)
            .build();
        loyaltyPointsRepository.save(loyaltyPoints);

        log.info("Customer enrolled successfully with ID: {} and welcome bonus: {} points",
            customer.getId(), welcomeBonusPoints);

        return mapToDTO(customer);
    }

    /**
     * Get customer by ID.
     */
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return mapToDTO(customer);
    }

    /**
     * Get customer by customer code.
     */
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerByCode(String customerCode) {
        Customer customer = customerRepository.findByCustomerCode(customerCode)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "customerCode", customerCode));
        return mapToDTO(customer);
    }

    /**
     * Get customer by email.
     */
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", email));
        return mapToDTO(customer);
    }

    /**
     * Get all customers with pagination.
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
            .map(this::mapToDTO);
    }

    /**
     * Get customers by status.
     */
    @Transactional(readOnly = true)
    public List<CustomerDTO> getCustomersByStatus(Customer.CustomerStatus status) {
        return customerRepository.findByStatus(status)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Get customers by tier.
     */
    @Transactional(readOnly = true)
    public List<CustomerDTO> getCustomersByTier(Customer.CustomerTier tier) {
        return customerRepository.findByTier(tier)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Search customers by name, email, or code.
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> searchCustomers(String searchTerm, Pageable pageable) {
        return customerRepository.searchCustomers(searchTerm, pageable)
            .map(this::mapToDTO);
    }

    /**
     * Get customers by age range.
     */
    @Transactional(readOnly = true)
    public List<CustomerDTO> getCustomersByAgeRange(int minAge, int maxAge) {
        LocalDate startDate = LocalDate.now().minusYears(maxAge);
        LocalDate endDate = LocalDate.now().minusYears(minAge);
        return customerRepository.findByAgeRange(startDate, endDate)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Update customer information.
     */
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        log.info("Updating customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        // Update fields
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setPhone(customerDTO.getPhone());
        customer.setAddress(customerDTO.getAddress());
        customer.setCity(customerDTO.getCity());
        customer.setState(customerDTO.getState());
        customer.setPostalCode(customerDTO.getPostalCode());
        customer.setCountry(customerDTO.getCountry());
        customer.setOccupation(customerDTO.getOccupation());
        customer.setCompany(customerDTO.getCompany());
        customer.setPreferences(customerDTO.getPreferences());
        customer.setProfileImageUrl(customerDTO.getProfileImageUrl());

        customer = customerRepository.save(customer);
        log.info("Customer updated successfully: {}", id);

        return mapToDTO(customer);
    }

    /**
     * Update customer status.
     */
    @Transactional
    public CustomerDTO updateCustomerStatus(Long id, Customer.CustomerStatus status) {
        log.info("Updating customer status for ID: {} to {}", id, status);

        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        customer.setStatus(status);
        customer = customerRepository.save(customer);

        return mapToDTO(customer);
    }

    /**
     * Update customer tier.
     */
    @Transactional
    public CustomerDTO updateCustomerTier(Long id, Customer.CustomerTier tier) {
        log.info("Updating customer tier for ID: {} to {}", id, tier);

        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        customer.setTier(tier);
        customer = customerRepository.save(customer);

        return mapToDTO(customer);
    }

    /**
     * Get customer's points balance.
     */
    @Transactional(readOnly = true)
    public LoyaltyPointsDTO getCustomerPointsBalance(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        LoyaltyPoints loyaltyPoints = loyaltyPointsRepository.findByCustomerId(customerId)
            .orElse(null);

        if (loyaltyPoints == null) {
            return LoyaltyPointsDTO.builder()
                .customerId(customerId)
                .customerName(customer.getFullName())
                .currentBalance(0L)
                .lifetimePoints(0L)
                .availableBalance(0L)
                .build();
        }

        LoyaltyPointsDTO dto = modelMapper.map(loyaltyPoints, LoyaltyPointsDTO.class);
        dto.setCustomerName(customer.getFullName());
        dto.setCustomerCode(customer.getCustomerCode());
        dto.setAvailableBalance(loyaltyPoints.getAvailableBalance());

        return dto;
    }

    /**
     * Get customer count by status.
     */
    @Transactional(readOnly = true)
    public long getCustomerCountByStatus(Customer.CustomerStatus status) {
        return customerRepository.countByStatus(status);
    }

    /**
     * Get inactive customers.
     */
    @Transactional(readOnly = true)
    public List<CustomerDTO> getInactiveCustomers(int daysInactive) {
        LocalDate cutoffDate = LocalDate.now().minusDays(daysInactive);
        return customerRepository.findInactiveCustomers(cutoffDate)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Map Customer entity to CustomerDTO.
     */
    private CustomerDTO mapToDTO(Customer customer) {
        CustomerDTO dto = modelMapper.map(customer, CustomerDTO.class);
        dto.setFullName(customer.getFullName());
        dto.setAge(customer.getAge());

        // Get points balance
        loyaltyPointsRepository.findByCustomerId(customer.getId())
            .ifPresent(lp -> dto.setCurrentPointsBalance(lp.getCurrentBalance()));

        return dto;
    }
}

