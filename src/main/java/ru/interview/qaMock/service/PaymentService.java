package ru.interview.qaMock.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.interview.qaMock.controller.dto.PaymentInitRequest;
import ru.interview.qaMock.controller.dto.PaymentRefundRequest;
import ru.interview.qaMock.model.Payment;
import ru.interview.qaMock.model.PaymentStatus;

import java.util.Currency;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
        boolean orderExists = payments.values().stream()
                .anyMatch(p -> request.getOrderId().equals(p.getOrderId()));
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

        // БАГ #6: Создает фиктивный платеж для несуществующего ID
        if (payment == null) {
            log.warn("Payment not found, creating fake payment: {}", paymentId);
            // ДОЛЖНО БЫТЬ: throw new IllegalArgumentException("Payment not found");
            payment = new Payment(paymentId, "unknown", 0L,
                    Currency.getInstance("RUB"), null, PaymentStatus.DEPOSITED, 0L);
            payments.put(paymentId, payment);
            return payment;
        }

        // БАГ #7: Разрешает повторное подтверждение
        if (payment.getStatus() == PaymentStatus.DEPOSITED) {
            log.warn("Payment already confirmed: {}", paymentId);
            // ДОЛЖНО БЫТЬ: throw new IllegalStateException("Payment already confirmed");
        }

        // БАГ #8: Разрешает переход из REFUNDED в DEPOSITED
        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            log.warn("Confirming refunded payment: {}", paymentId);
            // ДОЛЖНО БЫТЬ: запретить подтверждение возвращенного платежа
        }

        payment.setStatus(PaymentStatus.DEPOSITED);
        log.info("Payment confirmed successfully: {}", paymentId);
        return payment;
    }

    public Payment refund(PaymentRefundRequest request) {
        log.debug("Processing refund for payment: {}, amount: {}",
                request.getPaymentId(), request.getAmount());

        Payment payment = payments.get(request.getPaymentId());

        if (payment == null) {
            log.warn("Payment not found for refund: {}", request.getPaymentId());
            throw new IllegalArgumentException("Payment not found");
        }

        // БАГ #9: Разрешает возврат из любого статуса
        if (payment.getStatus() != PaymentStatus.DEPOSITED) {
            log.warn("Refunding payment with status {}: {}",
                    payment.getStatus(), request.getPaymentId());
            // ДОЛЖНО БЫТЬ: throw new IllegalStateException("Payment must be confirmed before refund");
        }

        // БАГ #10: Не проверяет превышение суммы возврата
        long totalRefunded = payment.getRefundedAmount() + request.getAmount();
        if (totalRefunded > payment.getAmount()) {
            log.warn("Refund amount {} exceeds payment amount {} for payment: {}",
                    totalRefunded, payment.getAmount(), request.getPaymentId());
            // ДОЛЖНО БЫТЬ: throw new IllegalArgumentException("Refund amount exceeds payment amount");
        }

        // БАГ #11: Перезаписывает refundedAmount вместо добавления
        payment.setRefundedAmount(request.getAmount());  // ДОЛЖНО БЫТЬ: += request.getAmount()
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
}