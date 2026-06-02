package com.ele_shop_mgmt.backend.mapper;

import com.ele_shop_mgmt.backend.dto.request.CustomerRequest;
import com.ele_shop_mgmt.backend.dto.response.CustomerResponse;
import com.ele_shop_mgmt.backend.entity.Customer;

public final class CustomerMapper {
    private CustomerMapper(){
        throw new UnsupportedOperationException("Utility class - cannot be instantiated");

    }
    public static Customer toEntity(CustomerRequest request){
        if (request == null){
            return null;
        }
        return Customer.builder()
                .name(request.getName())
                .mobileNumber(request.getMobileNumber())
                .address(request.getAddress())
                .villageCity(request.getVillageCity())
                .gstNumber(request.getGstNumber())
                .build();
    }
    public static CustomerResponse toResponse(Customer customer){
        if(customer == null){
            return null;
        }

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
