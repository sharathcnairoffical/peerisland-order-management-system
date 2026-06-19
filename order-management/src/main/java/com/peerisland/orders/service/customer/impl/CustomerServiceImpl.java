package com.peerisland.orders.service.customer.impl;

import com.peerisland.orders.dto.ApiRestResponse;
import com.peerisland.orders.dto.PageRequestDto;
import com.peerisland.orders.dto.customer.CustomerCreationRequest;
import com.peerisland.orders.dto.customer.CustomerDto;
import com.peerisland.orders.dto.customer.CustomerUpdateRequest;
import com.peerisland.orders.entity.Customer;
import com.peerisland.orders.entity.Roles;
import com.peerisland.orders.exception.InvalidDataException;
import com.peerisland.orders.exception.NotFoundException;
import com.peerisland.orders.exception.UserExistsException;
import com.peerisland.orders.mapper.CustomerMapper;
import com.peerisland.orders.repository.CustomerRepository;
import com.peerisland.orders.repository.RolesRepository;
import com.peerisland.orders.security.models.UserPrincipal;
import com.peerisland.orders.service.customer.CustomerService;
import com.peerisland.orders.utility.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    private final RolesRepository rolesRepository;
    private final CustomerRepository customerRepository;

    @Override
    public ApiRestResponse createCustomer(String requestId, CustomerCreationRequest request) throws BadRequestException {

        log.info("RequestID: {} | Owner Creation request received: {}", requestId, request);

        if (ObjectUtils.isEmpty(request)) {
            log.error("RequestID: {} | Owner Creation request is empty", requestId);
            throw new BadRequestException("Owner Creation request is empty");
        }

        if (customerRepository.existsByEmail(request.email())) {
            log.error("RequestID: {} | Owner Creation request already exists", requestId);
            throw new UserExistsException("Customer already exists with email: " + request.email());
        }

        Roles role = getRole(requestId, request.role());
        LocalDateTime now = LocalDateTime.now();
        Customer customer = Customer.builder()
                .fullName(StringUtils.trim(request.name()))
                .email(StringUtils.lowerCase(StringUtils.trim(request.email())))
                .mobileNumber(StringUtils.trim(request.mobileNumber()))
                .countryCode(StringUtils.trimToNull(request.countryCode()))
                .password(passwordEncoder.encode(request.password()))
                .customerUniqueId(UUID.randomUUID().toString())
                .status(Constants.ACCOUNT_STATUS_ACTIVE)
                .role(role)
                .addBy(Constants.ADD_MODIFY_ETL)
                .modBy(Constants.ADD_MODIFY_ETL)
                .addDate(now)
                .modDate(now)
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        log.info("RequestID: {} | Customer created successfully : {}", requestId, savedCustomer.getCustomerId());
        return new ApiRestResponse("Customer created successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public ApiRestResponse getCustomers(String requestId, PageRequestDto pageRequestDto) {

        log.info("RequestId: {} | Fetching customers | page: {} | size: {}", requestId, pageRequestDto.page(),
                pageRequestDto.size());

        PageRequest pageRequest = PageRequest.of(pageRequestDto.page(), pageRequestDto.size(),
                Sort.by(Sort.Direction.DESC, "addDate", "customerId"));

        Page<Customer> customers = customerRepository.findAll(pageRequest);

        List<CustomerDto> customerDtoList = customerMapper.toCustomerDtoList(customers.getContent());

        log.info("RequestId: {} | Customers fetched successfully | recordsInPage: {} | totalElements: {} | totalPages: {}",
                requestId, customers.getNumberOfElements(), customers.getTotalElements(), customers.getTotalPages());

        return new ApiRestResponse(Map.of(
                "customers", customerDtoList,
                "page", customers.getNumber() + 1,
                "size", customers.getSize(),
                "totalElements", customers.getTotalElements(),
                "totalPages", customers.getTotalPages()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiRestResponse getCustomer(String requestId, Long customerId) {
        log.info("RequestID: {} | Fetching customer | customerId: {}", requestId, customerId);
        Customer customer =  customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.error("RequestId: {} | Customer not found | CustomerId: {}", requestId, customerId);
                    return new NotFoundException("Customer not found with id: " + customerId);
                });
        CustomerDto customerDto = customerMapper.toCustomerDto(customer);
        return new ApiRestResponse(customerDto);
    }

    @Override
    public ApiRestResponse updateCustomer(String requestId,
                                          Long customerId,
                                          CustomerUpdateRequest request) {

        log.info("RequestId: {} | Updating customer | customerId: {}", requestId, customerId);

        Customer customer = getCustomerById(requestId, customerId);
        if (StringUtils.isNotBlank(request.email())) {
            String email = StringUtils.lowerCase(StringUtils.trim(request.email()));
            if (!StringUtils.equals(email, customer.getEmail())
                    && customerRepository.existsByEmailAndCustomerIdNot(email, customerId)) {
                log.error("RequestID: {} | Customer already exists with email | customerId: {}", requestId, customerId);
                throw new UserExistsException("Customer already exists with email: " + email);
            }
            customer.setEmail(email);
        }


        customer.setFullName(request.name());
        customer.setMobileNumber(request.mobileNumber());
        customer.setCountryCode(request.countryCode());
        customer.setRole(getRole(requestId, request.role()));
        customer.setStatus((ObjectUtils.isEmpty(request.active()) && request.active()) ?
                Constants.ACCOUNT_STATUS_ACTIVE : Constants.ACCOUNT_STATUS_INACTIVE);

        customer.setModBy(Constants.ADD_MODIFY_ETL);
        customer.setModDate(LocalDateTime.now());
        customerRepository.save(customer);
        log.info("RequestId: {} | Customer updated successfully | customerId: {}",
                requestId, customerId);

        return new ApiRestResponse("Customer updated successfully");
    }

    @Override
    public ApiRestResponse UpdateCustomerStatus(String requestId, Long customerId, Boolean active) throws InvalidDataException {

        if (ObjectUtils.isEmpty(active)) {
            log.error("RequestId: {} | Account Status Change cannot be empty for Customer: {}", requestId, customerId);
            throw new InvalidDataException("Status cannot be empty. Please try again");
        }

        String status = active ? Constants.ACCOUNT_STATUS_ACTIVE : Constants.ACCOUNT_STATUS_INACTIVE;
        log.info("RequestId: {} | Received Request to {} for owner:  {}", requestId, status, customerId);
        getCustomerById(requestId, customerId);
        String user = getLoggedInUser();

        customerRepository.updateStatusByCustomerId(status, user, LocalDateTime.now(), customerId);
        return new ApiRestResponse(Map.of("message", String.format("Account %s failed",
                active ? "enabled" : "disabled")));
    }

    private Customer getCustomerById(String requestId, Long customerId) {
        log.info("RequestId: {} | Fetching customer details | customerId: {}",
                requestId, customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.error("RequestId: {} | Customer not found | customerId: {}", requestId, customerId);
                    return new NotFoundException("Customer not found with id: " + customerId);
                });

        log.info("RequestId: {} | Customer found | customerId: {}", requestId, customerId);
        return customer;
    }

    private Roles getRole(String requestId, String roleName) {
        String trimmedRoleName = StringUtils.trim(roleName);
        log.info("RequestId: {} | Fetching role details | roleName: {}", requestId, trimmedRoleName);
        Roles role = rolesRepository.findByRoleName(trimmedRoleName)
                .orElseThrow(() -> {
                    log.error("RequestId: {} | Customer not found | roleName: {}", requestId, trimmedRoleName);
                    return new NotFoundException("Role not found with name: " + roleName);
                });

        log.info("RequestId: {} | Role found | roleName: {}", requestId, trimmedRoleName);
        return role;
    }

    private String getLoggedInUser() {
        UserPrincipal authentication = (UserPrincipal) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();
        return Objects.requireNonNull(authentication).getUsername();
    }
}
