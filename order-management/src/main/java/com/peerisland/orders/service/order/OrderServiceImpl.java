package com.peerisland.orders.service.order;

import com.peerisland.orders.dto.ApiRestResponse;
import com.peerisland.orders.dto.PageRequestDto;
import com.peerisland.orders.exception.CommonException;
import com.peerisland.orders.exception.InvalidDataException;

public class OrderServiceImpl implements OrderService {
    @Override
    public ApiRestResponse createOrder() {
        return null;
    }

    @Override
    public ApiRestResponse getAllOrders(PageRequestDto pageRequestDto, String status) {
        return null;
    }

    @Override
    public ApiRestResponse getOrderById(Long orderId) throws CommonException {
        return null;
    }

    @Override
    public ApiRestResponse updateOrderStatus(String orderId, String status) throws InvalidDataException {
        return null;
    }

    @Override
    public ApiRestResponse cancelOrder(String orderId) throws CommonException {
        return null;
    }
}
