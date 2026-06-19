package com.peerisland.orders.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    public NotFoundException(String englishMessage) {
        super(englishMessage);
    }

}
