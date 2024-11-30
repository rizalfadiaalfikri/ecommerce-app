package id.orbion.ecommerce_app.service;

import java.util.List;

import id.orbion.ecommerce_app.model.ProductRequest;
import id.orbion.ecommerce_app.model.ProductResponse;

public interface ProductService {
    List<ProductResponse> findAll();

    ProductResponse findById(Long productId);

    ProductResponse create(ProductRequest productRequest);

    ProductResponse update(Long id, ProductRequest productRequest);

    void delete(Long id);

}
