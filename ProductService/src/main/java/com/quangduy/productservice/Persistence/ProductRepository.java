package com.quangduy.productservice.Persistence;

import com.quangduy.productservice.Business.Domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    // Check if the SKU already exists
    boolean existsBySku(String sku);
    boolean existsByVendorId(Long vendorId);
    // Find a product by its SKU
    Optional<Product> findBySku(String sku);
    Page<Product> findByVendorId(Long vendorId, Pageable pageable);
    Page<Product> findByCategoryCategoryName(String categoryCategoryName, Pageable pageable);

}