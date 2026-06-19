package com.peerisland.orders.service.order;

import com.peerisland.orders.dto.ApiRestResponse;
import com.peerisland.orders.dto.PageRequestDto;
import com.peerisland.orders.dto.order.OrderCreationRequest;
import com.peerisland.orders.dto.order.OrderItemRequest;
import com.peerisland.orders.dto.order.OrderItemResponse;
import com.peerisland.orders.dto.order.OrderResponse;
import com.peerisland.orders.dto.order.OrderStatusUpdateRequest;
import com.peerisland.orders.entity.Customer;
import com.peerisland.orders.entity.CustomerAddress;
import com.peerisland.orders.entity.OrderDetails;
import com.peerisland.orders.entity.Orders;
import com.peerisland.orders.exception.InvalidDataException;
import com.peerisland.orders.exception.NotFoundException;
import com.peerisland.orders.repository.OrderRepository;
import com.peerisland.orders.enums.OrderStatus;
import com.peerisland.orders.utility.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public ApiRestResponse createOrder(String requestId, OrderCreationRequest request) throws InvalidDataException {
        log.info("RequestId: {} | Create order request received | customerId: {} | itemCount: {}",
                requestId, request.customerId(), request.items().size());

        Customer customer = findCustomer(request.customerId());
        CustomerAddress address = resolveAddress(request.addressId(), customer.getCustomerId());
        Integer orderType = request.orderType() == null ? Constants.DEFAULT_ORDER_TYPE : request.orderType();
        LocalDateTime now = LocalDateTime.now();
        String orderUniqueId = "ORD-" + UUID.randomUUID();

        Orders order = Orders.builder()
                .orderUniqueId(orderUniqueId)
                .customer(customer)
                .address(address)
                .orderStatus(OrderStatus.PENDING.getCode())
                .orderType(orderType)
                .branchId(request.branchId())
                .branchUniqueId(StringUtils.trimToNull(request.branchUniqueId()))
                .specialNotes(StringUtils.trimToNull(request.specialNotes()))
                .isCouponCodeUsed(Boolean.FALSE)
                .orderAmount(calculateOrderAmount(request.items()))
                .orderAmountWithoutDiscount(calculateOrderAmount(request.items()))
                .loyaltyDiscount(0.0)
                .loyalPointsEarned(0.0)
                .loyaltyOnOrder(0.0)
                .addBy(requestId)
                .modBy(requestId)
                .addDate(now)
                .modDate(now)
                .build();

        List<OrderDetails> orderDetails = request.items().stream()
                .map(item -> toOrderDetails(item, order, orderUniqueId, orderType, now, requestId))
                .toList();
        order.setOrderDetails(orderDetails);

        Orders savedOrder = orderRepository.save(order);
        log.info("RequestId: {} | Order created successfully | orderId: {} | orderUniqueId: {} | status: {}",
                requestId, savedOrder.getOrderId(), savedOrder.getOrderUniqueId(), OrderStatus.PENDING.name());

        return new ApiRestResponse(toOrderResponse(savedOrder));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiRestResponse getAllOrders(String requestId, PageRequestDto pageRequestDto, String status)
            throws InvalidDataException {
        OrderStatus orderStatus = StringUtils.isBlank(status) ? null : parseStatus(status);
        PageRequest pageRequest = PageRequest.of(
                pageRequestDto.page(),
                pageRequestDto.size(),
                Sort.by(Sort.Direction.DESC, "addDate", "orderId")
        );

        log.info("RequestId: {} | Fetching orders | page: {} | size: {} | status: {}",
                requestId, pageRequestDto.page(), pageRequestDto.size(), orderStatus == null ? "ALL" : orderStatus.name());

        Integer statusCode = orderStatus == null ? null : orderStatus.getCode();
        Page<OrderResponse> orderPage = orderRepository.findAllByStatus(statusCode, pageRequest)
                .map(this::toOrderResponse);

        log.info("RequestId: {} | Orders fetched successfully | recordsInPage: {} | totalElements: {} | totalPages: {}",
                requestId, orderPage.getNumberOfElements(), orderPage.getTotalElements(), orderPage.getTotalPages());

        return new ApiRestResponse(Map.of(
                "orders", orderPage.getContent(),
                "page", orderPage.getNumber() + 1,
                "size", orderPage.getSize(),
                "totalElements", orderPage.getTotalElements(),
                "totalPages", orderPage.getTotalPages()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiRestResponse getOrderById(String requestId, Long orderId) {
        log.info("RequestId: {} | Fetching order details | orderId: {}", requestId, orderId);
        Orders order = getOrderOrThrow(orderId);
        log.info("RequestId: {} | Order details fetched successfully | orderId: {} | status: {}",
                requestId, orderId, OrderStatus.fromCode(order.getOrderStatus()).name());
        return new ApiRestResponse(toOrderResponse(order));
    }

    @Override
    public ApiRestResponse updateOrderStatus(String requestId, Long orderId, OrderStatusUpdateRequest request)
            throws InvalidDataException {
        OrderStatus targetStatus = parseStatus(request.status());
        if (targetStatus == OrderStatus.CANCELLED) {
            throw new InvalidDataException("Use cancel order API to cancel an order");
        }

        log.info("RequestId: {} | Update order status request received | orderId: {} | targetStatus: {}",
                requestId, orderId, targetStatus.name());

        Orders order = getOrderOrThrow(orderId);
        OrderStatus currentStatus = OrderStatus.fromCode(order.getOrderStatus());
        order.setOrderStatus(targetStatus.getCode());
        order.setModBy(requestId);
        order.setModDate(LocalDateTime.now());
        if (targetStatus == OrderStatus.DELIVERED) {
            order.setOrderCompleteDate(LocalDateTime.now());
        }

        Orders savedOrder = orderRepository.save(order);
        log.info("RequestId: {} | Order status updated | orderId: {} | fromStatus: {} | toStatus: {}",
                requestId, orderId, currentStatus.name(), targetStatus.name());
        return new ApiRestResponse(toOrderResponse(savedOrder));
    }

    @Override
    public ApiRestResponse cancelOrder(String requestId, Long orderId) throws InvalidDataException {
        log.info("RequestId: {} | Cancel order request received | orderId: {}", requestId, orderId);

        Orders order = getOrderOrThrow(orderId);
        OrderStatus currentStatus = OrderStatus.fromCode(order.getOrderStatus());
        if (currentStatus != OrderStatus.PENDING) {
            log.warn("RequestId: {} | Cancel order rejected | orderId: {} | currentStatus: {}",
                    requestId, orderId, currentStatus.name());
            throw new InvalidDataException("Order can be cancelled only in PENDING status");
        }

        order.setOrderStatus(OrderStatus.CANCELLED.getCode());
        order.setModBy(requestId);
        order.setModDate(LocalDateTime.now());

        Orders savedOrder = orderRepository.save(order);
        log.info("RequestId: {} | Order cancelled successfully | orderId: {}", requestId, orderId);
        return new ApiRestResponse(toOrderResponse(savedOrder));
    }

    @Override
    public int processPendingOrders() {
        LocalDateTime now = LocalDateTime.now();
        int updatedRows = orderRepository.updatePendingOrdersToProcessing(
                OrderStatus.PENDING.getCode(),
                OrderStatus.PROCESSING.getCode(),
                Constants.ADD_MODIFY_ETL,
                now
        );

        log.info("Pending order processor completed | updatedRows: {}", updatedRows);
        return updatedRows;
    }

    private Customer findCustomer(Long customerId) {
        return orderRepository.findCustomerById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + customerId));
    }

    private CustomerAddress resolveAddress(Long addressId, Long customerId) throws InvalidDataException {
        if (addressId == null) {
            return null;
        }

        CustomerAddress address = orderRepository.findCustomerAddressById(addressId)
                .orElseThrow(() -> new NotFoundException("Customer address not found with id: " + addressId));
        if (!address.getCustomer().getCustomerId().equals(customerId)) {
            throw new InvalidDataException("Address does not belong to customer");
        }
        return address;
    }

    private OrderDetails toOrderDetails(OrderItemRequest item,
                                        Orders order,
                                        String orderUniqueId,
                                        Integer orderType,
                                        LocalDateTime now,
                                        String requestId) {
        return OrderDetails.builder()
                .orders(order)
                .orderUniqueId(orderUniqueId)
                .orderType(orderType)
                .imageUrl(StringUtils.trimToNull(item.imageUrl()))
                .productId(item.productId())
                .name(StringUtils.trim(item.name()))
                .quantity(item.quantity())
                .productPrice(item.productPrice())
                .addBy(requestId)
                .modBy(requestId)
                .addDate(now)
                .modDate(now)
                .build();
    }

    private double calculateOrderAmount(List<OrderItemRequest> items) {
        return items.stream()
                .mapToDouble(item -> item.quantity() * item.productPrice())
                .sum();
    }

    private Orders getOrderOrThrow(Long orderId) {
        return orderRepository.findOrderByIdWithDetails(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));
    }

    private OrderStatus parseStatus(String status) throws InvalidDataException {
        try {
            return OrderStatus.from(status);
        } catch (IllegalArgumentException exception) {
            throw new InvalidDataException("Invalid order status: " + status);
        }
    }

    private OrderResponse toOrderResponse(Orders order) {
        List<OrderItemResponse> items = order.getOrderDetails() == null
                ? List.of()
                : order.getOrderDetails().stream().map(this::toOrderItemResponse).toList();

        return new OrderResponse(
                order.getOrderId(),
                order.getOrderUniqueId(),
                order.getCustomer().getCustomerId(),
                OrderStatus.fromCode(order.getOrderStatus()).name(),
                order.getAddress() == null ? null : order.getAddress().getAddressId(),
                order.getOrderType(),
                order.getBranchId(),
                order.getBranchUniqueId(),
                round(order.getOrderAmount()),
                order.getSpecialNotes(),
                order.getAddDate(),
                order.getModDate(),
                order.getOrderCompleteDate(),
                items
        );
    }

    private OrderItemResponse toOrderItemResponse(OrderDetails orderDetails) {
        double lineTotal = nullSafe(orderDetails.getProductPrice()) * nullSafe(orderDetails.getQuantity());
        return new OrderItemResponse(
                orderDetails.getOrderDetailId(),
                orderDetails.getProductId(),
                orderDetails.getName(),
                orderDetails.getQuantity(),
                round(orderDetails.getProductPrice()),
                round(lineTotal),
                orderDetails.getImageUrl()
        );
    }

    private double nullSafe(Double value) {
        return value == null ? 0.0 : value;
    }

    private int nullSafe(Integer value) {
        return value == null ? 0 : value;
    }

    private double round(Double value) {
        return Math.round(nullSafe(value) * 100.0) / 100.0;
    }
}
