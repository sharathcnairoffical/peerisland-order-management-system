package com.peerisland.orders.security.service;

import com.peerisland.orders.dto.customer.CustomerDto;
import com.peerisland.orders.entity.Customer;
import com.peerisland.orders.exception.NotFoundException;
import com.peerisland.orders.mapper.CustomerMapper;
import com.peerisland.orders.repository.CustomerRepository;
import com.peerisland.orders.security.models.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Customer customer = customerRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("customer not found with email: " + username));
        CustomerDto customerDto = customerMapper.toCustomerDto(customer);
        var role = customerDto.role();

        log.debug("Loading customer: {}, Role from DB: {}", username, role.roleName());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.roleName()));


        log.debug("customer: {} final authorities: {}", username, authorities);

        return new UserPrincipal(customerDto, customer.getPassword(), authorities);
    }
}
