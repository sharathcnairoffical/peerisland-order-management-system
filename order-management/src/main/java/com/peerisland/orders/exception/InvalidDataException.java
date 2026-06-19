package com.peerisland.orders.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class InvalidDataException extends Exception {

    private final int status;

    public InvalidDataException(String message, int status) {

        super(message);
        this.status = status;
    }

    public InvalidDataException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST.value();
    }
}