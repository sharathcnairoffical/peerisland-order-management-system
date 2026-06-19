package com.peerisland.orders.service.order;


import com.peerisland.orders.dto.ApiRestResponse;
import com.peerisland.orders.dto.PageRequestDto;
import com.peerisland.orders.dto.order.OrderCreationRequest;
import com.peerisland.orders.dto.order.OrderStatusUpdateRequest;
import com.peerisland.orders.exception.InvalidDataException;

public interface OrderService {

    ApiRestResponse createOrder(String requestId, OrderCreationRequest request) throws InvalidDataException;

    ApiRestResponse getAllOrders(String requestId, PageRequestDto pageRequestDto, String status) throws InvalidDataException;

    ApiRestResponse getOrderById(String requestId, Long orderId);

    ApiRestResponse updateOrderStatus(String requestId, Long orderId, OrderStatusUpdateRequest request)
            throws InvalidDataException;

    ApiRestResponse cancelOrder(String requestId, Long orderId) throws InvalidDataException;

    int processPendingOrders();

}
