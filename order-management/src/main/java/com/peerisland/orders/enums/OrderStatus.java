package com.peerisland.orders.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OrderStatus {
    PENDING(1),
    PROCESSING(2),
    SHIPPED(3),
    DELIVERED(4),
    CANCELLED(5);

    private final int code;

    OrderStatus(int code) {
        this.code = code;
    }

    public static OrderStatus from(String status) {
        return Arrays.stream(values())
                .filter(orderStatus -> orderStatus.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid order status: " + status));
    }

    public static OrderStatus fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(orderStatus -> orderStatus.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid order status code: " + code));
    }
}
