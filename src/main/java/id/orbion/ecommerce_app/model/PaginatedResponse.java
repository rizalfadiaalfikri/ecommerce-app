package id.orbion.ecommerce_app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponse<T> {
    private int status;
    private String message;
    private T data;
    private PaginationDetails pagination;
}
