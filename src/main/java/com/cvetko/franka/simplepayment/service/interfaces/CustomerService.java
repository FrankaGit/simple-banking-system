package com.cvetko.franka.simplepayment.service.interfaces;

import com.cvetko.franka.simplepayment.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer saveCustomer(Customer c);

    Optional<Customer> findCustomerById(Integer id);
    List<Customer> createDummyCustomers(Integer numberOfCustomers);
}
