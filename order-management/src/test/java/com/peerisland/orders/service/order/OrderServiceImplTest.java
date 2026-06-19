package com.peerisland.orders.service.order;

import com.peerisland.orders.dto.ApiRestResponse;
import com.peerisland.orders.dto.PageRequestDto;
import com.peerisland.orders.dto.order.OrderCreationRequest;
import com.peerisland.orders.dto.order.OrderItemRequest;
import com.peerisland.orders.dto.order.OrderResponse;
import com.peerisland.orders.dto.order.OrderStatusUpdateRequest;
import com.peerisland.orders.entity.Customer;
import com.peerisland.orders.entity.OrderDetails;
import com.peerisland.orders.entity.Orders;
import com.peerisland.orders.enums.OrderStatus;
import com.peerisland.orders.exception.InvalidDataException;
import com.peerisland.orders.repository.OrderRepository;
import com.peerisland.orders.utility.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    private static final String REQUEST_ID = "test-request-id";

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrderCalculatesAmountAndCreatesPendingOrder() throws InvalidDataException {
        Customer customer = customer(1L);
        when(orderRepository.findCustomerById(1L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Orders.class))).thenAnswer(invocation -> {
            Orders order = invocation.getArgument(0);
            order.setOrderId(10L);
            order.getOrderDetails().get(0).setOrderDetailId(100L);
            order.getOrderDetails().get(1).setOrderDetailId(101L);
            return order;
        });

        ApiRestResponse response = orderService.createOrder(REQUEST_ID, createOrderRequest());

        OrderResponse orderResponse = (OrderResponse) response.data();
        assertThat(orderResponse.orderId()).isEqualTo(10L);
        assertThat(orderResponse.status()).isEqualTo("PENDING");
        assertThat(orderResponse.orderAmount()).isEqualTo(421.00);
        assertThat(orderResponse.items()).hasSize(2);

        ArgumentCaptor<Orders> orderCaptor = ArgumentCaptor.forClass(Orders.class);
        verify(orderRepository).save(orderCaptor.capture());
        Orders savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getCustomer()).isSameAs(customer);
        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.PENDING.getCode());
        assertThat(savedOrder.getOrderAmount()).isEqualTo(421.00);
        assertThat(savedOrder.getOrderDetails())
                .allSatisfy(orderDetails -> assertThat(orderDetails.getOrders()).isSameAs(savedOrder));
    }

    @Test
    void getAllOrdersFiltersByStatus() throws InvalidDataException {
        Orders order = order(10L, OrderStatus.PENDING);
        when(orderRepository.findAllByStatus(eq(OrderStatus.PENDING.getCode()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(order)));

        ApiRestResponse response = orderService.getAllOrders(
                REQUEST_ID,
                PageRequestDto.builder().page(0).size(10).build(),
                "PENDING"
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.data();
        assertThat(data.get("totalElements")).isEqualTo(1L);
        assertThat(data.get("totalPages")).isEqualTo(1);
        assertThat(data.get("orders")).asList().hasSize(1);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(orderRepository).findAllByStatus(eq(OrderStatus.PENDING.getCode()), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(10);
    }

    @Test
    void updateOrderStatusRejectsCancelStatus() {
        assertThatThrownBy(() -> orderService.updateOrderStatus(
                REQUEST_ID,
                10L,
                new OrderStatusUpdateRequest("CANCELLED")
        ))
                .isInstanceOf(InvalidDataException.class)
                .hasMessage("Use cancel order API to cancel an order");

        verify(orderRepository, never()).findOrderByIdWithDetails(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderStatusUpdatesOrderAndSetsCompletionDateForDelivered() throws InvalidDataException {
        Orders order = order(10L, OrderStatus.SHIPPED);
        when(orderRepository.findOrderByIdWithDetails(10L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Orders.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiRestResponse response = orderService.updateOrderStatus(
                REQUEST_ID,
                10L,
                new OrderStatusUpdateRequest("DELIVERED")
        );

        OrderResponse orderResponse = (OrderResponse) response.data();
        assertThat(orderResponse.status()).isEqualTo("DELIVERED");
        assertThat(orderResponse.orderCompleteDate()).isNotNull();
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.DELIVERED.getCode());
        assertThat(order.getModBy()).isEqualTo(REQUEST_ID);
    }

    @Test
    void cancelOrderRejectsNonPendingOrder() {
        when(orderRepository.findOrderByIdWithDetails(10L))
                .thenReturn(Optional.of(order(10L, OrderStatus.PROCESSING)));

        assertThatThrownBy(() -> orderService.cancelOrder(REQUEST_ID, 10L))
                .isInstanceOf(InvalidDataException.class)
                .hasMessage("Order can be cancelled only in PENDING status");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancelOrderSetsStatusToCancelledWhenOrderIsPending() throws InvalidDataException {
        Orders order = order(10L, OrderStatus.PENDING);
        when(orderRepository.findOrderByIdWithDetails(10L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Orders.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ApiRestResponse response = orderService.cancelOrder(REQUEST_ID, 10L);

        OrderResponse orderResponse = (OrderResponse) response.data();
        assertThat(orderResponse.status()).isEqualTo("CANCELLED");
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED.getCode());
        assertThat(order.getModBy()).isEqualTo(REQUEST_ID);
    }

    @Test
    void processPendingOrdersMovesPendingOrdersToProcessing() {
        when(orderRepository.updatePendingOrdersToProcessing(
                eq(OrderStatus.PENDING.getCode()),
                eq(OrderStatus.PROCESSING.getCode()),
                eq(Constants.ADD_MODIFY_ETL),
                any(LocalDateTime.class)
        )).thenReturn(3);

        int updatedRows = orderService.processPendingOrders();

        assertThat(updatedRows).isEqualTo(3);
    }

    private OrderCreationRequest createOrderRequest() {
        return new OrderCreationRequest(
                1L,
                null,
                1,
                101L,
                "BR-101",
                "Unit test order",
                List.of(
                        new OrderItemRequest(1001L, "Coffee", 2, 120.50, null),
                        new OrderItemRequest(1002L, "Sandwich", 1, 180.00, null)
                )
        );
    }

    private Orders order(Long orderId, OrderStatus status) {
        Orders order = Orders.builder()
                .orderId(orderId)
                .orderUniqueId("ORD-" + orderId)
                .customer(customer(1L))
                .orderStatus(status.getCode())
                .orderType(1)
                .orderAmount(421.00)
                .orderAmountWithoutDiscount(421.00)
                .specialNotes("Unit test order")
                .addDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .build();
        OrderDetails orderDetails = OrderDetails.builder()
                .orderDetailId(100L)
                .orders(order)
                .orderUniqueId(order.getOrderUniqueId())
                .orderType(order.getOrderType())
                .productId(1001L)
                .name("Coffee")
                .quantity(2)
                .productPrice(120.50)
                .build();
        order.setOrderDetails(List.of(orderDetails));
        return order;
    }

    private Customer customer(Long customerId) {
        return Customer.builder()
                .customerId(customerId)
                .fullName("Sharath Nair")
                .email("sharath.nair@peerisland.com")
                .mobileNumber("9999999999")
                .status(Constants.ACCOUNT_STATUS_ACTIVE)
                .build();
    }
}
