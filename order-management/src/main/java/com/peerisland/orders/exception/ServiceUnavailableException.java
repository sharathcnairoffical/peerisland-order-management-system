package com.peerisland.orders.exception;

import lombok.Getter;

@Getter
public class ServiceUnavailableException extends RuntimeException {

    private final String arabicMessage;

    public ServiceUnavailableException(String message, String arabicMessage) {
        super(message);
        this.arabicMessage = arabicMessage;
    }

    public ServiceUnavailableException(String message, String arabicMessage, Throwable cause) {
        super(message, cause);
        this.arabicMessage = arabicMessage;
    }
}
