package id.orbion.ecommerce_app.common;

import java.util.EnumMap;
import java.util.Set;

import id.orbion.ecommerce_app.model.OrderStatus;

public class OrderStateTransition {
    // PENDING -> [PAID, CANCELLED, PAYMENT_FAILED]
    // PAID -> [SHIPPED]
    // CANCELLED -> []
    // PAYMENT_FAILED -> []
    // SHIPPED -> []

    private static final EnumMap<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = new EnumMap<>(OrderStatus.class);

    static {
        VALID_TRANSITIONS.put(OrderStatus.PENDING,
                Set.of(OrderStatus.PAID, OrderStatus.CANCELLED, OrderStatus.PAYMENT_FAILED));
        VALID_TRANSITIONS.put(OrderStatus.PAID, Set.of(OrderStatus.SHIPPED));
        VALID_TRANSITIONS.put(OrderStatus.CANCELLED, Set.of());
        VALID_TRANSITIONS.put(OrderStatus.PAYMENT_FAILED, Set.of());
        VALID_TRANSITIONS.put(OrderStatus.SHIPPED, Set.of());
    }

    // PENDING & PAID ==> return true
    public static boolean isValidTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        Set<OrderStatus> validNextStatus = VALID_TRANSITIONS.get(currentStatus);

        if (validNextStatus == null) {
            return false;
        }

        return validNextStatus.contains(newStatus);

    }

}
