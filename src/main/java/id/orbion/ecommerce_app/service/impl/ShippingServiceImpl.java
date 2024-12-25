package id.orbion.ecommerce_app.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import id.orbion.ecommerce_app.model.ShippingOrderRequest;
import id.orbion.ecommerce_app.model.ShippingOrderResponse;
import id.orbion.ecommerce_app.model.ShippingRateRequest;
import id.orbion.ecommerce_app.model.ShippingRateResponse;
import id.orbion.ecommerce_app.repository.OrderItemRepository;
import id.orbion.ecommerce_app.repository.OrderRepository;
import id.orbion.ecommerce_app.repository.ProductRepository;
import id.orbion.ecommerce_app.service.ShippingService;
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
    public ShippingOrderResponse createShippingOrder(ShippingOrderRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createShippingOrder'");
    }

    @Override
    public String generateAwbNumber(Long orderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateAwbNumber'");
    }

    @Override
    public BigDecimal calculateTotalWeight(Long orderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculateTotalWeight'");
    }

}
