package com.quangduy.orderservice.controller;


import com.quangduy.orderservice.model.dto.OrderDTO;
import com.quangduy.orderservice.model.dto.OrderRequest;
import com.quangduy.orderservice.model.dto.PaymentMethod;
import com.quangduy.orderservice.model.entity.Order;
import com.quangduy.orderservice.model.entity.OrderItem;
import com.quangduy.orderservice.model.entity.OrderStatus;
import com.quangduy.orderservice.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Roles") String roles,
            @RequestBody OrderRequest orderRequest) {

        authorizeUserRole(roles);

        Order order = orderService.createOrder(userId,roles,orderRequest);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<Page<Order>> getOrdersByUserId(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Roles") String roles,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy
    ) {
        authorizeUserRole(roles);
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId,page,size,sortBy));
    }
    @GetMapping("/vendor")
    public ResponseEntity<Page<OrderDTO>> getVendorOrders(
            @RequestHeader("X-User-ID") Long userId,
            @RequestHeader("X-User-Roles") String roles,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        authorizeVendorRole(roles);
        Page<OrderDTO> orders = orderService.getOrdersForVendor(
                userId, orderId, userName, phoneNumber, status, sort, page, size);

        return ResponseEntity.ok(orders);
    }

    private void authorizeUserRole(String roles) {
        if (!roles.contains("ROLE_USER")) {
            throw new UnauthorizedException("Access denied");
        }
    }
    private void authorizeVendorRole(String roles) {
        if (!roles.contains("ROLE_VENDOR")) {
            throw new UnauthorizedException("Access denied");
        }
    }


    // Exception class
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}
