package com.peerisland.orders.dto.customer;

public record LogoutRequest(String accessToken, String refreshToken) {
}
