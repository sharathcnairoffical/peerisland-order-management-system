package com.peerisland.orders.repository;

import com.peerisland.orders.entity.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {

    List<OrderDetails> findByOrders_OrderId(Long ordersOrderId);
}
