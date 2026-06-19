package com.peerisland.orders.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Map;

public class Utility {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static ObjectMapper getMapper() {
        return MAPPER;
    }


    public static final Map<String, Integer> ORDER_STATUS_MAP =
            Map.ofEntries(
                    Map.entry(Constants.ORDER_RECEIVED, 1),
                    Map.entry(Constants.ORDER_PREPARING, 2),
                    Map.entry(Constants.ORDER_DELIVERED, 3),
                    Map.entry(Constants.ORDER_CANCELLED, 4),
                    Map.entry(Constants.ORDER_READY, 5),
                    Map.entry(Constants.ORDER_PENDING, 6),
                    Map.entry(Constants.ORDER_OUT_FOR_DELIVERY, 7),
                    Map.entry(Constants.ORDER_REOPEN, 8),
                    Map.entry(Constants.ORDER_PAYMENT_FAILED, 9),
                    Map.entry(Constants.ORDER_WAITING, 10),
                    Map.entry(Constants.ORDER_PAYMENT_PENDING, 11)
            );

}
