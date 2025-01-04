package id.orbion.ecommerce_app.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.xendit.exception.XenditException;
import com.xendit.model.Invoice;

import id.orbion.ecommerce_app.common.OrderStateTransition;
import id.orbion.ecommerce_app.common.error.InventoryException;
import id.orbion.ecommerce_app.common.error.ResourceNotFoundException;
import id.orbion.ecommerce_app.entity.CartItem;
import id.orbion.ecommerce_app.entity.Order;
import id.orbion.ecommerce_app.entity.OrderItem;
import id.orbion.ecommerce_app.entity.Product;
import id.orbion.ecommerce_app.entity.UserAddress;
import id.orbion.ecommerce_app.model.CheckoutRequest;
import id.orbion.ecommerce_app.model.OrderItemResponse;
import id.orbion.ecommerce_app.model.OrderResponse;
import id.orbion.ecommerce_app.model.OrderStatus;
import id.orbion.ecommerce_app.model.PaginatedOrderResponse;
import id.orbion.ecommerce_app.model.PaymentResponse;
import id.orbion.ecommerce_app.model.ShippingRateRequest;
import id.orbion.ecommerce_app.model.ShippingRateResponse;
import id.orbion.ecommerce_app.repository.CartItemRepository;
import id.orbion.ecommerce_app.repository.OrderItemRepository;
import id.orbion.ecommerce_app.repository.OrderRepository;
import id.orbion.ecommerce_app.repository.ProductRepository;
import id.orbion.ecommerce_app.repository.UserAddressRepository;
import id.orbion.ecommerce_app.service.InventoryService;
import id.orbion.ecommerce_app.service.OrderService;
import id.orbion.ecommerce_app.service.PaymentService;
import id.orbion.ecommerce_app.service.ShippingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

        private final BigDecimal TAX_RATE = BigDecimal.valueOf(0.03);

        private final CartItemRepository cartItemRepository;
        private final OrderRepository orderRepository;
        private final OrderItemRepository orderItemRepository;
        private final UserAddressRepository userAddressRepository;
        private final ProductRepository productRepository;
        private final ShippingService shippingService;
        private final PaymentService paymentService;
        private final InventoryService inventoryService;

        @Override
        @Transactional
        public OrderResponse checkout(CheckoutRequest checkoutRequest) {
                List<CartItem> selectedItems = cartItemRepository.findAllById(checkoutRequest.getSelectedCartItemIds());

                if (selectedItems.isEmpty()) {
                        throw new ResourceNotFoundException("No cart items found for checkout");
                }

                UserAddress shippingAddress = userAddressRepository.findById(checkoutRequest.getUserAddressId())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "No shipping address found for checkout"));

                Map<Long, Integer> productQuantities = selectedItems.stream()
                                .collect(Collectors.toMap(CartItem::getProductId, CartItem::getQuantity));

                if (!inventoryService.checkAndLockInventory(productQuantities)) {
                        throw new InventoryException("Not enough inventory");
                }

                // request is validate
                Order newOrder = Order.builder()
                                .userId(checkoutRequest.getUserId())
                                .status(OrderStatus.PENDING)
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

                BigDecimal subTotal = orderItems.stream()
                                .map(orderItem -> orderItem.getPrice()
                                                .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal shippingFee = orderItems.stream()
                                .map(orderItem -> {
                                        Optional<Product> product = productRepository
                                                        .findById(orderItem.getProductId());
                                        if (product.isEmpty()) {
                                                return BigDecimal.ZERO;
                                        }
                                        Optional<UserAddress> sellerAddress = userAddressRepository
                                                        .findByUserIdAndIsDefaultTrue(
                                                                        orderItem.getProductId());

                                        if (sellerAddress.isEmpty()) {
                                                return BigDecimal.ZERO;
                                        }

                                        BigDecimal totalWeight = product.get().getWeight().multiply(
                                                        BigDecimal.valueOf(orderItem.getQuantity()));

                                        // calculate shipping rate
                                        ShippingRateRequest shippingRateRequest = ShippingRateRequest.builder()
                                                        .totalWeightInGrams(totalWeight)
                                                        .fromAddress(ShippingRateRequest
                                                                        .fromAddress(sellerAddress.get()))
                                                        .toAddress(ShippingRateRequest.fromAddress(shippingAddress))
                                                        .build();
                                        ShippingRateResponse rateResponse = shippingService
                                                        .calculateShippingRate(shippingRateRequest);

                                        return rateResponse.getShippingFee();
                                })
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal taxFee = subTotal.multiply(TAX_RATE);
                BigDecimal totalAmount = subTotal.add(shippingFee).add(taxFee);

                savedOrder.setSubtotal(subTotal);
                savedOrder.setTotalAmount(totalAmount);
                savedOrder.setShippingFee(shippingFee);
                savedOrder.setTaxFee(taxFee);

                orderRepository.save(savedOrder);

                String paymenturl;

                try {
                        PaymentResponse paymentResponse = paymentService.create(savedOrder);
                        savedOrder.setXenditInvoiceId(paymentResponse.getXenditInvoiceId());
                        savedOrder.setXenditPaymentStatus(paymentResponse.getXenditInvoiceStatus());
                        paymenturl = paymentResponse.getXenditPaymentUrl();

                        orderRepository.save(savedOrder);
                        inventoryService.decreaseQuantity(productQuantities);

                        // interact with xendit api
                        // generate payment url
                } catch (Exception e) {
                        log.error("Payment creation for order {} failed", savedOrder.getOrderId(), e);
                        savedOrder.setStatus(OrderStatus.PAYMENT_FAILED);
                        orderRepository.save(savedOrder);

                        return OrderResponse.fromOrder(savedOrder);
                }

                OrderResponse orderResponse = OrderResponse.fromOrder(savedOrder);
                orderResponse.setPaymentUrl(paymenturl);
                return orderResponse;
        }

        @Override
        public Optional<Order> findOrderById(Long orderId) {
                return orderRepository.findById(orderId);
        }

        @Override
        public List<Order> findOrderByUserId(Long userId) {
                return orderRepository.findByUserId(userId);
        }

        @Override
        public List<Order> findOrderByStatus(OrderStatus status) {
                return orderRepository.findByStatus(status);
        }

        @Override
        @Transactional
        public void cancelOrder(Long orderId) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException("No order found for cancel"));
                if (!OrderStateTransition.isValidTransition(order.getStatus(), OrderStatus.CANCELLED)) {
                        throw new IllegalStateException("Only pending orders can be cancelled");
                }

                List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
                Map<Long, Integer> productQuantities = orderItems.stream()
                                .collect(Collectors.toMap(OrderItem::getProductId, OrderItem::getQuantity));

                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);

                if (order.getStatus().equals(OrderStatus.CANCELLED)) {
                        cancelXenditInvoide(order);
                        inventoryService.increaseQuantity(productQuantities);
                }

        }

        @Override
        public List<OrderItemResponse> findOrderItemsByOrderId(Long orderId) {
                List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
                if (orderItems.isEmpty()) {
                        return Collections.emptyList();
                }

                List<Long> productIds = orderItems.stream()
                                .map(OrderItem::getProductId)
                                .toList();

                List<Long> shippingAddressIds = orderItems.stream()
                                .map(OrderItem::getUserAddressId)
                                .toList();

                // Query list of products & shipping address from the orders
                List<Product> products = productRepository.findAllById(productIds);
                List<UserAddress> shippingAddresses = userAddressRepository.findAllById(shippingAddressIds);

                Map<Long, Product> produMap = products.stream()
                                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

                Map<Long, UserAddress> shippingAddressMap = shippingAddresses.stream()
                                .collect(Collectors.toMap(UserAddress::getUserAddressId, Function.identity()));

                return orderItems.stream()
                                .map(orderItem -> OrderItemResponse.fromOrderItemProductAndAddress(orderItem,
                                                produMap.get(orderItem.getProductId()),
                                                shippingAddressMap.get(orderItem.getUserAddressId())))
                                .toList();
        }

        @Override
        @Transactional
        public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
                Order order = orderRepository.findById(orderId)
                                .orElseThrow(
                                                () -> new ResourceNotFoundException("No order found for cancel"));
                if (!OrderStateTransition.isValidTransition(order.getStatus(), newStatus)) {
                        throw new IllegalStateException("order with current status " + order.getStatus()
                                        + " cannot be updated to " + newStatus);
                }

                if (newStatus == OrderStatus.CANCELLED) {
                        cancelXenditInvoide(order);
                        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
                        Map<Long, Integer> productQuantities = orderItems.stream()
                                        .collect(Collectors.toMap(OrderItem::getProductId, OrderItem::getQuantity));
                        inventoryService.increaseQuantity(productQuantities);
                }

                order.setStatus(newStatus);
                orderRepository.save(order);
        }

        @Override
        public Double calculateOrderTotal(Long orderId) {
                return orderItemRepository.calculateTotalOrder(orderId);
        }

        private void cancelXenditInvoide(Order order) {
                try {
                        Invoice invoice = Invoice.expire(order.getXenditInvoiceId());
                        order.setXenditPaymentStatus(invoice.getStatus());
                        orderRepository.save(order);
                } catch (XenditException e) {
                        log.error("Failed to cancel Xendit invoice", e);
                        e.printStackTrace();
                }
        }

        // run each minutes
        @Scheduled(cron = "0 * * * * *")
        @Transactional
        public void cancelUnpaidOrder() {
                LocalDateTime cancelTreshold = LocalDateTime.now().minusDays(1);
                List<Order> unpaidOrders = orderRepository.findByStatusAndOrderDateBefore(OrderStatus.PENDING,
                                cancelTreshold);

                for (Order order : unpaidOrders) {
                        order.setStatus(OrderStatus.CANCELLED);
                        orderRepository.save(order);

                        cancelXenditInvoide(order);
                }
        }

        @Override
        public Page<OrderResponse> findOrderByUserIdAndPageable(Long userId, Pageable pageable) {
                return orderRepository.findByUserIdByPageable(userId, pageable)
                                .map(order -> {
                                        return OrderResponse.fromOrder(order);
                                });
        }

        @Override
        public PaginatedOrderResponse convertOrderPage(Page<OrderResponse> orderResponse) {
                return PaginatedOrderResponse.builder().data(orderResponse.getContent())
                                .pageNo(orderResponse.getNumber()).pageSize(orderResponse.getSize())
                                .totalElement(orderResponse.getTotalElements())
                                .totalPages(orderResponse.getTotalPages()).last(orderResponse.isLast()).build();
        }

}
