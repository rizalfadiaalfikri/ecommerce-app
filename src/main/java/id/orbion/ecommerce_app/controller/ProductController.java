package id.orbion.ecommerce_app.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import id.orbion.ecommerce_app.model.ApiResponse;
import id.orbion.ecommerce_app.model.PaginatedResponse;
import id.orbion.ecommerce_app.model.PaginationDetails;
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
        public ResponseEntity<PaginatedResponse<ProductResponse>> findAllProducts(
                        @RequestParam(name = "page", defaultValue = "0") Integer page,
                        @RequestParam(name = "size", defaultValue = "10") Integer size,
                        @RequestParam(name = "sort", defaultValue = "product_id, asc") String[] sort,
                        @RequestParam(required = false) String name) {
                List<Sort.Order> orders = new ArrayList<>();
                if (sort[0].contains(",")) {
                        for (String orderSort : sort) {
                                String[] _sort = orderSort.split(",");
                                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
                        }
                } else {
                        orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
                }

                Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
                Page<ProductResponse> productResponses;

                if (name != null && !name.isEmpty()) {
                        productResponses = productService.findByNamePage(name, pageable);
                } else {
                        productResponses = productService.findByPage(pageable);
                }

                ProductResponse firstProduct = productResponses.getContent().isEmpty()
                                ? null
                                : productResponses.getContent().get(0);

                PaginationDetails paginationDetails = PaginationDetails.builder()
                                .pageNumber(productResponses.getNumber())
                                .pageSize(productResponses.getSize())
                                .totalElements(productResponses.getTotalElements())
                                .totalPages(productResponses.getTotalPages())
                                .last(productResponses.isLast())
                                .first(productResponses.isFirst())
                                .empty(productResponses.isEmpty())
                                .build();

                PaginatedResponse<ProductResponse> response = PaginatedResponse.<ProductResponse>builder()
                                .status(200)
                                .message("Product found")
                                .data(firstProduct)
                                .pagination(paginationDetails)
                                .build();

                return ResponseEntity.ok(response);
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

        private Sort.Direction getSortDirection(String direction) {
                if (direction.equals("asc")) {
                        return Sort.Direction.ASC;
                } else if (direction.equals("desc")) {
                        return Sort.Direction.DESC;
                }
                return Sort.Direction.ASC;
        }

}
