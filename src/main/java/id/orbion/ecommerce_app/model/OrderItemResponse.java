package id.orbion.ecommerce_app.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import id.orbion.ecommerce_app.entity.OrderItem;
import id.orbion.ecommerce_app.entity.Product;
import id.orbion.ecommerce_app.entity.UserAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(SnakeCaseStrategy.class)
public class OrderItemResponse implements Serializable {

        private Long orderItemId;
        private Long productId;
        private String productName;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal totalPrice;
        private UserAddressResponse shippingAddress;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static OrderItemResponse fromOrderItemProductAndAddress(OrderItem orderItem,
                        Product product, UserAddress shippingAddress) {
                BigDecimal totalPrice = orderItem.getPrice().multiply(
                                BigDecimal.valueOf(orderItem.getQuantity()));

                return OrderItemResponse.builder()
                                .orderItemId(orderItem.getOrderItemId())
                                .productId(orderItem.getProductId())
                                .productName(product.getName())
                                .price(totalPrice)
                                .quantity(orderItem.getQuantity())
                                .totalPrice(totalPrice)
                                .shippingAddress(UserAddressResponse.fromUserAddress(shippingAddress))
                                .createdAt(orderItem.getCreatedAt())
                                .updatedAt(orderItem.getUpdatedAt())
                                .build();
        }

}
