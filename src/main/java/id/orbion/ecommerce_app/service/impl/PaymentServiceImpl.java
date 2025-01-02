package id.orbion.ecommerce_app.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.xendit.exception.XenditException;
import com.xendit.model.Invoice;

import id.orbion.ecommerce_app.common.error.ResourceNotFoundException;
import id.orbion.ecommerce_app.entity.Order;
import id.orbion.ecommerce_app.entity.User;
import id.orbion.ecommerce_app.model.PaymentNotification;
import id.orbion.ecommerce_app.model.PaymentResponse;
import id.orbion.ecommerce_app.repository.OrderRepository;
import id.orbion.ecommerce_app.repository.UserRepository;
import id.orbion.ecommerce_app.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public PaymentResponse create(Order order) {
        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Map<String, Object> params = new HashMap<>();
        params.put("external_id", order.getOrderId().toString());
        params.put("amount", order.getTotalAmount().doubleValue());
        params.put("payer_email", user.getEmail());
        params.put("description", "Payment for Order #" + order.getOrderId());

        try {
            Invoice invoice = Invoice.create(params);

            return PaymentResponse.builder()
                    .xenditPaymentUrl(invoice.getInvoiceUrl())
                    .xenditExternalId(invoice.getExternalId())
                    .xenditInvoiceId(invoice.getId())
                    .amount(order.getTotalAmount())
                    .xenditInvoiceStatus(invoice.getStatus())
                    .build();
        } catch (XenditException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public PaymentResponse findByPaymentId(String payment) {
        try {
            Invoice invoice = Invoice.getById(payment);

            return PaymentResponse.builder()
                    .xenditPaymentUrl(invoice.getInvoiceUrl())
                    .xenditExternalId(invoice.getExternalId())
                    .xenditInvoiceId(invoice.getId())
                    .amount(BigDecimal.valueOf(invoice.getAmount().doubleValue()))
                    .xenditInvoiceStatus(invoice.getStatus())
                    .build();
        } catch (XenditException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verifyByPaymentId(String paymentId) {
        try {
            Invoice invoice = Invoice.getById(paymentId);
            return "PAID".equals(invoice.getStatus());
        } catch (XenditException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleNotification(PaymentNotification paymentNotification) {
        String invoiceId = paymentNotification.getPaymentId();
        String status = paymentNotification.getStatus();

        Order order = orderRepository.findByXenditInvoiceId(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setXenditPaymentStatus(status);

        switch (status) {
            case "PAID":
                order.setStatus("PAID");
                break;
            case "PENDING":
                order.setStatus("PENDING");
                break;
            case "EXPIRED":
                order.setStatus("CANCELLED");
                break;
            case "FAILED":
                order.setStatus("FAILED");
                break;
            default:
                break;
        }

        if (paymentNotification.getPaymentMethod() != null) {
            order.setXenditPaymentMethod(paymentNotification.getPaymentMethod());
        }

        orderRepository.save(order);
    }

}
