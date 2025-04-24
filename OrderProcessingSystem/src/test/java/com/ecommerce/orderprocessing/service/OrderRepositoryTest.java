package com.ecommerce.orderprocessing.service;

import com.ecommerce.orderprocessing.model.Order;
import com.ecommerce.orderprocessing.model.OrderItem;
import com.ecommerce.orderprocessing.model.OrderStatus;
import com.ecommerce.orderprocessing.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setCustomerId("CUST123");
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        OrderItem item = new OrderItem();
        item.setProductName("Laptop");
        item.setQuantity(1);
        item.setPrice(1000.0);
        order.getItems().add(item);

        orderRepository.save(order);
    }

    @Test
    void findByStatus_ShouldReturnOrdersWithGivenStatus() {
        List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);
        assertThat(pendingOrders).hasSize(1);
        assertThat(pendingOrders.get(0).getCustomerId()).isEqualTo("CUST123");
    }

    @Test
    void findByStatus_NoOrdersWithStatus_ShouldReturnEmptyList() {
        List<Order> shippedOrders = orderRepository.findByStatus(OrderStatus.SHIPPED);
        assertThat(shippedOrders).isEmpty();
    }

    @Test
    void findById_ShouldReturnOrderWithItems() {
        Order foundOrder = orderRepository.findById(order.getId()).orElse(null);
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getItems()).hasSize(1);
        assertThat(foundOrder.getItems().get(0).getProductName()).isEqualTo("Laptop");
    }
}