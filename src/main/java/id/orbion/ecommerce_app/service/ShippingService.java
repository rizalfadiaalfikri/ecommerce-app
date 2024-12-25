package id.orbion.ecommerce_app.service;

import java.math.BigDecimal;

import id.orbion.ecommerce_app.model.ShippingOrderRequest;
import id.orbion.ecommerce_app.model.ShippingOrderResponse;
import id.orbion.ecommerce_app.model.ShippingRateRequest;
import id.orbion.ecommerce_app.model.ShippingRateResponse;

public interface ShippingService {

    ShippingRateResponse calculateShippingRate(ShippingRateRequest request);

    ShippingOrderResponse createShippingOrder(ShippingOrderRequest request);

    String generateAwbNumber(Long orderId);

    BigDecimal calculateTotalWeight(Long orderId);

}
