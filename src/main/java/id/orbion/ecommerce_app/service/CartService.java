package id.orbion.ecommerce_app.service;

import java.util.List;

import id.orbion.ecommerce_app.model.CartItemResponse;

public interface CartService {

    void addItemToCart(Long userId, Long productId, int quantity);

    void updateCartItemQuantity(Long userId, Long productId, int quantity);

    void removeItemFromCart(Long userId, Long cartItemId);

    void clearCart(Long userId);

    List<CartItemResponse> getCartItems(Long userId);
}
