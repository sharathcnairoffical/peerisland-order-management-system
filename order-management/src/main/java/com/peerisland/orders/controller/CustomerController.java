package com.peerisland.orders.controller;

import com.peerisland.orders.dto.ApiRestResponse;
import com.peerisland.orders.dto.PageRequestDto;
import com.peerisland.orders.dto.customer.CustomerCreationRequest;
import com.peerisland.orders.dto.customer.CustomerUpdateRequest;
import com.peerisland.orders.exception.InvalidDataException;
import com.peerisland.orders.service.customer.CustomerService;
import com.peerisland.orders.utility.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiRestResponse> createCustomer(@RequestHeader(value = Constants.REQUEST_ID) String requestId,
                                                          @Valid @RequestBody CustomerCreationRequest request)
            throws BadRequestException {
        ApiRestResponse customer = customerService.createCustomer(requestId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @GetMapping
    public ResponseEntity<ApiRestResponse> getCustomers(@RequestHeader(value = Constants.REQUEST_ID) String requestId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        ApiRestResponse customers = customerService.getCustomers(requestId, PageRequestDto.builder().page(page).size(size).build());
        return new ResponseEntity<>(customers, HttpStatus.OK);

    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiRestResponse> getCustomer(@RequestHeader(value = Constants.REQUEST_ID) String requestId,
                                                       @PathVariable Long customerId) {
        ApiRestResponse customer = customerService.getCustomer(requestId, customerId);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<ApiRestResponse> updateCustomer(@RequestHeader(value = Constants.REQUEST_ID) String requestId,
                                                          @PathVariable Long customerId,
                                                          @Valid @RequestBody CustomerUpdateRequest request) {
        ApiRestResponse apiRestResponse = customerService.updateCustomer(requestId, customerId, request);
        return new ResponseEntity<>(apiRestResponse, HttpStatus.OK);
    }

    @PatchMapping("/change-account-status")
    public ResponseEntity<ApiRestResponse> updateCustomerActive(@RequestHeader(value = Constants.REQUEST_ID) String requestId,
                                                                @RequestParam Long customerId,
                                                                @RequestParam Boolean active)
            throws InvalidDataException {
        ApiRestResponse apiRestResponse = customerService.UpdateCustomerStatus(requestId, customerId, active);
        return new ResponseEntity<>(apiRestResponse, HttpStatus.OK);
    }
}
