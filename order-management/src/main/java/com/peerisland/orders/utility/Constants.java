package com.peerisland.orders.utility;

public class Constants {

    private Constants() {
        throw new UnsupportedOperationException("Utility class");
    }


    public static final String ADD_MODIFY_ETL = "SYSTEM";

    public static final String BEARER_LITERAL = "Bearer ";

    public static final String ACCOUNT_STATUS_ACTIVE = "ACTIVE";
    public static final String ACCOUNT_STATUS_INACTIVE = "INACTIVE";

    public static final String REQUEST_ID = "requestId";

    public static final String CUSTOMER_OTP_LOGIN_CACHE_PREFIX = "CUSTOMER-OTP-LOGIN-";

    public static final String CUSTOMER_CACHE_PREFIX = "CUSTOMER-";
    public static final Integer CUSTOMER_CACHE_EXPIRY = 20;

    public static final Integer OTP_LENGTH = 6;
    public static final Integer TOKEN_EXPIRY = 3;

    public static final String ACCESS_TOKEN_LITERAL = "accessToken";
    public static final String REFRESH_TOKEN_LITERAL = "refreshToken";

    public static final String ORDER_PAYMENT_PENDING = "ORDER_PAYMENT_PENDING";
    public static final String ORDER_RECEIVED = "ORDER_RECEIVED";
    public static final String ORDER_PREPARING = "ORDER_PREPARING";
    public static final String ORDER_DELIVERED = "ORDER_DELIVERED";
    public static final String ORDER_CANCELLED = "ORDER_CANCELLED";
    public static final String ORDER_READY = "ORDER_READY";
    public static final String ORDER_PENDING = "ORDER_PENDING";
    public static final String ORDER_OUT_FOR_DELIVERY = "ORDER_OUT_FOR_DELIVERY";
    public static final String ORDER_REOPEN = "ORDER_REOPEN";
    public static final String ORDER_PAYMENT_FAILED = "ORDER_PAYMENT_FAILED";
    public static final String ORDER_WAITING = "ORDER_WAITING";

}
