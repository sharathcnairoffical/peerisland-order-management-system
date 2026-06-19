package com.peerisland.orders.security.models;

import com.peerisland.orders.dto.customer.CustomerDto;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private final CustomerDto customer;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public String getCustomerUniqueId(){ return this.customer.customerUniqueId();}

    public Long getCustomerId(){ return this.customer.customerId();}

    public String getMobileNumber() {
        return customer.mobileNumber();
    }

    @Override
    public String getUsername() {
        return customer.email();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
