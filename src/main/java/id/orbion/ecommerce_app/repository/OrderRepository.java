package id.orbion.ecommerce_app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.orbion.ecommerce_app.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(String status);

    @Query(value = """
            SELECT * FROM orders
            WHERE user_id = :userId
            AND order_date BETWEEN :startDate AND :endDate
            """, nativeQuery = true)
    List<Order> findByOrderIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
