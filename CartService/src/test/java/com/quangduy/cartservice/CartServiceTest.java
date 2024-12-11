package com.quangduy.cartservice;


import com.quangduy.cartservice.model.dto.AddItemRequest;
import com.quangduy.cartservice.model.dto.CartResponse;
import com.quangduy.cartservice.model.dto.ProductResponse;
import com.quangduy.cartservice.model.dto.UpdateItemRequest;
import com.quangduy.cartservice.model.entity.Cart;
import com.quangduy.cartservice.model.entity.CartItem;
import com.quangduy.cartservice.repository.CartItemRepository;
import com.quangduy.cartservice.repository.CartRepository;
import com.quangduy.cartservice.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CartService cartService;

    private Long userId;
    private Long productId;
    private AddItemRequest addItemRequest;
    private UpdateItemRequest updateItemRequest;
    private ProductResponse productResponse;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        userId = 1L;
        productId = 100L;

        addItemRequest = new AddItemRequest();
        addItemRequest.setProductId(productId);
        addItemRequest.setQuantity(2);

        updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setQuantity(5);

        productResponse = new ProductResponse();
        productResponse.setId(productId);
        productResponse.setProductName("Test Product");
        productResponse.setPriceUnit(10.0);
        productResponse.setImageUrl("http://example.com/image.jpg");

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProductId(productId);
        cartItem.setQuantity(2);

        cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());
        cart.getItems().add(cartItem);
    }

    @Test
    void testAddItemToCart_NewCart_NewItem() {
        // Cart does not exist
        when(cartRepository.findById(userId)).thenReturn(Optional.empty());
        when(restTemplate.getForEntity(anyString(), eq(ProductResponse.class)))
                .thenReturn(ResponseEntity.ok(productResponse));

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);

        cartService.addItemToCart(userId, addItemRequest);

        verify(cartRepository).save(cartCaptor.capture());
        Cart savedCart = cartCaptor.getValue();

        assertEquals(userId, savedCart.getUserId());
        assertEquals(1, savedCart.getItems().size());
        CartItem savedItem = savedCart.getItems().get(0);
        assertEquals(productId, savedItem.getProductId());
        assertEquals(2, savedItem.getQuantity());
    }

    @Test
    void testAddItemToCart_ExistingCart_ExistingItem() {
        // Cart exists with the item
        when(cartRepository.findById(userId)).thenReturn(Optional.of(cart));
        when(restTemplate.getForEntity(anyString(), eq(ProductResponse.class)))
                .thenReturn(ResponseEntity.ok(productResponse));

        addItemRequest.setQuantity(3); // Add 3 more

        cartService.addItemToCart(userId, addItemRequest);

        verify(cartRepository).save(cart);
        assertEquals(1, cart.getItems().size());
        CartItem updatedItem = cart.getItems().get(0);
        assertEquals(5, updatedItem.getQuantity()); // 2 + 3
    }

    @Test
    void testUpdateItemQuantity_ItemExists() {
        when(cartRepository.findById(userId)).thenReturn(Optional.of(cart));

        updateItemRequest.setQuantity(10);

        cartService.updateItemQuantity(userId, productId, updateItemRequest);

        verify(cartRepository).save(cart);
        assertEquals(1, cart.getItems().size());
        CartItem updatedItem = cart.getItems().get(0);
        assertEquals(10, updatedItem.getQuantity());
    }

    @Test
    void testUpdateItemQuantity_ItemDoesNotExist() {
        when(cartRepository.findById(userId)).thenReturn(Optional.of(cart));

        Long nonExistingProductId = 999L;

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                cartService.updateItemQuantity(userId, nonExistingProductId, updateItemRequest));

        assertEquals("Item not found in cart", exception.getMessage());
    }

    @Test
    void testRemoveItemFromCart_ItemExists() {
        when(cartRepository.findById(userId)).thenReturn(Optional.of(cart));

        cartService.removeItemFromCart(userId, productId);

        verify(cartRepository).save(cart);
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testRemoveItemFromCart_ItemDoesNotExist() {
        when(cartRepository.findById(userId)).thenReturn(Optional.of(cart));

        Long nonExistingProductId = 999L;

        cartService.removeItemFromCart(userId, nonExistingProductId);

        verify(cartRepository).save(cart);
        assertEquals(1, cart.getItems().size()); // No change
    }

    @Test
    void testGetCart_CartExists() {
        when(cartRepository.findById(userId)).thenReturn(Optional.of(cart));
        when(restTemplate.getForEntity(anyString(), eq(ProductResponse.class)))
                .thenReturn(ResponseEntity.ok(productResponse));

        CartResponse cartResponse = cartService.getCart(userId);

        assertNotNull(cartResponse);
        assertEquals(1, cartResponse.getItems().size());
        CartResponse.CartItemResponse itemResponse = cartResponse.getItems().get(0);
        assertEquals(productId, itemResponse.getProductId());
        assertEquals("Test Product", itemResponse.getProductName());
        assertEquals(2, itemResponse.getQuantity());
        assertEquals(10.0, itemResponse.getPriceUnit());
        assertEquals(20.0, itemResponse.getSubtotal());
    }

    @Test
    void testGetCart_CartDoesNotExist() {
        when(cartRepository.findById(userId)).thenReturn(Optional.empty());

        CartResponse cartResponse = cartService.getCart(userId);

        assertNotNull(cartResponse);
        assertTrue(cartResponse.getItems().isEmpty());
    }

    @Test
    void testClearCart_CartExists() {
        doNothing().when(cartRepository).deleteById(userId);

        // Call the method to be tested
        cartService.clearCart(userId);

        // Verify the interaction with the repository
        verify(cartRepository).deleteById(userId);
    }

    @Test
    void testFetchProductDTO_Success() {
        when(restTemplate.getForEntity(anyString(), eq(ProductResponse.class)))
                .thenReturn(ResponseEntity.ok(productResponse));

        ProductResponse result = cartService.fetchProductDTO(productId);

        assertNotNull(result);
        assertEquals(productId, result.getId());
    }

    @Test
    void testFetchProductDTO_Failure() {
        when(restTemplate.getForEntity(anyString(), eq(ProductResponse.class)))
                .thenReturn(ResponseEntity.status(404).body(null));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                cartService.fetchProductDTO(productId));

        assertEquals("Product not found or service unavailable", exception.getMessage());
    }
}
