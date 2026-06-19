package com.peerisland.orders.exception;

import lombok.Getter;

@Getter
public class CommonException extends Exception {

    private final int status;

    public CommonException(String message, int status) {
        super(message);
        this.status = status;
    }
}
