package id.orbion.ecommerce_app.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import id.orbion.ecommerce_app.common.error.ResourceNotFoundException;
import id.orbion.ecommerce_app.entity.CartItem;
import id.orbion.ecommerce_app.entity.Order;
import id.orbion.ecommerce_app.entity.OrderItem;
import id.orbion.ecommerce_app.entity.UserAddress;
import id.orbion.ecommerce_app.model.CheckoutRequest;
import id.orbion.ecommerce_app.model.OrderItemResponse;
import id.orbion.ecommerce_app.repository.CartItemRepository;
import id.orbion.ecommerce_app.repository.OrderItemRepository;
import id.orbion.ecommerce_app.repository.OrderRepository;
import id.orbion.ecommerce_app.repository.UserAddressRepository;
import id.orbion.ecommerce_app.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserAddressRepository userAddressRepository;

    @Override
    @Transactional
    public Order checkout(CheckoutRequest checkoutRequest) {
        List<CartItem> selectedItems = cartItemRepository.findAllById(checkoutRequest.getSelectedCartItemIds());

        if (selectedItems.isEmpty()) {
            throw new ResourceNotFoundException("No cart items found for checkout");
        }

        UserAddress shippingAddress = userAddressRepository.findById(checkoutRequest.getUserAddressId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("No shipping address found for checkout"));

        // request is validate
        Order newOrder = Order.builder()
                .userId(checkoutRequest.getUserId())
                .status("PENDING")
                .orderDate(LocalDateTime.now())
                .totalAmount(BigDecimal.ZERO)
                .taxFee(BigDecimal.ZERO)
                .subtotal(BigDecimal.ZERO)
                .shippingFee(BigDecimal.ZERO)
                .build();

        Order savedOrder = orderRepository.save(newOrder);

        List<OrderItem> orderItems = selectedItems.stream()
                .map(cartItem -> OrderItem.builder()
                        .orderId(savedOrder.getOrderId())
                        .productId(cartItem.getProductId())
                        .price(cartItem.getPrice())
                        .quantity(cartItem.getQuantity())
                        .userAddressId(shippingAddress.getUserAddressId())
                        .build())
                .toList();

        orderItemRepository.saveAll(orderItems);

        cartItemRepository.deleteAll(selectedItems);

        BigDecimal totalAmount = orderItems.stream()
                .map(orderItem -> orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        savedOrder.setTotalAmount(totalAmount);

        return orderRepository.save(savedOrder);
    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findOrderById'");
    }

    @Override
    public List<Order> findOrderByUserId(Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findOrderByUserId'");
    }

    @Override
    public List<Order> findOrderByStatus(Long status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findOrderByStatus'");
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("No order found for cancel"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalStateException("Only pending orders can be cancelled");
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);

    }

    @Override
    public List<OrderItemResponse> findOrderItemsByOrderId(Long orderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findOrderItemsByOrderId'");
    }

    @Override
    public void updateOrderStatus(Long orderId, String newStatus) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateOrderStatus'");
    }

    @Override
    public Double calculateOrderTotal(Long orderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculateOrderTotal'");
    }

}
