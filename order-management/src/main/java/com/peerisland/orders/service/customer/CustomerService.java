package com.peerisland.orders.service.customer;

import com.peerisland.orders.dto.ApiRestResponse;
import com.peerisland.orders.dto.PageRequestDto;
import com.peerisland.orders.dto.customer.CustomerCreationRequest;
import com.peerisland.orders.dto.customer.CustomerUpdateRequest;
import com.peerisland.orders.exception.InvalidDataException;
import org.apache.coyote.BadRequestException;

public interface CustomerService {

    ApiRestResponse createCustomer(String requestId, CustomerCreationRequest request) throws BadRequestException;

    ApiRestResponse getCustomers(String requestId, PageRequestDto pageRequestDto);

    ApiRestResponse getCustomer(String requestId, Long customerId);

    ApiRestResponse updateCustomer(String requestId, Long customerId, CustomerUpdateRequest request);

    ApiRestResponse UpdateCustomerStatus(String requestId, Long customerId, Boolean active) throws InvalidDataException;
}
