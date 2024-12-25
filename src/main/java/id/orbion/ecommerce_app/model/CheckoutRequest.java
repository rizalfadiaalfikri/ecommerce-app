package id.orbion.ecommerce_app.model;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(SnakeCaseStrategy.class)
public class CheckoutRequest {

    private Long userId;

    @NotEmpty(message = "Please select at least one item from cart")
    @Size(min = 1, message = "Please select at least one item from cart")
    private List<Long> selectedCartItemIds;

    @NotNull(message = "Please select a shipping address")
    private Long userAddressId;

}
