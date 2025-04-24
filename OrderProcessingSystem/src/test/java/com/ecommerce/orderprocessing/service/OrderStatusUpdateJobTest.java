package com.ecommerce.orderprocessing.service;

import com.ecommerce.orderprocessing.model.Order;
import com.ecommerce.orderprocessing.model.OrderItem;
import com.ecommerce.orderprocessing.model.OrderStatus;
import com.ecommerce.orderprocessing.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderStatusUpdateJobTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusUpdateJob orderStatusUpdateJob;

    @Autowired
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void updatePendingOrders_ShouldChangePendingToProcessing() {
        Order order = new Order();
        order.setCustomerId("CUST123");
        OrderItem item = new OrderItem();
        item.setProductName("Laptop");
        item.setQuantity(1);
        item.setPrice(1000.0);
        order.getItems().add(item);
        orderService.createOrder(order);

        orderStatusUpdateJob.updatePendingOrders();

        List<Order> processingOrders = orderRepository.findByStatus(OrderStatus.PROCESSING);
        assertThat(processingOrders).hasSize(1);
        assertThat(processingOrders.getFirst().getCustomerId()).isEqualTo("CUST123");
    }

    @Test
    void updatePendingOrders_NoPendingOrders_ShouldNotChangeAnything() {
        Order order = new Order();
        order.setCustomerId("CUST123");
        order = orderService.createOrder(order);
        orderService.updateOrderStatus(order.getId(), OrderStatus.PROCESSING);

        orderStatusUpdateJob.updatePendingOrders();

        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
        assertThat(pendingOrders).isEmpty();
    }
}