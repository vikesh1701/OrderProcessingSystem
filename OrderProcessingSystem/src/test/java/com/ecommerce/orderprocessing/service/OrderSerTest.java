package com.ecommerce.orderprocessing.service;

import com.ecommerce.orderprocessing.model.Order;
import com.ecommerce.orderprocessing.model.OrderStatus;
import com.ecommerce.orderprocessing.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderSerTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void createOrder_ShouldSetPendingStatus() {
        Order order = new Order();
        order.setCustomerId("CUST123");

        Order savedOrder = orderService.createOrder(order);

        assertNotNull(savedOrder.getId());
        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());
        assertNotNull(savedOrder.getCreatedAt());
    }

    @Test
    void cancelOrder_NonPendingOrder_ShouldThrowException() {
        Order order = new Order();
        order.setCustomerId("CUST123");
        order = orderService.createOrder(order);
        orderService.updateOrderStatus(order.getId(), OrderStatus.PROCESSING);

        Order finalOrder = order;
        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(finalOrder.getId()));
    }

    @Test
    void cancelOrder_NonPendingOrder_ShouldThrowException2() {
        Order order = new Order();
        order.setCustomerId("CUST1234");
        order = orderService.createOrder(order);
        orderService.updateOrderStatus(order.getId(), OrderStatus.DELIVERED);

        Order finalOrder = order;
        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(finalOrder.getId()));
    }

    @Test
    void cancelOrder_NonPendingOrder_ShouldThrowException3() {
        Order order = new Order();
        order.setCustomerId("CUST12345");
        order = orderService.createOrder(order);
        orderService.updateOrderStatus(order.getId(), OrderStatus.SHIPPED);

        Order finalOrder = order;
        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(finalOrder.getId()));
    }

    @Test
    void getOrderById_NotFound_ShouldThrowException() {
        assertThrows(NoSuchElementException.class, () -> orderService.getOrderById(999L));
    }
}