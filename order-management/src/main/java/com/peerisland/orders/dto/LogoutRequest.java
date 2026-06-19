package com.peerisland.orders.dto;

public record LogoutRequest(String accessToken, String refreshToken) {
}
