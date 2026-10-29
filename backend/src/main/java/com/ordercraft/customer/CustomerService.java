package com.ordercraft.customer;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    public Customer create(Customer customer) {
        customer.setId(null);
        return customerRepository.save(customer);
    }

    public Customer update(Long id, Customer updates) {
        Customer customer = findById(id);
        customer.setName(updates.getName());
        customer.setEmail(updates.getEmail());
        customer.setPhone(updates.getPhone());
        customer.setAddress(updates.getAddress());
        return customerRepository.save(customer);
    }

    public void delete(Long id) {
        customerRepository.delete(findById(id));
    }
}
