package com.quangduy.productservice.Business.Service;


import com.quangduy.productservice.Business.Domain.Product;
import com.quangduy.productservice.Business.Domain.Reservation;
import com.quangduy.productservice.Persistence.CategoryRepository;
import com.quangduy.productservice.Persistence.ProductRepository;
import com.quangduy.productservice.Persistence.ReservationRepository;
import com.quangduy.productservice.Presentation.dto.QuantityFilter;
import com.quangduy.productservice.Presentation.dto.ReleaseProductRequest;
import com.quangduy.productservice.Presentation.dto.ReservationResponse;
import com.quangduy.productservice.Utils.Exception.ProductException;
import com.quangduy.productservice.Utils.sorting.ProductSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ReservationRepository reservationRepository;


    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository, ReservationRepository reservationRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.reservationRepository = reservationRepository;
    }

    // CREATE a new product
    @Transactional
    public Product createProduct(Product product) {
        // Check if the SKU already exists
        if (productRepository.existsBySku(product.getSku())) {
            throw new ProductException.DuplicateSkuException("SKU already exists: " + product.getSku());
        }
        if (product.getVendorId() == null){
            throw new IllegalArgumentException("Vendor id is null");
        }

        // Check if the Category exists
        if (!categoryRepository.existsById(product.getCategory().getCategoryId())) {
            throw new ProductException.CategoryNotFoundException("Category not found with ID: " + product.getCategory().getCategoryId());
        }
        product.setCreatedAt(OffsetDateTime.now());

        // Save the new product
        return productRepository.save(product);
    }

    // READ a product by its SKU
    public Product getProductBySku(String sku) {
        // Check if the product exists
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ProductException.ProductNotFoundException("Product with SKU: " + sku + " not found"));
    }
    @Transactional
    public Product getProductById(Long id) {
        // Check if the product exists
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductException.ProductNotFoundException("Product with Id: " + id + " not found"));
    }

    // READ all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // UPDATE a product by its Id
    @Transactional
    public Product updateProduct(Product updatedProductDetails,Long vendorId) {
        // Check if the product exists
        Product existingProduct = productRepository.findById(updatedProductDetails.getId())
                .orElseThrow(() -> new ProductException.ProductNotFoundException("Product with Id: " + updatedProductDetails.getId() + " not found"));

        // Ensure that SKU is not updated
        if (!existingProduct.getSku().equals(updatedProductDetails.getSku())) {
            throw new ProductException.InvalidUpdateException("SKU cannot be updated");
        }

        if (!existingProduct.getVendorId().equals(vendorId)) {
            throw new ProductException.UnauthorizedException("The user do not own this product");
        }

        // Check if the Category exists
        if (!categoryRepository.existsById(updatedProductDetails.getCategory().getCategoryId())) {
            throw new ProductException.CategoryNotFoundException("Category not found with ID: " + updatedProductDetails.getCategory().getCategoryId());
        }

        // Validation is handled by Hibernate Validator via annotations

        // Update product fields
        existingProduct.setProductName(updatedProductDetails.getProductName());
        existingProduct.setPriceUnit(updatedProductDetails.getPriceUnit());
        existingProduct.setQuantity(updatedProductDetails.getQuantity());
        existingProduct.setImageUrl(updatedProductDetails.getImageUrl());
        existingProduct.setCategory(updatedProductDetails.getCategory());
        existingProduct.setDescription(updatedProductDetails.getDescription());
        // Note: SKU is not updated

        // Save the updated product
        return productRepository.save(existingProduct);
    }

    // DELETE a product by its SKU
    @Transactional
    public void deleteProduct(Long id,Long vendorId) {
        // Check if the product exists
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException.ProductNotFoundException("Product with Id: " + id + " not found"));
        if (!product.getVendorId().equals(vendorId)) {
            throw new ProductException.UnauthorizedException("The user do not own this product");
        }
        // Delete the product
        productRepository.delete(product);
    }

    @Transactional
    public Page<Product> getPagingProducts(int page, int size,String[] sort) {
        Sort.Order order = getOrder(sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        return productRepository.findAll(pageable);
    }

    private Sort.Order getOrder(String[] sort) {
        String columnName = sort[0];
        if (!getAllowedSortColumns(Product.class).contains(columnName)){
            throw new IllegalArgumentException("Invalid sort column: "+ sort[0]);
        }
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sort[1]);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid sort direction: " + sort[1]);
        }

        Sort.Order order = new Sort.Order(direction, columnName);
        return order;
    }

    @Transactional
    public Page<Product> getPagingProductsByCategoryName(int page, int size,String[] sort, String categoryName) {
        categoryRepository.findByCategoryName(categoryName).orElseThrow(() -> new ProductException.CategoryNotFoundException("Category not found with name: " + categoryName));
        Sort.Order order = getOrder(sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));
        Page<Product> productPage = productRepository.findByCategoryCategoryName(categoryName, pageable);
        if (productPage.isEmpty() && page>0) {
            throw new IllegalArgumentException("Number of page exceed: "+page+"/"+productPage.getTotalPages());
        }
        return productPage;
    }
    private List<String> getAllowedSortColumns(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());
    }
    @Transactional
    public Page<Product> getProductsByVendorId( int page, int size, String[] sort,Long vendorId) {
        Sort.Order order = getOrder(sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));
        Page<Product> productPage = productRepository.findByVendorId(vendorId,pageable);
        if (productPage.isEmpty() && page>0) {
            throw new IllegalArgumentException("Number of page exceed: "+page+"/"+productPage.getTotalPages());
        }
        return productPage;
    }

    @Transactional
    public ReservationResponse reserveProduct(Long userId, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        // Reduce product quantity
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        // Create reservation record
        Reservation reservation = new Reservation();
        reservation.setProductId(productId);
        reservation.setUserId(userId);
        reservation.setQuantity(quantity);

        // Optionally set orderId or timestamp

        Reservation savedReservation = reservationRepository.save(reservation);

        // Return the reservation ID for tracking
        ReservationResponse response = new ReservationResponse(savedReservation.getReservationId(),product.getVendorId());
        return response;
    }

    @Transactional
    public void releaseProduct(Long reservationId) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        Product product = productRepository.findById(reservation.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Increase product quantity
        product.setQuantity(product.getQuantity() + reservation.getQuantity());
        productRepository.save(product);

        // Delete the reservation record
        reservationRepository.delete(reservation);
    }
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String keyword, Integer categoryId, Long vendorId, String sortOption, QuantityFilter quantityFilter
            , int page, int size) {
        Pageable pageable = PageRequest.of(page, size, getSort(sortOption));

        Specification<Product> spec = Specification.where(null);

        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and(ProductSpecifications.hasKeywordInName(keyword));
        }

        if (categoryId != null) {
            spec = spec.and(ProductSpecifications.hasCategory(categoryId));
        }

        if (vendorId != null) {
            spec = spec.and(ProductSpecifications.hasVendorId(vendorId));
        }
        if (quantityFilter != null) {
            spec = spec.and(ProductSpecifications.hasQuantity(quantityFilter));
        }

        return productRepository.findAll(spec, pageable);
    }

    private Sort getSort(String sortOption) {
        if ("priceAsc".equals(sortOption)) {
            return Sort.by(Sort.Direction.ASC, "priceUnit");
        } else if ("priceDesc".equals(sortOption)) {
            return Sort.by(Sort.Direction.DESC, "priceUnit");
        } else if ("newest".equals(sortOption)) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        } else if ("oldest".equals(sortOption)) {
            return Sort.by(Sort.Direction.ASC, "createdAt");
        } else {
            // Default sort
            return Sort.unsorted();
        }
    }
}

