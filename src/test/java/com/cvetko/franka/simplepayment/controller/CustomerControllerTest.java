package com.cvetko.franka.simplepayment.controller;

import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.service.interfaces.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @Test
    public void testGetCustomerById_CustomerFound() {
        Integer customerId = 1;
        Customer customer = new Customer(customerId,"John Doe","Ilica 1","random@random.com",new ArrayList<>());
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(customer));
        ResponseEntity<?> response = customerController.getCustomerById(customerId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Optional.of(customer), response.getBody());
    }

    @Test
    public void testGetCustomerById_CustomerNotFound() {
        Integer customerId = 1;
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.empty());
        ResponseEntity<?> response = customerController.getCustomerById(customerId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer not found with ID: " + customerId, response.getBody());
    }

}
