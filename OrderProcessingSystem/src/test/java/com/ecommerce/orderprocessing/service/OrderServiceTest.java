package com.ecommerce.orderprocessing.service;

import com.ecommerce.orderprocessing.model.Order;
import com.ecommerce.orderprocessing.model.OrderItem;
import com.ecommerce.orderprocessing.model.OrderStatus;
import com.ecommerce.orderprocessing.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setCustomerId("CUST123");
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        OrderItem item = new OrderItem();
        item.setProductName("Laptop");
        item.setQuantity(1);
        item.setPrice(1000.0);
        order.getItems().add(item);
    }

    @Test
    void createOrder_ShouldSetPendingStatusAndSave() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order createdOrder = orderService.createOrder(order);

        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(createdOrder.getCreatedAt()).isNotNull();
        verify(orderRepository).save(order);
    }

    @Test
    void getOrderById_ExistingOrder_ShouldReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.getOrderById(1L);

        assertThat(foundOrder).isEqualTo(order);
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_NonExistingOrder_ShouldThrowException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> orderService.getOrderById(999L));
        verify(orderRepository).findById(999L);
    }

    @Test
    void getAllOrders_NoStatusFilter_ShouldReturnAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<Order> orders = orderService.getAllOrders(null);

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0)).isEqualTo(order);
        verify(orderRepository).findAll();
    }

    @Test
    void getAllOrders_WithStatusFilter_ShouldReturnFilteredOrders() {
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(List.of(order));

        List<Order> orders = orderService.getAllOrders(OrderStatus.PENDING);

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(orderRepository).findByStatus(OrderStatus.PENDING);
    }

    @Test
    void updateOrderStatus_ValidOrder_ShouldUpdateStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.updateOrderStatus(1L, OrderStatus.PROCESSING);

        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PROCESSING);
        assertThat(updatedOrder.getUpdatedAt()).isNotNull();
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_NonExistingOrder_ShouldThrowException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> orderService.updateOrderStatus(999L, OrderStatus.PROCESSING));
        verify(orderRepository).findById(999L);
    }

    @Test
    void cancelOrder_PendingOrder_ShouldDeleteOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        verify(orderRepository).delete(order);
    }

    @Test
    void cancelOrder_NonPendingOrder_ShouldThrowException() {
        order.setStatus(OrderStatus.PROCESSING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(1L));
        verify(orderRepository, never()).delete(any());
    }

    @Test
    void cancelOrder_NonExistingOrder_ShouldThrowException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> orderService.cancelOrder(999L));
        verify(orderRepository, never()).delete(any());
    }
}