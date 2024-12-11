package com.quangduy.productservice.Utils.sorting;

import com.quangduy.productservice.Business.Domain.Product;
import com.quangduy.productservice.Presentation.dto.QuantityFilter;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;



public class ProductSpecifications {

    public static Specification<Product> hasKeywordInName(String keyword) {
        return (root, query, builder) -> builder.like(
                builder.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Product> hasCategory(Integer categoryId) {
        return (root, query, builder) -> builder.equal(root.get("category").get("categoryId"), categoryId);
    }
    public static Specification<Product> hasVendorId(Long vendorId) {
        return (root, query, builder) ->
                builder.equal(root.get("vendorId"), vendorId);
    }

    public static Specification<Product> hasQuantity(QuantityFilter quantityFilter) {
        return (root, query, builder) -> {
            switch (quantityFilter) {
                case OUT_OF_STOCK:
                    return builder.lessThanOrEqualTo(root.get("quantity"), 0);
                case LOW_STOCK:
                    return builder.and(
                            builder.greaterThan(root.get("quantity"), 0),
                            builder.lessThan(root.get("quantity"), 10)
                    );
                case IN_STOCK: // or SUFFICIENT_STOCK
                    return builder.greaterThanOrEqualTo(root.get("quantity"), 10);
                default:
                    return null; // No additional filtering
            }
        };
    }
}
