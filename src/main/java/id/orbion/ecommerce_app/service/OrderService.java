package id.orbion.ecommerce_app.service;

import java.util.List;
import java.util.Optional;

import id.orbion.ecommerce_app.entity.Order;
import id.orbion.ecommerce_app.model.CheckoutRequest;
import id.orbion.ecommerce_app.model.OrderItemResponse;

public interface OrderService {
    Order checkout(CheckoutRequest checkoutRequest);

    Optional<Order> findOrderById(Long orderId);

    List<Order> findOrderByUserId(Long userId);

    List<Order> findOrderByStatus(String status);

    void cancelOrder(Long orderId);

    List<OrderItemResponse> findOrderItemsByOrderId(Long orderId);

    void updateOrderStatus(Long orderId, String newStatus);

    Double calculateOrderTotal(Long orderId);
}
