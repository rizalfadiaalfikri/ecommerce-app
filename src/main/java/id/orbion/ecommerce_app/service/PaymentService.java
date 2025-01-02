package id.orbion.ecommerce_app.service;

import id.orbion.ecommerce_app.entity.Order;
import id.orbion.ecommerce_app.model.PaymentNotification;
import id.orbion.ecommerce_app.model.PaymentResponse;

public interface PaymentService {

    PaymentResponse create(Order order);

    PaymentResponse findByPaymentId(String payment);

    boolean verifyByPaymentId(String paymentId);

    void handleNotification(PaymentNotification paymentNotification);

}
