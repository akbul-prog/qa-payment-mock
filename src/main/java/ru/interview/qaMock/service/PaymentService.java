package ru.interview.qaMock.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.interview.qaMock.controller.dto.PaymentInitRequest;
import ru.interview.qaMock.controller.dto.PaymentRefundRequest;
import ru.interview.qaMock.model.Payment;
import ru.interview.qaMock.model.PaymentStatus;

import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaymentService {

    private final Map<String, Payment> payments = new ConcurrentHashMap<>();

    public Payment initPayment(PaymentInitRequest request) {
        log.debug("Processing payment init request for orderId: {}", request.getOrderId());

        // БАГ #1: Неправильная валидация amount - разрешает 0
        if (request.getAmount() < 0) {  // ДОЛЖНО БЫТЬ: <= 0
            log.warn("Invalid amount: {}", request.getAmount());
            throw new IllegalArgumentException("Amount must be positive");
        }

        // БАГ #2: Разрешает валюты кроме RUB
        // ДОЛЖНО БЫТЬ:
        // if (!"RUB".equals(request.getCurrency())) {
        //     log.warn("Unsupported currency: {}", request.getCurrency());
        //     throw new IllegalArgumentException("Only RUB currency is supported");
        // }
        log.debug("Currency validation passed: {}", request.getCurrency());

        // БАГ #3: Не проверяет дублирование orderId
        boolean orderExists = !getPaymentsByOrderId(request.getOrderId()).isEmpty();
        if (orderExists) {
            log.warn("Order already exists: {}", request.getOrderId());
            // ДОЛЖНО БЫТЬ: throw new IllegalStateException("Order ID already exists");
        }

        // БАГ #4: Не валидирует email формат
        String email = request.getClientEmail();
        log.debug("Processing email: {}", email);
        // ДОЛЖНО БЫТЬ: валидация email
        // if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
        //     throw new IllegalArgumentException("Invalid email format");
        // }

        // БАГ #5: Работа с минорными единицами - неясность для пользователей
        // Сумма приходит в копейках, но нигде это не документировано

        String paymentId = UUID.randomUUID().toString();
        log.info("Generated paymentId: {}", paymentId);

        Payment payment = new Payment(
                paymentId,
                request.getOrderId(),
                request.getAmount(),
                Currency.getInstance(request.getCurrency()),
                request.getClientEmail(),
                PaymentStatus.STARTED,
                0L
        );

        payments.put(paymentId, payment);
        log.info("Payment stored successfully: {}", paymentId);
        return payment;
    }

    public Payment confirm(String paymentId) {
        log.debug("Processing confirmation for payment: {}", paymentId);

        Payment payment = payments.get(paymentId);

        if (payment == null) {
            throw new IllegalArgumentException("Payment with id: " + paymentId + " was not found");
        }

        List<Payment> paymentsForOrder = getPaymentsByOrderId(payment.getOrderId());
        boolean orderAlreadyPaid = paymentsForOrder.stream()
                .filter(p -> !Objects.equals(p.getPaymentId(), paymentId))
                .anyMatch(p -> !Objects.equals(p.getStatus(), PaymentStatus.STARTED));
        if (orderAlreadyPaid) {
            throw new IllegalArgumentException("Order with id: " + payment.getOrderId() + " was already paid");
        }

        // БАГ #7: Разрешает повторное подтверждение
        if (payment.getStatus() == PaymentStatus.DEPOSITED) {
            log.warn("Payment already confirmed: {}", paymentId);
            // ДОЛЖНО БЫТЬ: throw new IllegalStateException("Payment already confirmed");
        }

        payment.setStatus(PaymentStatus.DEPOSITED);
        log.info("Payment confirmed successfully: {}", paymentId);
        return payment;
    }

    public Payment refund(PaymentRefundRequest request) {
        log.debug("Processing refund for payment: {}, amount: {}", request.getPaymentId(), request.getAmount());

        Payment payment = payments.get(request.getPaymentId());

        if (payment == null) {
            log.warn("Payment not found for refund: {}", request.getPaymentId());
            throw new IllegalArgumentException("Payment not found");
        }

        log.info("Refunding payment with status {}: {}", payment.getStatus(), request.getPaymentId());

        // БАГ #8: Не проверяет превышение суммы возврата
        long totalRefunded = payment.getRefundedAmount() + request.getAmount();
        if (totalRefunded > payment.getAmount()) {
            log.warn("Refund amount {} exceeds payment amount {} for payment: {}",
                    totalRefunded, payment.getAmount(), request.getPaymentId());
            // ДОЛЖНО БЫТЬ: throw new IllegalArgumentException("Refund amount exceeds payment amount");
        }

        payment.setRefundedAmount(totalRefunded);
        payment.setStatus(PaymentStatus.REFUNDED);

        log.info("Refund processed successfully: {}", request.getPaymentId());
        return payment;
    }

    public Payment getPaymentById(String paymentId) {
        log.debug("Retrieving payment: {}", paymentId);
        Payment payment = payments.get(paymentId);

        if (payment == null) {
            log.warn("Payment not found: {}", paymentId);
        } else {
            log.debug("Payment found: {}", paymentId);
        }

        return payment;
    }

    private List<Payment> getPaymentsByOrderId(String orderId) {
        return payments.values().stream()
                .filter(p -> Objects.equals(orderId, p.getOrderId()))
                .collect(Collectors.toUnmodifiableList());
    }
}