package id.orbion.ecommerce_app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.orbion.ecommerce_app.entity.Order;
import id.orbion.ecommerce_app.model.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(OrderStatus status);

    @Query(value = """
            SELECT * FROM orders
            WHERE user_id = :userId
            AND order_date BETWEEN :startDate AND :endDate
            """, nativeQuery = true)
    List<Order> findByOrderIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    Optional<Order> findByXenditInvoiceId(String xenditInvoiceId);

    List<Order> findByStatusAndOrderDateBefore(OrderStatus status, LocalDateTime orderDate);

    @Query(value = """
            SELECT * FROM orders
            WHERE user_id = :userId
            """, nativeQuery = true)
    Page<Order> findByUserIdByPageable(Long userId, Pageable pageable);

}
