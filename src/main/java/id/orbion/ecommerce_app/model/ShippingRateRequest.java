package id.orbion.ecommerce_app.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import id.orbion.ecommerce_app.entity.UserAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(SnakeCaseStrategy.class)
public class ShippingRateRequest {

    private Address fromAddress;

    private Address toAddress;

    private BigDecimal totalWeightInGrams;

    @Data
    @Builder
    public static class Address {
        private String streetAddress;

        private String city;

        private String state;

        private String postalCode;

    }

    public static Address fromAddress(UserAddress userAddress) {
        return Address.builder()
                .streetAddress(userAddress.getStreetAddress())
                .city(userAddress.getCity())
                .state(userAddress.getState())
                .postalCode(userAddress.getPostalCode())
                .build();
    }

}
