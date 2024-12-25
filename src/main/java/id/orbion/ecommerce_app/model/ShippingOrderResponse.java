package id.orbion.ecommerce_app.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(SnakeCaseStrategy.class)
public class ShippingOrderResponse {

    private String awbNumber;

    private BigDecimal shippingFee;

    private String estimatedDeliveryTime;

}
