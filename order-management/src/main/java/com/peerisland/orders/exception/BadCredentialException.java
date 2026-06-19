package com.peerisland.orders.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BadCredentialException extends RuntimeException {

    private final int status;

    public BadCredentialException(String message, int status) {
        super(message);
        this.status = status;
    }
}