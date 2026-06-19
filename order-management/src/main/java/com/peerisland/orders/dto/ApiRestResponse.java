package com.peerisland.orders.dto;

public record ApiRestResponse(
        Boolean isSuccess,
        Boolean isError,
        String errorMessage,
        Object data) {

    public ApiRestResponse(Object data) {
        this(true, false, null, data);
    }

    public ApiRestResponse(Object data, boolean isError) {
        this(!isError, isError, null, data);
    }

    public ApiRestResponse(String errorMessage, boolean isError) {
        this(!isError, isError, errorMessage,  null);
    }

}


