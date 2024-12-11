package com.quangduy.orderservice.specification;

import com.quangduy.orderservice.model.entity.Order;
import com.quangduy.orderservice.model.entity.OrderItem;
import com.quangduy.orderservice.model.entity.OrderStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecifications {

    public static Specification<Order> hasVendorId(Long vendorId) {
        return (root, query, criteriaBuilder) -> {
            // Ensure distinct results
            query.distinct(true);

            // Join with OrderItem
            Join<Order, OrderItem> itemsJoin = root.join("items", JoinType.INNER);

            return criteriaBuilder.equal(itemsJoin.get("vendorId"), vendorId);
        };
    }

    public static Specification<Order> hasOrderId(Long orderId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), orderId);
    }

    public static Specification<Order> hasUserName(String userName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("userName")), "%" + userName.toLowerCase() + "%");
    }

    public static Specification<Order> hasPhoneNumber(String phoneNumber) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("phoneNumber"), phoneNumber);
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }
}