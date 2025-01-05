package id.orbion.ecommerce_app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import id.orbion.ecommerce_app.entity.Order;
import id.orbion.ecommerce_app.model.CheckoutRequest;
import id.orbion.ecommerce_app.model.OrderItemResponse;
import id.orbion.ecommerce_app.model.OrderResponse;
import id.orbion.ecommerce_app.model.OrderStatus;
import id.orbion.ecommerce_app.model.PaginatedOrderResponse;

public interface OrderService {
    OrderResponse checkout(CheckoutRequest checkoutRequest);

    Optional<Order> findOrderById(Long orderId);

    List<Order> findOrderByUserId(Long userId);

    Page<OrderResponse> findOrderByUserIdAndPageable(Long userId, Pageable pageable);

    List<Order> findOrderByStatus(OrderStatus status);

    void cancelOrder(Long orderId);

    List<OrderItemResponse> findOrderItemsByOrderId(Long orderId);

    void updateOrderStatus(Long orderId, OrderStatus newStatus);

    Double calculateOrderTotal(Long orderId);

    PaginatedOrderResponse convertOrderPage(Page<OrderResponse> orderResponse);
}
