package id.orbion.ecommerce_app.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import id.orbion.ecommerce_app.common.error.BadRequestException;
import id.orbion.ecommerce_app.common.error.ForbiddenAccessException;
import id.orbion.ecommerce_app.common.error.ResourceNotFoundException;
import id.orbion.ecommerce_app.entity.Cart;
import id.orbion.ecommerce_app.entity.CartItem;
import id.orbion.ecommerce_app.entity.Product;
import id.orbion.ecommerce_app.model.CartItemResponse;
import id.orbion.ecommerce_app.repository.CartItemRepository;
import id.orbion.ecommerce_app.repository.CartRepository;
import id.orbion.ecommerce_app.repository.ProductRepository;
import id.orbion.ecommerce_app.service.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void addItemToCart(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(userId)
                            .build();
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " not found"));

        if (product.getUserId().equals(userId)) {
            throw new BadRequestException("Cannot add your own product to cart");
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId);

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cartId(cart.getCartId())
                    .productId(productId)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();
            cartItemRepository.save(newItem);
        }

    }

    @Override
    @Transactional
    public void updateCartItemQuantity(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart for user " + userId + " not found"));

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId);
        if (existingItemOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cart item for product " + productId + " not found");
        }

        CartItem item = existingItemOpt.get();

        if (!item.getCartId().equals(cart.getCartId())) {
            throw new ForbiddenAccessException("Cart item does not belong to user's cart");
        }

        if (quantity <= 0) {
            cartItemRepository.deleteById(item.getCartItemId());
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart for user " + userId + " not found"));

        Optional<CartItem> existingItemOpt = cartItemRepository.findById(cartItemId);

        if (existingItemOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cart item with id " + cartItemId + " not found");
        }

        CartItem item = existingItemOpt.get();

        if (!item.getCartId().equals(cart.getCartId())) {
            throw new ForbiddenAccessException("Cart item does not belong to user's cart");
        }

        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart for user " + userId + " not found"));

        cartItemRepository.deleteAllByCartId(cart.getCartId());
    }

    @Override
    public List<CartItemResponse> getCartItems(Long userId) {
        List<CartItem> cartItems = cartItemRepository.getUserCartItems(userId);

        if (cartItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> productIds = cartItems.stream()
                .map(CartItem::getProductId)
                .toList();

        List<Product> products = productRepository.findAllById(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductId, product -> product));

        return cartItems.stream()
                .map(item -> CartItemResponse.fromCartItemAndProduct(item, productMap.get(item.getProductId())))
                .toList();
    }

}
