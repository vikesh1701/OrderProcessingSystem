package com.ecommerce.orderprocessing.repository;

import com.ecommerce.orderprocessing.model.Order;
import com.ecommerce.orderprocessing.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
}