package com.quangduy.productservice.Presentation.controller;

import com.quangduy.productservice.Business.Domain.Product;
import com.quangduy.productservice.Business.Service.ProductService;
import com.quangduy.productservice.Presentation.dto.*;
import com.quangduy.productservice.Utils.Exception.ProductException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {


    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // CREATE a new product
    @PostMapping("/create")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            @RequestHeader("X-User-Roles") String roles,
            @RequestHeader("X-User-ID") String userID) {
        if (!roles.contains("ROLE_VENDOR")) {
            throw new ProductException.UnauthorizedException("The user does not have the required role to perform action to Product");
        }
        if (userID == null){
            throw new IllegalArgumentException("The user ID in the request header cannot be null");
        }
        Product createdProduct = productService.createProduct(productDTO.convertToProduct(Long.parseLong(userID)));
        return ResponseEntity.ok(ProductResponse.toDTO(createdProduct));
    }

    // READ a product by SKU
    @GetMapping("/get/sku/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        Product product = productService.getProductBySku(sku);
        return ResponseEntity.ok(ProductResponse.toDTO(product));
    }

    // Handle requests by ID (Long)
    @GetMapping("/get/id/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(ProductResponse.toDTO(product));
    }

    // READ all products // Don't use
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/get")
    public ResponseEntity<PageResponseDTO<ProductResponse>> getProductsByPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        Page<Product> products = productService.getPagingProducts(page,size,sort);
        return ResponseEntity.ok(convertToPageDTO(products));
    }

    public PageResponseDTO<ProductResponse> convertToPageDTO(Page<Product> products) {
        List<ProductResponse> content = products.getContent().stream().map(ProductResponse::toDTO).toList();
        PageResponseDTO<ProductResponse> pageResponseDTO = new PageResponseDTO<>();
        pageResponseDTO.setContent(content);
        pageResponseDTO.setTotalPages(products.getTotalPages());
        pageResponseDTO.setTotalElements(products.getTotalElements());
        pageResponseDTO.setLast(products.isLast());
        pageResponseDTO.setSize(products.getSize());
        pageResponseDTO.setNumber(products.getNumber());
        pageResponseDTO.setFirst(products.isFirst());
        pageResponseDTO.setNumberOfElements(products.getNumberOfElements());
        pageResponseDTO.setEmpty(products.isEmpty());
        return pageResponseDTO;
    }

    @GetMapping("/get-by-category")
    public ResponseEntity<PageResponseDTO<ProductResponse>> getProductsByPagingAndCategory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort,
            @RequestParam String categoryName) {
        Page<Product> products = productService.getPagingProductsByCategoryName(page,size,sort,categoryName);

        return ResponseEntity.ok(convertToPageDTO(products));
    }

    @GetMapping("/get-by-vendor")
    public ResponseEntity<PageResponseDTO<ProductResponse>> getProductsByVendor(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort,
            @RequestParam Long vendorId) {
        Page<Product> products = productService.getProductsByVendorId(page,size,sort,vendorId);

        return ResponseEntity.ok(convertToPageDTO(products));
    }

    @GetMapping("/search")
    public  ResponseEntity<PageResponseDTO<ProductResponse>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) QuantityFilter quantityFilter ,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> productPage = productService.searchProducts(keyword, category, vendorId, sort, quantityFilter,page, size);

        return ResponseEntity.ok(convertToPageDTO(productPage));
    }
    @GetMapping("/search-for-vendor")
    public  ResponseEntity<PageResponseDTO<ProductResponse>> searchProductsForVendor(
            @RequestHeader("X-User-Roles") String roles,
            @RequestHeader("X-User-ID") Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) QuantityFilter quantityFilter ,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> productPage = productService.searchProducts(keyword, category, userId, sort,quantityFilter, page, size);

        return ResponseEntity.ok(convertToPageDTO(productPage));
    }

    // UPDATE a product by Id
    @PutMapping("/update")
    public ResponseEntity<ProductResponse> updateProduct(
            @Valid @RequestBody Product updatedProductDetails,
            @RequestHeader("X-User-Roles") String roles,
            @RequestHeader("X-User-ID") Long userId) {
        if (!roles.contains("ROLE_VENDOR")) {
            throw new ProductException.UnauthorizedException("The user does not have the required role to perform action to Product");
        }
        Product updatedProduct = productService.updateProduct(updatedProductDetails,userId);
        return ResponseEntity.ok(ProductResponse.toDTO(updatedProduct));
    }

    // DELETE a product by SKU
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            @RequestHeader("X-User-Roles") String roles,
            @RequestHeader("X-User-ID") Long userId) {
        if (!roles.contains("ROLE_VENDOR")) {
            throw new ProductException.UnauthorizedException("The user doesn't have the required role to perform action to Product");
        }
        productService.deleteProduct(id,userId);
        return ResponseEntity.ok().build();  // Return 200
    }

    @PostMapping("/reserve")
    public ResponseEntity<ReservationResponse> reserveProduct(@Valid @RequestBody ReserveProductRequest request) {
        ReservationResponse reservation = productService.reserveProduct(
                request.getUserId(),
                request.getProductId(),
                request.getQuantity()
        );

        return ResponseEntity.ok(reservation);
    }

    @PostMapping("/release")
    public ResponseEntity<Void> releaseProduct(@Valid @RequestBody ReleaseProductRequest request) {
        productService.releaseProduct(request.getReservationId());
        return ResponseEntity.ok().build();
    }



    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint(){
        return ResponseEntity.ok().build();
    }
}