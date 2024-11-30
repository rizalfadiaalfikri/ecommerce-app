package id.orbion.ecommerce_app.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import id.orbion.ecommerce_app.model.ProductRequest;
import id.orbion.ecommerce_app.model.ProductResponse;

public interface ProductService {
    List<ProductResponse> findAll();

    Page<ProductResponse> findByPage(Pageable pageable);

    Page<ProductResponse> findByNamePage(String name, Pageable pageable);

    ProductResponse findById(Long productId);

    ProductResponse create(ProductRequest productRequest);

    ProductResponse update(Long id, ProductRequest productRequest);

    void delete(Long id);

}
