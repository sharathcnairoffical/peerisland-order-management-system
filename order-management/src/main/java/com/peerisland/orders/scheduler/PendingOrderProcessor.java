package com.peerisland.orders.scheduler;

import com.peerisland.orders.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PendingOrderProcessor {

    private final OrderService orderService;

    @Scheduled(fixedRateString = "${orders.pending-to-processing-interval-ms:300000}")
    public void processPendingOrders() {
        log.info("Pending order processor started");
        orderService.processPendingOrders();
    }
}
