package id.orbion.ecommerce_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.orbion.ecommerce_app.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
