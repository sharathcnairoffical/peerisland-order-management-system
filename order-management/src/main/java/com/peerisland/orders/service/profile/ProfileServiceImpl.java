package com.peerisland.orders.service.profile;

import com.peerisland.orders.cache.InMemoryCacheService;
import com.peerisland.orders.dto.ApiRestResponse;
import com.peerisland.orders.dto.LoginRequestDto;
import com.peerisland.orders.dto.LogoutRequest;
import com.peerisland.orders.dto.RefreshTokenDto;
import com.peerisland.orders.dto.customer.CustomerDto;
import com.peerisland.orders.entity.Customer;
import com.peerisland.orders.exception.CommonException;
import com.peerisland.orders.exception.NotFoundException;
import com.peerisland.orders.mapper.CustomerMapper;
import com.peerisland.orders.repository.CustomerRepository;
import com.peerisland.orders.security.models.UserPrincipal;
import com.peerisland.orders.security.service.JWTService;
import com.peerisland.orders.utility.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{

    @Value("${jwt.access.expiry}")
    private Long accessTokenExpiry;

    @Value("${jwt.refresh.expiry}")
    private Long refreshTokenExpiry;

    private final JWTService jwtService;

    private final CustomerMapper customerMapper;

    private final CustomerRepository customerRepository;

    private final AuthenticationManager authenticationManager;

    private final InMemoryCacheService inMemoryCacheService;

    @Override
    public ApiRestResponse login(String requestId, LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.password()));

        if (authentication.isAuthenticated()) {
            log.info("RequestId: {} | Owner authenticated successfully", requestId);
            String accessToken = jwtService.generateToken(loginRequestDto.email(), accessTokenExpiry);
            String refreshToken = jwtService.generateToken(loginRequestDto.email(), refreshTokenExpiry);
            return new ApiRestResponse(Map.of(
                    Constants.ACCESS_TOKEN_LITERAL, accessToken,
                    Constants.REFRESH_TOKEN_LITERAL, refreshToken
            ));
        }

        log.error("RequestId: {} | Invalid email or password", requestId);
        return new ApiRestResponse(Boolean.FALSE, Boolean.TRUE, "Invalid credentials", null);
    }

    @Override
    public ApiRestResponse logout(String requestId, LogoutRequest requestToken) {
        log.info("RequestId: {} | Owner logged out request", requestId);
        UserPrincipal authentication = (UserPrincipal) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();
        String username = Objects.requireNonNull(authentication).getUsername();
        log.info("RequestId: {} | Logout requested | username: {}", requestId, username);
        // Blacklist Access Token (if exists)
        if (StringUtils.isNotEmpty(requestToken.accessToken())) {
            String accessToken = removeBearer(requestToken.accessToken());
            long accessExpiry = jwtService.getRemainingExpiry(accessToken);
            blacklistAccessOrRefreshToken(accessToken, accessExpiry, Boolean.TRUE);
        }

        // Blacklist Refresh Token (mandatory)
        String refreshToken = removeBearer(requestToken.refreshToken());
        long refreshExpiry = jwtService.getRemainingExpiry(refreshToken);
        blacklistAccessOrRefreshToken(refreshToken, refreshExpiry, Boolean.FALSE);
        return new ApiRestResponse(Map.of("message", "Logged out successfully"));
    }

    @Override
    public ApiRestResponse refreshToken(String requestId, RefreshTokenDto requestToken)
            throws CommonException {
        String email = jwtService.extractUserName(requestToken.refreshToken());
        if (email == null || !jwtService.validateToken(requestToken.refreshToken(), email)) {
            log.error("RequestID: {} | Invalid refresh token", requestId);
            return new ApiRestResponse(false, true, "Invalid refresh token", null);
        }
        String hashedToken = DigestUtils.sha256Hex(requestToken.refreshToken());
        String key = Constants.BLACKLISTED_REFRESH_TOKEN_PREFIX.concat(hashedToken);
        if (inMemoryCacheService.contains(key)) {
            log.error("RequestID: {} | Token is blacklisted", requestId);
            throw new CommonException("Invalid refresh Token", HttpStatus.UNAUTHORIZED.value());
        }
        String newAccessToken = jwtService.generateToken(email, accessTokenExpiry);
        String newRefreshToken = jwtService.generateToken(email, refreshTokenExpiry);

        return new ApiRestResponse(Map.of(
                Constants.ACCESS_TOKEN_LITERAL, newAccessToken,
                Constants.REFRESH_TOKEN_LITERAL, newRefreshToken
        ));
    }

    @Override
    public ApiRestResponse viewProfile(String requestId) {
        UserPrincipal authentication = (UserPrincipal) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();
        String user = Objects.requireNonNull(authentication).getUsername();
        CustomerDto customerDto = getUserDetails(user);
        return new ApiRestResponse(customerDto);
    }

    private String removeBearer(String token) {
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    private void blacklistAccessOrRefreshToken(String token, long expiryMillis, Boolean isAccessToken) {
        String hashedToken = DigestUtils.sha256Hex(token);
        String key = StringUtils.EMPTY;
        if (isAccessToken) {
            key = Constants.BLACKLISTED_ACCESS_TOKEN_PREFIX.concat(hashedToken);
        } else {
            key = Constants.BLACKLISTED_REFRESH_TOKEN_PREFIX.concat(hashedToken);
        }

        inMemoryCacheService.put(key, Boolean.TRUE, expiryMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public CustomerDto getUserDetails(String username) {
        String cacheKey = Constants.CUSTOMER_CACHE_PREFIX.concat(username);
        CustomerDto customerDto = inMemoryCacheService.get(cacheKey, CustomerDto.class)
                .orElseGet(() -> loadAndCacheCustomer(username, cacheKey));
        if (!ObjectUtils.isEmpty(customerDto)) {
            return customerDto;
        }
        return null;
    }

    @Override
    public ApiRestResponse resetPassword(String requestId, String username, String securityAnswer) {
        log.warn("RequestId: {} | Reset password requested but not implemented | username: {}",
                requestId, username);
        return new ApiRestResponse("Reset password is not implemented", true);
    }

    private CustomerDto loadAndCacheCustomer(String username, String cacheKey) {
        Customer customer = customerRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.error("Owner not found with email : {}", username);
                    return new NotFoundException("Owner not found with email: " + username);
                });
        CustomerDto customerDto = customerMapper.toCustomerDto(customer);
        inMemoryCacheService.put(
                cacheKey,
                customerDto,
                Constants.CUSTOMER_CACHE_EXPIRY,
                TimeUnit.MINUTES);
        return customerDto;
    }
}
