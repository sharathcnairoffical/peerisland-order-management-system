package com.peerisland.orders.exception;

import lombok.Getter;

@Getter
public class UserExistsException extends RuntimeException {


    public UserExistsException(String englishMessage) {
        super(englishMessage);
    }
}
