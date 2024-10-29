package id.orbion.ecommerce_app.model;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Name Product cannot be blank")
    @Size(min = 3, max = 100, message = "Name Product must be between 3 and 100 characters")
    private String name;

    @NotNull(message = "Price Product cannot be null")
    @Positive(message = "Price Product must be positive value")
    @Digits(integer = 10, fraction = 2, message = "Price Product must be a valid decimal value")
    private Long price;

    @NotNull(message = "Description Product cannot be null")
    @Size(min = 3, max = 1000, message = "Description Product must be between 3 and 1000 characters")
    private String description;

}
