package id.orbion.ecommerce_app.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
    private BigDecimal price;

    @NotNull(message = "Description Product cannot be null")
    @Size(min = 3, max = 1000, message = "Description Product must be between 3 and 1000 characters")
    private String description;

    @NotNull(message = "Stok tidak boleh kosong")
    @Min(value = 0, message = "Stok tidak boleh kurang atau sama dengan 0")
    private Integer stockQuantity;

    @NotNull(message = "Berat tidak boleh kosong")
    @Min(value = 100, message = "Berat tidak boleh kurang dari 1000 gram")
    private BigDecimal weight;

    @NotEmpty(message = "Harus ada kategory yang dipilih")
    private List<Long> categorieIds;
}
