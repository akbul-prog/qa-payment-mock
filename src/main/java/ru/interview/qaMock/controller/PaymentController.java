package ru.interview.qaMock.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.interview.qaMock.controller.dto.*;
import ru.interview.qaMock.model.Payment;
import ru.interview.qaMock.service.PaymentService;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/init")
    public ResponseEntity<PaymentResponse> initPayment(@RequestBody PaymentInitRequest request) {
        log.info("Initializing payment for orderId: {}, amount: {} {}",
                request.getOrderId(), request.getAmount(), request.getCurrency());

        try {
            Payment payment = paymentService.initPayment(request);

            PaymentResponse response = new PaymentResponse(
                    payment.getPaymentId(),
                    payment.getStatus(),
                    payment.getAmount(),
                    payment.getCurrency().getCurrencyCode()
            );

            log.info("Payment initialized successfully: {}", payment.getPaymentId());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Payment initialization failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error during payment initialization", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(@RequestBody PaymentConfirmRequest request) {
        log.info("Confirming payment: {}", request.getPaymentId());

        try {
            Payment payment = paymentService.confirm(request.getPaymentId());

            PaymentResponse response = new PaymentResponse(
                    payment.getPaymentId(),
                    payment.getStatus(),
                    payment.getAmount(),
                    payment.getCurrency().getCurrencyCode()
            );

            log.info("Payment confirmed successfully: {}", payment.getPaymentId());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Payment confirmation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Unexpected error during payment confirmation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<PaymentRefundResponse> refundPayment(@RequestBody PaymentRefundRequest request) {
        log.info("Processing refund for payment: {}, amount: {}",
                request.getPaymentId(), request.getAmount());

        try {
            Payment payment = paymentService.refund(request);

            PaymentRefundResponse response = new PaymentRefundResponse(
                    payment.getPaymentId(),
                    payment.getStatus(),
                    request.getAmount()
            );

            log.info("Refund processed successfully for payment: {}", payment.getPaymentId());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Refund processing failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            log.warn("Refund not allowed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Unexpected error during refund processing", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String paymentId) {
        log.info("Getting payment info: {}", paymentId);

        Payment payment = paymentService.getPaymentById(paymentId);

        if (payment == null) {
            log.warn("Payment not found: {}", paymentId);
            return ResponseEntity.notFound().build();
        }

        PaymentResponse response = new PaymentResponse(
                payment.getPaymentId(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getCurrency().getCurrencyCode(),
                payment.getRefundedAmount()
        );

        log.info("Payment info retrieved successfully: {}", paymentId);
        return ResponseEntity.ok(response);
    }
}