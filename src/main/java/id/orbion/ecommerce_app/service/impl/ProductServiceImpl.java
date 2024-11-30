package id.orbion.ecommerce_app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import id.orbion.ecommerce_app.common.error.BadRequestException;
import id.orbion.ecommerce_app.common.error.ResourceNotFoundException;
import id.orbion.ecommerce_app.entity.Category;
import id.orbion.ecommerce_app.entity.Product;
import id.orbion.ecommerce_app.entity.ProductCategory;
import id.orbion.ecommerce_app.entity.ProductCategory.ProductCategoryId;
import id.orbion.ecommerce_app.model.CategoryResponse;
import id.orbion.ecommerce_app.model.ProductRequest;
import id.orbion.ecommerce_app.model.ProductResponse;
import id.orbion.ecommerce_app.repository.CategoryRepository;
import id.orbion.ecommerce_app.repository.ProductCategoryRepository;
import id.orbion.ecommerce_app.repository.ProductRepository;
import id.orbion.ecommerce_app.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(product -> {
                    List<ProductCategory> productCategories = productCategoryRepository
                            .findCategoriesByProductId(product.getProductId());
                    List<CategoryResponse> categoryResponsesList = productCategories.stream()
                            .map(productCategory -> {
                                Category category = categoryRepository.findById(productCategory.getId().getCategoryId())
                                        .orElseThrow(() -> {
                                            return new ResourceNotFoundException(
                                                    "Category not found for id "
                                                            + productCategory.getId().getCategoryId());
                                        });
                                return CategoryResponse.fromCategory(category);
                            }).toList();

                    return ProductResponse.fromProductCategories(product, categoryResponsesList);
                }).toList();
    }

    @Override
    public ProductResponse findById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Product not found for id " + productId);
                });

        List<ProductCategory> productCategories = productCategoryRepository.findCategoriesByProductId(productId);
        List<CategoryResponse> categoryResponsesList = productCategories.stream()
                .map(productCategory -> {
                    Category category = categoryRepository.findById(productCategory.getId().getCategoryId())
                            .orElseThrow(() -> {
                                return new ResourceNotFoundException(
                                        "Category not found for id " + productCategory.getId().getCategoryId());
                            });
                    return CategoryResponse.fromCategory(category);
                }).toList();

        return ProductResponse.fromProductCategories(product, categoryResponsesList);
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest productRequest) {
        try {
            List<Category> categories = getCategoriesByIds(productRequest.getCategorieIds());

            Product product = Product.builder()
                    .name(productRequest.getName())
                    .price(productRequest.getPrice())
                    .description(productRequest.getDescription())
                    .stockQuantity(productRequest.getStockQuantity())
                    .weight(productRequest.getWeight())
                    .build();

            Product savedProduct = productRepository.save(product);
            List<ProductCategory> productCategories = categories.stream()
                    .map(category -> {
                        ProductCategory productCategory = ProductCategory.builder().build();

                        ProductCategoryId productCategoryId = new ProductCategoryId();
                        productCategoryId.setCategoryId(category.getCategoryId());
                        productCategoryId.setProductId(savedProduct.getProductId());
                        productCategory.setId(productCategoryId);

                        return productCategory;
                    }).toList();

            productCategoryRepository.saveAll(productCategories);

            List<CategoryResponse> categoryResponsesList = categories.stream()
                    .map(category -> {
                        return CategoryResponse.fromCategory(category);
                    }).toList();

            return ProductResponse.fromProductCategories(savedProduct, categoryResponsesList);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest productRequest) {
        try {
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> {
                        return new ResourceNotFoundException("Product not found for id " + id);
                    });

            List<Category> categories = getCategoriesByIds(productRequest.getCategorieIds());
            existingProduct.setName(productRequest.getName());
            existingProduct.setPrice(productRequest.getPrice());
            existingProduct.setDescription(productRequest.getDescription());
            existingProduct.setStockQuantity(productRequest.getStockQuantity());
            existingProduct.setWeight(productRequest.getWeight());
            productRepository.save(existingProduct);

            List<ProductCategory> productCategories = productCategoryRepository.findCategoriesByProductId(id);
            productCategoryRepository.deleteAll(productCategories);

            List<ProductCategory> newProductCategories = categories.stream()
                    .map(category -> {
                        ProductCategory productCategory = ProductCategory.builder().build();

                        ProductCategoryId productCategoryId = new ProductCategoryId();
                        productCategoryId.setCategoryId(category.getCategoryId());
                        productCategoryId.setProductId(id);
                        productCategory.setId(productCategoryId);

                        return productCategory;
                    }).toList();

            productCategoryRepository.saveAll(newProductCategories);

            return ProductResponse.fromProductCategories(existingProduct, categories.stream()
                    .map(category -> {
                        return CategoryResponse.fromCategory(category);
                    }).toList());
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Product not found for id " + id);
                });

        List<ProductCategory> productCategories = productCategoryRepository.findCategoriesByProductId(id);
        productCategoryRepository.deleteAll(productCategories);

        productRepository.delete(existingProduct);
    }

    private List<Category> getCategoriesByIds(List<Long> categoryIds) {
        return categoryIds.stream()
                .map(categoryId -> {
                    return categoryRepository.findById(categoryId)
                            .orElseThrow(() -> {
                                return new ResourceNotFoundException("Category not found for id " + categoryId);
                            });
                }).toList();
    }

}
