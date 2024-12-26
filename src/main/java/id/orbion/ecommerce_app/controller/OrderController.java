package id.orbion.ecommerce_app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import id.orbion.ecommerce_app.common.error.ForbiddenAccessException;
import id.orbion.ecommerce_app.common.error.ResourceNotFoundException;
import id.orbion.ecommerce_app.entity.Order;
import id.orbion.ecommerce_app.model.ApiResponse;
import id.orbion.ecommerce_app.model.CheckoutRequest;
import id.orbion.ecommerce_app.model.OrderItemResponse;
import id.orbion.ecommerce_app.model.OrderResponse;
import id.orbion.ecommerce_app.model.UserInfo;
import id.orbion.ecommerce_app.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse> checkout(@Valid @RequestBody CheckoutRequest checkoutRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        checkoutRequest.setUserId(userInfo.getUser().getUserId());
        Order order = orderService.checkout(checkoutRequest);
        OrderResponse orderResponse = OrderResponse.fromOrder(order);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Order placed successfully")
                        .data(orderResponse)
                        .build());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse> findOrderById(@PathVariable("orderId") Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        Order order = orderService.findOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUserId().equals(userInfo.getUser().getUserId())) {
            throw new ForbiddenAccessException("You are not authorized to access this order");
        }

        OrderResponse orderResponse = OrderResponse.fromOrder(order);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Order found")
                        .data(orderResponse)
                        .build());
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse> findOrderByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        List<Order> orders = orderService.findOrderByUserId(userInfo.getUser().getUserId());
        List<OrderResponse> orderResponses = orders.stream()
                .map(OrderResponse::fromOrder)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Orders found")
                        .data(orderResponses)
                        .build());

    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse> cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Order cancelled successfully")
                        .build());
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<ApiResponse> findOrderItems(@PathVariable("orderId") Long orderId) {
        List<OrderItemResponse> orders = orderService.findOrderItemsByOrderId(orderId);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Order items found")
                        .data(orders)
                        .build());
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable("orderId") Long orderId,
            @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Order status updated successfully")
                        .build());
    }

    @GetMapping("/{orderId}/total")
    public ResponseEntity<ApiResponse> calculateOrderYotal(@PathVariable Long orderId) {
        double total = orderService.calculateOrderTotal(orderId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Order total calculated successfully")
                        .data(total)
                        .build());
    }
}
