package id.orbion.ecommerce_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.orbion.ecommerce_app.model.AddToCartRequest;
import id.orbion.ecommerce_app.model.ApiResponse;
import id.orbion.ecommerce_app.model.UpdateCartItemRequest;
import id.orbion.ecommerce_app.model.UserInfo;
import id.orbion.ecommerce_app.service.CartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/carts")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<ApiResponse> addItemToCart(@Valid @RequestBody AddToCartRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        cartService.addItemToCart(userInfo.getUser().getUserId(),
                request.getProductId(),
                request.getQuantity());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Item added to cart")
                        .build());
    }

    @PutMapping("/items")
    public ResponseEntity<ApiResponse> updateItemQuantity(@Valid @RequestBody UpdateCartItemRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        cartService.updateCartItemQuantity(userInfo.getUser().getUserId(),
                request.getProductId(),
                request.getQuantity());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Item quantity updated")
                        .build());
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse> removeItemFromCart(@PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        cartService.removeItemFromCart(userInfo.getUser().getUserId(), id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Item removed from cart")
                        .build());
    }

    @GetMapping("/items")
    public ResponseEntity<ApiResponse> getCartItems() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Cart items found")
                        .data(cartService.getCartItems(userInfo.getUser().getUserId()))
                        .build());
    }

    @DeleteMapping("/items")
    public ResponseEntity<ApiResponse> clearCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        cartService.clearCart(userInfo.getUser().getUserId());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Cart cleared")
                        .build());
    }

}
