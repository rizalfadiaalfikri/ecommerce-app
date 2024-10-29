package id.orbion.ecommerce_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import id.orbion.ecommerce_app.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
