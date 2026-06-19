package com.peerisland.orders.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peerisland.orders.dto.ApiRestResponse;
import com.peerisland.orders.dto.PageRequestDto;
import com.peerisland.orders.dto.order.OrderCreationRequest;
import com.peerisland.orders.dto.order.OrderStatusUpdateRequest;
import com.peerisland.orders.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private static final String REQUEST_ID = "test-request-id";

    @Mock
    private OrderService orderService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createOrderReturnsCreatedResponse() throws Exception {
        when(orderService.createOrder(eq(REQUEST_ID), any(OrderCreationRequest.class)))
                .thenReturn(new ApiRestResponse(Map.of("orderId", 10L, "status", "PENDING")));

        mockMvc.perform(post("/v1/orders")
                        .header("requestId", REQUEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderPayload())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data.orderId").value(10))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        ArgumentCaptor<OrderCreationRequest> requestCaptor = ArgumentCaptor.forClass(OrderCreationRequest.class);
        verify(orderService).createOrder(eq(REQUEST_ID), requestCaptor.capture());
        assertThat(requestCaptor.getValue().customerId()).isEqualTo(1L);
        assertThat(requestCaptor.getValue().items()).hasSize(2);
    }

    @Test
    void getOrderByIdReturnsOrderDetails() throws Exception {
        when(orderService.getOrderById(REQUEST_ID, 10L))
                .thenReturn(new ApiRestResponse(Map.of("orderId", 10L, "status", "PENDING")));

        mockMvc.perform(get("/v1/orders/{orderId}", 10L)
                        .header("requestId", REQUEST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(10))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        verify(orderService).getOrderById(REQUEST_ID, 10L);
    }

    @Test
    void getAllOrdersPassesPaginationAndOptionalStatus() throws Exception {
        when(orderService.getAllOrders(eq(REQUEST_ID), any(PageRequestDto.class), eq("PENDING")))
                .thenReturn(new ApiRestResponse(Map.of(
                        "orders", List.of(),
                        "page", 1,
                        "size", 5,
                        "totalElements", 0,
                        "totalPages", 0
                )));

        mockMvc.perform(get("/v1/orders")
                        .header("requestId", REQUEST_ID)
                        .param("page", "0")
                        .param("size", "5")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size").value(5))
                .andExpect(jsonPath("$.data.totalElements").value(0));

        ArgumentCaptor<PageRequestDto> pageRequestCaptor = ArgumentCaptor.forClass(PageRequestDto.class);
        verify(orderService).getAllOrders(eq(REQUEST_ID), pageRequestCaptor.capture(), eq("PENDING"));
        assertThat(pageRequestCaptor.getValue().page()).isZero();
        assertThat(pageRequestCaptor.getValue().size()).isEqualTo(5);
    }

    @Test
    void updateOrderStatusReturnsUpdatedOrder() throws Exception {
        when(orderService.updateOrderStatus(eq(REQUEST_ID), eq(10L), any(OrderStatusUpdateRequest.class)))
                .thenReturn(new ApiRestResponse(Map.of("orderId", 10L, "status", "SHIPPED")));

        mockMvc.perform(patch("/v1/orders/{orderId}/status", 10L)
                        .header("requestId", REQUEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", "SHIPPED"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SHIPPED"));

        ArgumentCaptor<OrderStatusUpdateRequest> requestCaptor =
                ArgumentCaptor.forClass(OrderStatusUpdateRequest.class);
        verify(orderService).updateOrderStatus(eq(REQUEST_ID), eq(10L), requestCaptor.capture());
        assertThat(requestCaptor.getValue().status()).isEqualTo("SHIPPED");
    }

    @Test
    void cancelOrderReturnsCancelledOrder() throws Exception {
        when(orderService.cancelOrder(REQUEST_ID, 10L))
                .thenReturn(new ApiRestResponse(Map.of("orderId", 10L, "status", "CANCELLED")));

        mockMvc.perform(patch("/v1/orders/{orderId}/cancel", 10L)
                        .header("requestId", REQUEST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        verify(orderService).cancelOrder(REQUEST_ID, 10L);
    }

    private Map<String, Object> createOrderPayload() {
        return Map.of(
                "customerId", 1L,
                "orderType", 1,
                "branchId", 101L,
                "branchUniqueId", "BR-101",
                "specialNotes", "Unit test order",
                "items", List.of(
                        Map.of("productId", 1001L, "name", "Coffee", "quantity", 2, "productPrice", 120.50),
                        Map.of("productId", 1002L, "name", "Sandwich", "quantity", 1, "productPrice", 180.00)
                )
        );
    }
}
