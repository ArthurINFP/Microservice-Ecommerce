package com.quangduy.orderservice.service;


import com.quangduy.orderservice.model.dto.*;
import com.quangduy.orderservice.model.entity.Order;
import com.quangduy.orderservice.model.entity.OrderItem;
import com.quangduy.orderservice.model.entity.OrderStatus;
import com.quangduy.orderservice.repository.OrderItemRepository;
import com.quangduy.orderservice.repository.OrderRepository;
import com.quangduy.orderservice.specification.OrderSpecifications;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestTemplate restTemplate;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public Order createOrder(Long userId,String role,OrderRequest orderRequest) {
        // Fetch cart details
        CartResponse cartResponse = getCartFromCartService(userId,role);

        // Validate and reserve inventory
        for (CartResponse.CartItemResponse item : cartResponse.getItems()) {
            // Call Product Service to reserve inventory
            ReserveProductRequest reserveRequest = new ReserveProductRequest(item.getProductId(),userId ,item.getQuantity());
            String productServiceUrl = "http://productservice/product/reserve";

            // Create headers if needed
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            headers.add("Accept", "application/json");

            // Wrap the request body and headers
            HttpEntity<ReserveProductRequest> entity = new HttpEntity<>(reserveRequest, headers);

            // Send POST request
            ResponseEntity<ReservationResponse> response = restTemplate.exchange(
                    productServiceUrl,
                    HttpMethod.POST,
                    entity,
                    ReservationResponse.class
            );
            ReservationResponse reservationResponse = response.getBody();
            if (reservationResponse != null) {
                item.setReservationId(reservationResponse.getReservationId());
                item.setVendorId(reservationResponse.getVendorId());
            }
            else{
                throw new RuntimeException("Cant find reservation id for item with id: "+item.getProductId());
            }

        }
        UserProfileResponse userProfile =  getUserProfile(userId);
        // Create order with status 'PENDING'
        Order order = new Order();
        order.setUserId(userId);
        order.setUserName(userProfile.getFullName());
        order.setPhoneNumber(userProfile.getPhone());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setAddress(orderRequest.getAddress());
        order.setTotalAmount(cartResponse.getTotalAmount());

        List<OrderItem> orderItems = cartResponse.getItems().stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProductId(item.getProductId());
                    orderItem.setProductName(item.getProductName());
                    orderItem.setImgUrl(item.getImageUrl());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPriceUnit(item.getPriceUnit());
                    orderItem.setReservationId(item.getReservationId());
                    orderItem.setVendorId(item.getVendorId());
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);

        // Save the order to generate the order ID
        Order savedOrder = orderRepository.save(order);

        // Process payment
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(savedOrder.getId());
        paymentRequest.setUserId(userId);
        paymentRequest.setAmount(savedOrder.getTotalAmount());
        paymentRequest.setPaymentMethod(orderRequest.getPaymentMethod()); // or get from user input

        String paymentUrl = "http://paymentservice/process";
        ResponseEntity<PaymentResponse> paymentResponse = restTemplate.postForEntity(paymentUrl, paymentRequest, PaymentResponse.class);

//        if (paymentResponse.getBody() == null || paymentResponse.getBody().getStatus() != PaymentStatus.SUCCESS) {
//            // Handle payment failure
//            // Update order status to 'FAILED'
//            savedOrder.setStatus("FAILED");
//            orderRepository.save(savedOrder);
//
//            // Release reserved inventory
//            for (CartResponse.CartItemResponse item : cartResponse.getItems()) {
//                String productUrl = "http://productservice/products/release";
//                ReleaseProductRequest releaseRequest = new ReleaseProductRequest(item.getProductId(), item.getQuantity());
//                restTemplate.postForEntity(productUrl, releaseRequest, Void.class);
//            }
//
//            throw new IllegalStateException("Payment failed");
//        }

        // Payment succeeded, update order status to 'CONFIRMED'
        savedOrder.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(savedOrder);

        // Clear the cart
        clearCart(userId, role);

        return savedOrder;
    }

    public Page<OrderDTO> getOrdersForVendor(
            Long vendorId,
            Long orderId,
            String userName,
            String phoneNumber,
            OrderStatus status,
            String sortOption,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size, getSort(sortOption));

        Specification<Order> spec = Specification.where(OrderSpecifications.hasVendorId(vendorId));

        if (orderId != null) {
            spec = spec.and(OrderSpecifications.hasOrderId(orderId));
        }

        if (userName != null && !userName.isEmpty()) {
            spec = spec.and(OrderSpecifications.hasUserName(userName));
        }

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            spec = spec.and(OrderSpecifications.hasPhoneNumber(phoneNumber));
        }

        if (status != null) {
            spec = spec.and(OrderSpecifications.hasStatus(status));
        }

        // Fetch Orders with the specified criteria
        Page<Order> ordersPage = orderRepository.findAll(spec, pageable);

        // Build OrderDTO list
        List<OrderDTO> ordersForVendor = ordersPage.stream()
                .map(order -> {
                    OrderDTO orderDTO = new OrderDTO(order);

                    // Filter items to include only those from the vendor
                    List<OrderItemDTO> itemDTOs = order.getItems().stream()
                            .filter(item -> item.getVendorId().equals(vendorId))
                            .map(this::toOrderItemDTO)
                            .collect(Collectors.toList());
                    orderDTO.setItems(itemDTOs);

                    return orderDTO;
                })
                .collect(Collectors.toList());

        // Convert to Page
        return new PageImpl<>(ordersForVendor, pageable, ordersPage.getTotalElements());
    }

    private Sort getSort(String sortOption) {
        if ("oldest".equalsIgnoreCase(sortOption)) {
            return Sort.by(Sort.Direction.ASC, "orderDate");
        } else {
            // Default to newest
            return Sort.by(Sort.Direction.DESC, "orderDate");
        }
    }


    private OrderItemDTO toOrderItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setImgUrl(item.getImgUrl());
        dto.setQuantity(item.getQuantity());
        dto.setPriceUnit(item.getPriceUnit());
        dto.setTotalPrice(item.getPriceUnit() * item.getQuantity());
        return dto;
    }
    // Modify the clearCart method to accept the injected RestTemplate
    private void clearCart(Long userId, String role) {
        String url = "http://cartservice/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-ID", String.valueOf(userId));
        headers.set("X-User-Roles", role);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Cart cleared successfully");
        } else {
            throw new RuntimeException("Fail to clear cart");
        }
    }

    public UserProfileResponse getUserProfile(Long userId){
        String url = "http://userservice/profile/"+ userId;
        ResponseEntity<UserProfileResponse> response = restTemplate.exchange(url,HttpMethod.POST,null,UserProfileResponse.class);
        UserProfileResponse userProfileResponse = response.getBody();
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Get user profile successfully");
        }
        else {
            throw new RuntimeException("OrderService - Fail to get user profile");
        }
        return userProfileResponse;
    }



    public Page<Order> getOrdersByUserId(Long userId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending()); // Sort by the field and order (DESC)
        return orderRepository.findByUserId(userId, pageable);
    }

    public CartResponse getCartFromCartService(Long userId, String roles) {
        // Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-ID", userId.toString());
        headers.set("X-User-Roles", roles);

        // Create the HttpEntity that contains the headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Define the cart service URL
        String cartUrl = "http://cartservice/";

        // Make the request using exchange and provide the headers
        ResponseEntity<CartResponse> response = restTemplate.exchange(cartUrl, HttpMethod.GET, entity, CartResponse.class);

        CartResponse cartResponse = response.getBody();

        if (cartResponse == null || cartResponse.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        return cartResponse;
    }
}

