package id.orbion.ecommerce_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import id.orbion.ecommerce_app.entity.ProductCategory;
import id.orbion.ecommerce_app.entity.ProductCategory.ProductCategoryId;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, ProductCategoryId> {

    @Query(value = """
            SELECT * FROM product_category
            WHERE product_id = :productId
            """, nativeQuery = true)
    List<ProductCategory> findCategoriesByProductId(@Param("productId") Long productId);

}
