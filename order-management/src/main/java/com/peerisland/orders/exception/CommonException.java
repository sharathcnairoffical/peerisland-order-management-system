package com.peerisland.orders.exception;

import lombok.Getter;

@Getter
public class CommonException extends Exception {

    private final int status;
    private final String arabicMessage;

    public CommonException(String message, String arabicMessage, int status) {
        super(message);
        this.status = status;
        this.arabicMessage = arabicMessage;
    }
}
