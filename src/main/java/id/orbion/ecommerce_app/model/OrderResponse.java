package id.orbion.ecommerce_app.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import id.orbion.ecommerce_app.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse implements Serializable {

    private Long orderId;

    private Long userId;

    private BigDecimal subTotal;

    private BigDecimal shippingFee;

    private BigDecimal taxFee;

    private BigDecimal totalAmount;

    private String status;

    private LocalDateTime orderDate;

    public static OrderResponse fromOrder(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .subTotal(order.getSubtotal())
                .shippingFee(order.getShippingFee())
                .taxFee(order.getTaxFee())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .build();
    }

}
