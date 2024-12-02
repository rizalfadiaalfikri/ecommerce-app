package id.orbion.ecommerce_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.orbion.ecommerce_app.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    @Query(value = """
            SELECT oi.* FROM order_items oi
            JOIN orders o ON o.order_id = oi.order_id
            WHERE o.user_id = :userId
            AND oi.product_id = :productId
            """, nativeQuery = true)
    List<OrderItem> findByUserAndProduct(Long userId, Long productId);

    @Query(value = """
            SELECT SUM(quantity * price) FROM order_items
            WHERE order_id = :orderId
            """, nativeQuery = true)
    Double calculateTotalOrder(Long orderId);
}
