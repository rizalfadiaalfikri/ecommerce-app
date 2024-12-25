package id.orbion.ecommerce_app.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import org.springframework.stereotype.Service;

import id.orbion.ecommerce_app.common.error.ResourceNotFoundException;
import id.orbion.ecommerce_app.entity.Order;
import id.orbion.ecommerce_app.model.ShippingOrderRequest;
import id.orbion.ecommerce_app.model.ShippingOrderResponse;
import id.orbion.ecommerce_app.model.ShippingRateRequest;
import id.orbion.ecommerce_app.model.ShippingRateResponse;
import id.orbion.ecommerce_app.repository.OrderItemRepository;
import id.orbion.ecommerce_app.repository.OrderRepository;
import id.orbion.ecommerce_app.repository.ProductRepository;
import id.orbion.ecommerce_app.service.ShippingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingServiceImpl implements ShippingService {

    private static final BigDecimal BASE_RATE = BigDecimal.valueOf(10000);
    private static final BigDecimal RATE_PER_KG = BigDecimal.valueOf(2500);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Override
    public ShippingRateResponse calculateShippingRate(ShippingRateRequest request) {
        // shipping_fee BASE_RATE + (weight * rate per kg)

        BigDecimal shippingFee = BASE_RATE
                .add(request.getTotalWeightInGrams().divide(BigDecimal.valueOf(1000).multiply(RATE_PER_KG)))
                .setScale(2, RoundingMode.HALF_UP);
        String estimatedDeliveyFee = "3 - 5 Hari kerja";

        return ShippingRateResponse.builder()
                .shippingFee(shippingFee)
                .estimatedDeliveryTime(estimatedDeliveyFee)
                .build();
    }

    @Override
    @Transactional
    public ShippingOrderResponse createShippingOrder(ShippingOrderRequest request) {
        String awbNumber = generateAwbNumber(request.getOrderId());

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("No order found for shipping order"));

        order.setStatus("SHIPPING");
        order.setAwbNumber(awbNumber);

        orderRepository.save(order);

        return ShippingOrderResponse.builder()
                .awbNumber(awbNumber)
                .shippingFee(BigDecimal.ZERO)
                .estimatedDeliveryTime("3 - 5 Hari kerja")
                .build();
    }

    @Override
    public String generateAwbNumber(Long orderId) {
        Random random = new Random();
        String prefix = "AWB";
        String awbNumber = String.format("%s%011d", prefix, random.nextInt(100000000));
        return awbNumber;
    }

    @Override
    public BigDecimal calculateTotalWeight(Long orderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculateTotalWeight'");
    }

}
