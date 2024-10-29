package id.orbion.ecommerce_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.orbion.ecommerce_app.entity.ProductCategory;
import id.orbion.ecommerce_app.entity.ProductCategory.ProductCategoryId;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, ProductCategoryId> {

}
