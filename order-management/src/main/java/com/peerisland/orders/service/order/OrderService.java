package com.peerisland.orders.service.order;


import com.peerisland.orders.dto.ApiRestResponse;
import com.peerisland.orders.dto.PageRequestDto;
import com.peerisland.orders.exception.CommonException;
import com.peerisland.orders.exception.InvalidDataException;

public interface OrderService {

    ApiRestResponse createOrder();

    ApiRestResponse getAllOrders(PageRequestDto pageRequestDto, String status);

    ApiRestResponse getOrderById(Long orderId) throws CommonException;

    ApiRestResponse updateOrderStatus(String orderId, String status) throws InvalidDataException;

    ApiRestResponse cancelOrder(String orderId) throws CommonException;


}
