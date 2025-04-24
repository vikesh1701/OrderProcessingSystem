package com.ecommerce.orderprocessing.service;

import com.ecommerce.orderprocessing.model.Order;
import com.ecommerce.orderprocessing.model.OrderItem;
import com.ecommerce.orderprocessing.model.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setCustomerId("CUST123");
        OrderItem item = new OrderItem();
        item.setProductName("Laptop");
        item.setQuantity(1);
        item.setPrice(1000.0);
        order.getItems().add(item);
        order = orderService.createOrder(order);
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        Order newOrder = new Order();
        newOrder.setCustomerId("CUST456");
        OrderItem item = new OrderItem();
        item.setProductName("Phone");
        item.setQuantity(2);
        item.setPrice(500.0);
        newOrder.getItems().add(item);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("CUST456"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items[0].productName").value("Phone"));
    }

    @Test
    void getOrder_ExistingOrder_ShouldReturnOrder() throws Exception {
        mockMvc.perform(get("/api/orders/{orderId}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()))
                .andExpect(jsonPath("$.customerId").value("CUST123"));
    }

    @Test
    void getOrder_NonExistingOrder_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllOrders_NoFilter_ShouldReturnAllOrders() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerId").value("CUST123"));
    }

    @Test
    void getAllOrders_WithStatusFilter_ShouldReturnFilteredOrders() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void updateOrderStatus_ValidOrder_ShouldUpdateStatus() throws Exception {
        mockMvc.perform(put("/api/orders/{orderId}/status", order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderStatus.SHIPPED)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void updateOrderStatus_NonExistingOrder_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(put("/api/orders/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(OrderStatus.SHIPPED)))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelOrder_PendingOrder_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/orders/{orderId}", order.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelOrder_NonPendingOrder_ShouldReturnBadRequest() throws Exception {
        orderService.updateOrderStatus(order.getId(), OrderStatus.PROCESSING);

        mockMvc.perform(delete("/api/orders/{orderId}", order.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelOrder_NonExistingOrder_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/orders/999"))
                .andExpect(status().isNotFound());
    }
}