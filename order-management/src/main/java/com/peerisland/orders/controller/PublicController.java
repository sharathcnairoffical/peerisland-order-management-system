package com.peerisland.orders.controller;

import com.peerisland.orders.dto.ApiRestResponse;
import com.peerisland.orders.dto.LoginRequestDto;
import com.peerisland.orders.dto.RefreshTokenDto;
import com.peerisland.orders.exception.CommonException;
import com.peerisland.orders.service.profile.ProfileService;
import com.peerisland.orders.utility.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public")
public class PublicController {

    private final ProfileService profileService;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @PostMapping("/v1/login")
    public ResponseEntity<ApiRestResponse> login(@RequestHeader(value = Constants.REQUEST_ID) String requestId,
                                                 @Valid @RequestBody LoginRequestDto loginRequestDto) {
        ApiRestResponse response = profileService.login(requestId, loginRequestDto);
        return ResponseEntity.status(response.isError() ? HttpStatus.UNAUTHORIZED : HttpStatus.OK).body(response);
    }

    @PostMapping("/v1/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader(value = Constants.REQUEST_ID) String requestId,
                                          @RequestBody RefreshTokenDto requestToken) throws CommonException {
        if (StringUtils.isEmpty(requestToken.refreshToken())) {
            return ResponseEntity.badRequest().body(Constants.REFRESH_TOKEN_VALIDATION);
        }
        ApiRestResponse response = profileService.refreshToken(requestId, requestToken);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/v1/reset-password/{username}")
    //@PreAuthorize("hasAnyRole('ADMIN') and hasAuthority('EDIT')")
    public ResponseEntity<ApiRestResponse> resetPassword(@RequestHeader(value = Constants.REQUEST_ID) String requestId,
                                                         @RequestHeader(value = "security_answer") String securityAnswer,
                                                         @PathVariable(name = "username") String username) {
        ApiRestResponse response = profileService.resetPassword(requestId, username, securityAnswer);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
