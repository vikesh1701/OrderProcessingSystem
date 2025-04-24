package com.ecommerce.orderprocessing.service;

import com.ecommerce.orderprocessing.model.Order;
import com.ecommerce.orderprocessing.model.OrderStatus;
import com.ecommerce.orderprocessing.repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderStatusUpdateJob {

    private final OrderRepository orderRepository;

    public OrderStatusUpdateJob(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Scheduled(fixedDelay = 300000) // 5 minutes in milliseconds
    public void updatePendingOrders() {
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
        for (Order order : pendingOrders) {
            order.setStatus(OrderStatus.PROCESSING);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        }
    }
}