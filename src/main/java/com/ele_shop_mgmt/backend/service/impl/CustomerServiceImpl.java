package com.ele_shop_mgmt.backend.service.impl;

import com.ele_shop_mgmt.backend.dto.request.CustomerRequest;
import com.ele_shop_mgmt.backend.dto.response.CustomerResponse;
import com.ele_shop_mgmt.backend.entity.Customer;
import com.ele_shop_mgmt.backend.exception.DuplicateResourceException;
import com.ele_shop_mgmt.backend.exception.ResourceNotFoundException;
import com.ele_shop_mgmt.backend.repository.CustomerRepository;
import com.ele_shop_mgmt.backend.service.interfaces.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        // Prevent duplicate mobile numbers
        customerRepository.findByMobileNumber(request.getMobileNumber())
                .ifPresent(c -> {
                    throw new DuplicateResourceException("Customer with mobile number already exists");
                });

        Customer customer = Customer.builder()
                .name(request.getName())
                .mobileNumber(request.getMobileNumber())
                .address(request.getAddress())
                .villageCity(request.getVillageCity())
                .gstNumber(request.getGstNumber())
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        return mapToResponse(savedCustomer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return mapToResponse(customer);
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setName(request.getName());
        customer.setMobileNumber(request.getMobileNumber());
        customer.setAddress(request.getAddress());
        customer.setVillageCity(request.getVillageCity());
        customer.setGstNumber(request.getGstNumber());

        Customer updatedCustomer = customerRepository.save(customer);
        return mapToResponse(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customer);
    }

    @Override
    public List<CustomerResponse> searchCustomers(String keyword) {
        return customerRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .mobileNumber(customer.getMobileNumber())
                .address(customer.getAddress())
                .villageCity(customer.getVillageCity())
                .gstNumber(customer.getGstNumber())
                .build();
    }
}
