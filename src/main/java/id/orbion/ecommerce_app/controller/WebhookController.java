package id.orbion.ecommerce_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.orbion.ecommerce_app.model.ApiResponse;
import id.orbion.ecommerce_app.model.PaymentNotification;
import id.orbion.ecommerce_app.service.PaymentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final PaymentService paymentService;

    @PostMapping("/xendit")
    public ResponseEntity<ApiResponse> handleXenditWebhook(
            @RequestBody PaymentNotification paymentNotification) {
        paymentService.handleNotification(paymentNotification);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Webhook handled successfully")
                        .build());
    }

}
