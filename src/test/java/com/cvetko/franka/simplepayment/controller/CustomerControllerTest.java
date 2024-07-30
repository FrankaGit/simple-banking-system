package com.cvetko.franka.simplepayment.controller;

import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.service.interfaces.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

}
