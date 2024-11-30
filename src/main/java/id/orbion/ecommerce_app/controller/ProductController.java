package id.orbion.ecommerce_app.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.orbion.ecommerce_app.model.ApiResponse;
import id.orbion.ecommerce_app.model.ProductRequest;
import id.orbion.ecommerce_app.model.ProductResponse;
import id.orbion.ecommerce_app.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
@SecurityRequirement(name = "Bearer")
public class ProductController {

        private final ProductService productService;

        public ProductController(ProductService productService) {
                this.productService = productService;
        }

        @GetMapping("/{productId}")
        public ResponseEntity<ApiResponse> findProductById(@PathVariable("productId") Long productId) {
                ProductResponse productResponse = productService.findById(productId);
                return ResponseEntity.ok(
                                ApiResponse.builder()
                                                .status(200)
                                                .message("Product found")
                                                .data(productResponse)
                                                .build());
        }

        @GetMapping
        public ResponseEntity<ApiResponse> findAllProducts() {
                return ResponseEntity.ok(
                                ApiResponse.builder()
                                                .status(200)
                                                .message("Products found")
                                                .data(productService.findAll())
                                                .build());
        }

        @PostMapping
        public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
                ProductResponse productResponse = productService.create(productRequest);
                return ResponseEntity.created(URI.create("")).body(
                                ApiResponse.builder()
                                                .status(201)
                                                .message("Product created successfully")
                                                .data(productResponse)
                                                .build());
        }

        @PutMapping("/{productId}")
        public ResponseEntity<ApiResponse> updateProduct(@PathVariable("productId") Long productId,
                        @Valid @RequestBody ProductRequest productRequest) {
                ProductResponse productResponse = productService.update(productId, productRequest);
                return ResponseEntity.ok(
                                ApiResponse.builder()
                                                .status(200)
                                                .message("Product updated successfully")
                                                .data(productResponse)
                                                .build());
        }

        @DeleteMapping("/{productId}")
        public ResponseEntity<ApiResponse> deleteProduct(@PathVariable("productId") Long productId) {
                productService.delete(productId);
                return ResponseEntity.ok(
                                ApiResponse.builder()
                                                .status(200)
                                                .message("Product deleted successfully")
                                                .build());
        }

}
