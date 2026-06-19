package com.peerisland.orders.service.profile;

import com.peerisland.orders.dto.ApiRestResponse;
import com.peerisland.orders.dto.LoginRequestDto;
import com.peerisland.orders.dto.LogoutRequest;
import com.peerisland.orders.dto.RefreshTokenDto;
import com.peerisland.orders.dto.customer.CustomerDto;
import com.peerisland.orders.exception.CommonException;

public interface ProfileService {

    ApiRestResponse login(String requestId, LoginRequestDto loginRequestDto);

    ApiRestResponse logout(String requestId, LogoutRequest requestToken);

    ApiRestResponse refreshToken(String requestId, RefreshTokenDto requestToken) throws CommonException;

    ApiRestResponse viewProfile(String requestId);

    CustomerDto getUserDetails(String username);

    ApiRestResponse resetPassword(String requestId, String username, String securityAnswer);
}
