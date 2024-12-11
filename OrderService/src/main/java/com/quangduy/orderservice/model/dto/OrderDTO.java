package com.quangduy.orderservice.model.dto;

import com.quangduy.orderservice.model.entity.Order;
import com.quangduy.orderservice.model.entity.OrderItem;
import com.quangduy.orderservice.model.entity.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private String phoneNumber;
    private String userName;
    private Double totalAmount;
    private String address;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private List<OrderItemDTO> items;
    private PaymentMethod paymentMethod;
    public OrderDTO(Order order) {
        this.orderId = order.getId();
        this.userId = order.getUserId();
        this.phoneNumber = order.getPhoneNumber();
        this.userName = order.getUserName();
        this.address = order.getAddress();
        this.totalAmount = order.getTotalAmount();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus();
        this.paymentMethod = order.getPaymentMethod();
    }

}
