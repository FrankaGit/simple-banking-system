package com.cvetko.franka.simplepayment.service.implementation;

import com.cvetko.franka.simplepayment.dao.CustomerRepository;
import com.cvetko.franka.simplepayment.model.Customer;
import com.cvetko.franka.simplepayment.service.interfaces.CustomerService;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    CustomerRepository customerRepository;

    @Override
    public Customer saveCustomer(Customer c) {
        return customerRepository.save(c);
    }

    @Override
    public Optional<Customer> findCustomerById(Integer id) {
        return customerRepository.findById(id);
    }

    @Override
    public List<Customer> createDummyCustomers(Integer numberOfCustomers) {

            try {
                Faker faker = new Faker();
                List<Customer> customers = new ArrayList<>();

                for (int i = 0; i < numberOfCustomers; i++) {
                    Customer customer = new Customer();
                    customer.setName(faker.name().fullName());
                    customer.setAddress(faker.address().fullAddress());
                    customer.setEmail(faker.internet().emailAddress());
                    customers.add(customer);
                }

                customerRepository.saveAll(customers);
                return customers;

            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                return null;
            }
    }
}
