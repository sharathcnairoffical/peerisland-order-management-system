package com.peerisland.orders.controller;

import com.peerisland.orders.dto.ApiRestResponse;
import com.peerisland.orders.dto.PageRequestDto;
import com.peerisland.orders.dto.order.OrderCreationRequest;
import com.peerisland.orders.dto.order.OrderStatusUpdateRequest;
import com.peerisland.orders.exception.InvalidDataException;
import com.peerisland.orders.service.order.OrderService;
import com.peerisland.orders.utility.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiRestResponse> createOrder(@RequestHeader(value = Constants.REQUEST_ID) String requestId,
                                                       @Valid @RequestBody OrderCreationRequest request)
            throws InvalidDataException {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(requestId, request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiRestResponse> getOrderById(@RequestHeader(value = Constants.REQUEST_ID) String requestId,
                                                        @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(requestId, orderId));
    }

    @GetMapping
    public ResponseEntity<ApiRestResponse> getAllOrders(@RequestHeader(value = Constants.REQUEST_ID) String requestId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(required = false) String status)
            throws InvalidDataException {
        PageRequestDto pageRequestDto = PageRequestDto.builder().page(page).size(size).build();
        return ResponseEntity.ok(orderService.getAllOrders(requestId, pageRequestDto, status));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiRestResponse> updateOrderStatus(@RequestHeader(value = Constants.REQUEST_ID) String requestId,
                                                             @PathVariable Long orderId,
                                                             @Valid @RequestBody OrderStatusUpdateRequest request)
            throws InvalidDataException {
        return ResponseEntity.ok(orderService.updateOrderStatus(requestId, orderId, request));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiRestResponse> cancelOrder(@RequestHeader(value = Constants.REQUEST_ID) String requestId,
                                                       @PathVariable Long orderId)
            throws InvalidDataException {
        return ResponseEntity.ok(orderService.cancelOrder(requestId, orderId));
    }
}
