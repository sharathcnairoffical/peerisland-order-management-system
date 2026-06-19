package com.peerisland.orders.security.provider;


import com.peerisland.orders.entity.Customer;
import com.peerisland.orders.exception.BadCredentialException;
import com.peerisland.orders.exception.NotFoundException;
import com.peerisland.orders.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


import java.util.List;

/**
 * @author Sharath Nair
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomerRepository customerRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        Customer customer = customerRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("customer not found with email: " + username));

        var role = customer.getRole();
        log.info("CustomAuthenticationProvider: customer {} has role: {}", username, role.getRoleName());

        if (passwordEncoder.matches(password, customer.getPassword()) && customer.getEmail().equals(username)
                && customer.getActive()) {

            String authority = "ROLE_" + role.getRoleName();
            log.info("CustomAuthenticationProvider: Creating authentication with authority: {}", authority);
            return new UsernamePasswordAuthenticationToken(
                    customer.getEmail(),
                    null,
                    List.of(new SimpleGrantedAuthority(authority))
            );
        } else {
            throw new BadCredentialException("Invalid Credentials!", HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
