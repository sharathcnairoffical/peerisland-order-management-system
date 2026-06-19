package com.peerisland.orders.security.service;

import com.peerisland.orders.cache.InMemoryCacheService;
import com.peerisland.orders.dto.customer.CustomerDto;
import com.peerisland.orders.entity.Customer;
import com.peerisland.orders.exception.NotFoundException;
import com.peerisland.orders.mapper.CustomerMapper;
import com.peerisland.orders.repository.CustomerRepository;
import com.peerisland.orders.security.models.UserPrincipal;
import com.peerisland.orders.utility.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;
    private final InMemoryCacheService inMemoryCacheService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String cacheKey = Constants.CUSTOMER_CACHE_PREFIX.concat("AUTH-").concat(username);
        return inMemoryCacheService.get(cacheKey, UserPrincipal.class)
                .map(UserDetails.class::cast)
                .orElseGet(() -> loadAndCacheUserDetails(username, cacheKey));
    }

    private UserDetails loadAndCacheUserDetails(String username, String cacheKey) {
        Customer customer = customerRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("customer not found with email: " + username));
        CustomerDto customerDto = customerMapper.toCustomerDto(customer);
        var role = customerDto.role();

        log.debug("Loading customer: {}, Role from DB: {}", username, role.roleName());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.roleName()));


        log.debug("customer: {} final authorities: {}", username, authorities);

        UserPrincipal userPrincipal = new UserPrincipal(customerDto, customer.getPassword(), authorities);
        inMemoryCacheService.put(
                cacheKey,
                userPrincipal,
                Constants.CUSTOMER_CACHE_EXPIRY,
                TimeUnit.MINUTES);
        return userPrincipal;
    }
}
