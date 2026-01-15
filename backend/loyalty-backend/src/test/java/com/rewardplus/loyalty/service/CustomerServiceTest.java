package com.rewardplus.loyalty.service;

import com.rewardplus.loyalty.dto.CustomerDTO;
import com.rewardplus.loyalty.dto.LoyaltyPointsDTO;
import com.rewardplus.loyalty.entity.Customer;
import com.rewardplus.loyalty.entity.LoyaltyPoints;
import com.rewardplus.loyalty.exception.ResourceNotFoundException;
import com.rewardplus.loyalty.repository.CustomerRepository;
import com.rewardplus.loyalty.repository.LoyaltyPointsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoyaltyPointsRepository loyaltyPointsRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private LoyaltyPoints testPoints;

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
        testCustomer.setTier(Customer.CustomerTier.BRONZE);
        testCustomer.setEnrollmentDate(LocalDate.now());

        testPoints = new LoyaltyPoints();
        testPoints.setId(1L);
        testPoints.setCustomer(testCustomer);
        testPoints.setCurrentBalance(500);
        testPoints.setLifetimePoints(500);
        testPoints.setPointsEarned(500);
        testPoints.setPointsRedeemed(0);
    }

    @Test
    void enrollCustomer_Success() {
        CustomerDTO inputDTO = new CustomerDTO();
        inputDTO.setFirstName("John");
        inputDTO.setLastName("Doe");
        inputDTO.setEmail("john.doe@email.com");
        inputDTO.setDateOfBirth("1990-05-15");

        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        when(loyaltyPointsRepository.save(any(LoyaltyPoints.class))).thenAnswer(invocation -> {
            LoyaltyPoints saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        CustomerDTO result = customerService.enrollCustomer(inputDTO);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@email.com", result.getEmail());
        assertEquals(100, result.getWelcomeBonusPoints());
        assertEquals(Customer.CustomerTier.BRONZE.name(), result.getTier());

        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(loyaltyPointsRepository, times(1)).save(any(LoyaltyPoints.class));
    }

    @Test
    void getCustomerById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(loyaltyPointsRepository.findByCustomerId(1L)).thenReturn(Optional.of(testPoints));

        CustomerDTO result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("CUST000001", result.getCustomerCode());
        assertEquals("John", result.getFirstName());
    }

    @Test
    void getCustomerById_NotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            customerService.getCustomerById(999L);
        });
    }

    @Test
    void getCustomerPointsBalance_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(loyaltyPointsRepository.findByCustomerId(1L)).thenReturn(Optional.of(testPoints));

        LoyaltyPointsDTO result = customerService.getCustomerPointsBalance(1L);

        assertNotNull(result);
        assertEquals(500, result.getCurrentBalance());
        assertEquals(500, result.getLifetimePoints());
    }

    @Test
    void getCustomerPointsBalance_CustomerNotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            customerService.getCustomerPointsBalance(999L);
        });
    }

    @Test
    void getCustomerPointsBalance_NoPointsRecord() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(loyaltyPointsRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

        LoyaltyPointsDTO result = customerService.getCustomerPointsBalance(1L);

        assertNotNull(result);
        assertEquals(0, result.getCurrentBalance());
    }
}

