package com.cvetko.franka.simplepayment.service;

import com.cvetko.franka.simplepayment.dao.CustomerRepository;
import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.service.implementation.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    public void testSaveCustomer_Success() {
        Customer customer = new Customer();
        when(customerRepository.save(customer)).thenReturn(customer);
        Customer result = customerService.saveCustomer(customer);
        assertEquals(customer, result);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    public void testFindCustomerById_Success() {
        Integer customerId = 1;
        Customer customer = new Customer();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        Optional<Customer> result = customerService.findCustomerById(customerId);
        assertEquals(Optional.of(customer), result);
    }

    @Test
    public void testFindCustomerById_NotFound() {
        Integer customerId = 1;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());
        Optional<Customer> result = customerService.findCustomerById(customerId);
        assertEquals(Optional.empty(), result);
    }

}
