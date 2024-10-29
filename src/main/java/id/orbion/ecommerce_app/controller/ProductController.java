package id.orbion.ecommerce_app.controller;

import java.math.BigDecimal;
import java.util.List;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.orbion.ecommerce_app.model.ProductRequest;
import id.orbion.ecommerce_app.model.ProductResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

        @GetMapping("/{productId}")
        public ResponseEntity<ProductResponse> findProductById(@PathVariable("productId") Long productId) {
                return ResponseEntity.ok(
                                ProductResponse.builder()
                                                .name("Product Name")
                                                .price(BigDecimal.ONE)
                                                .description("Product Description")
                                                .build());
        }

        @GetMapping
        public ResponseEntity<List<ProductResponse>> findAllProducts() {
                return ResponseEntity.ok(List.of(
                                ProductResponse.builder()
                                                .name("Product Name")
                                                .price(BigDecimal.ONE)
                                                .description("Product Description")
                                                .build(),
                                ProductResponse.builder()
                                                .name("Product Name")
                                                .price(BigDecimal.ONE)
                                                .description("Product Description")
                                                .build(),
                                ProductResponse.builder()
                                                .name("Product Name")
                                                .price(BigDecimal.ONE)
                                                .description("Product Description")
                                                .build()));
        }

        @PostMapping
        public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
                return ResponseEntity.created(URI.create("/products/1")).body(
                                ProductResponse.builder()
                                                .name("Product Name")
                                                .price(BigDecimal.ONE)
                                                .description("Product Description")
                                                .build());
        }

}
