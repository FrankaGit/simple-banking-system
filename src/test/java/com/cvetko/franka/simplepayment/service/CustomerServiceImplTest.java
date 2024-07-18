package com.cvetko.franka.simplepayment.service;

import com.cvetko.franka.simplepayment.dao.CustomerRepository;
import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.service.implementation.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerServiceImpl;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john.doe@example.com");
    }

    @Test
    void testSaveCustomer() {
        given(customerRepository.save(any(Customer.class))).willReturn(testCustomer);

        Customer savedCustomer = customerServiceImpl.saveCustomer(testCustomer);
        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getName()).isEqualTo(testCustomer.getName());
        assertThat(savedCustomer.getEmail()).isEqualTo(testCustomer.getEmail());
        verify(customerRepository).save(testCustomer);
    }

    @Test
    void testFindCustomerById() {
        given(customerRepository.findById(1)).willReturn(Optional.of(testCustomer));

        Optional<Customer> foundCustomerOptional = customerServiceImpl.findCustomerById(1);

        assertThat(foundCustomerOptional).isPresent();
        Customer foundCustomer = foundCustomerOptional.get();
        assertThat(foundCustomer.getName()).isEqualTo(testCustomer.getName());
        assertThat(foundCustomer.getEmail()).isEqualTo(testCustomer.getEmail());
        verify(customerRepository).findById(1);
    }

    @Test
    void testFindCustomerById_NotFound() {
        given(customerRepository.findById(2)).willReturn(Optional.empty());

        Optional<Customer> foundCustomerOptional = customerServiceImpl.findCustomerById(2);

        assertThat(foundCustomerOptional).isEmpty();
        verify(customerRepository).findById(2);
    }
}
