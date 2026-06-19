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

    public static final String REFRESH_TOKEN_VALIDATION = "Refresh token is required.";


    public static final String CUSTOMER_OTP_LOGIN_CACHE_PREFIX = "CUSTOMER-OTP-LOGIN-";

    public static final String CUSTOMER_CACHE_PREFIX = "CUSTOMER-";
    public static final Integer CUSTOMER_CACHE_EXPIRY = 20;
    public static final String BLACKLISTED_ACCESS_TOKEN_PREFIX = "BLACKLISTED_ACCESS_TOKEN-";
    public static final String BLACKLISTED_REFRESH_TOKEN_PREFIX = "BLACKLISTED_REFRESH_TOKEN-";

    public static final Integer OTP_LENGTH = 6;
    public static final Integer TOKEN_EXPIRY = 3;

    public static final String ACCESS_TOKEN_LITERAL = "accessToken";
    public static final String REFRESH_TOKEN_LITERAL = "refreshToken";

    public static final Integer DEFAULT_ORDER_TYPE = 1;


}
