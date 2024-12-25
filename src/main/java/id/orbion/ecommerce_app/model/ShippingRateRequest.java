package id.orbion.ecommerce_app.model;

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
public class ShippingRateRequest {

    private Address fromAddress;

    private Address toAddress;

    private int totalWeightInGrams;

    @Data
    @Builder
    public static class Address {
        private String streetAddress;

        private String city;

        private String state;

        private String postalCode;

    }

}
