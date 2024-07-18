package com.cvetko.franka.simplepayment.controller;

import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.service.interfaces.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private Customer mockCustomer;

    @BeforeEach
    void setUp() {
        mockCustomer = new Customer();
        mockCustomer.setName("John Doe");
        mockCustomer.setEmail("john.doe@example.com");
    }

    @Test
    void testGetCustomerByIdFound() {
        when(customerService.findCustomerById(1)).thenReturn(Optional.of(mockCustomer));

        ResponseEntity<Customer> responseEntity = customerController.getCustomerById(1);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(mockCustomer);
    }

    @Test
    void testGetCustomerByIdNotFound() {
        when(customerService.findCustomerById(2)).thenReturn(Optional.empty());

        ResponseEntity<Customer> responseEntity = customerController.getCustomerById(2);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNull();
    }
}
