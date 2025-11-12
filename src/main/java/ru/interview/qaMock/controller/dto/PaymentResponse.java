package ru.interview.qaMock.controller.dto;

import ru.interview.qaMock.model.PaymentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String paymentId;
    private PaymentStatus status;
    private Long amount;
    private String currency;
    private Long refundedAmount;

    // Конструктор для ответа init (без refundedAmount)
    public PaymentResponse(String paymentId, PaymentStatus status, Long amount, String currency) {
        this.paymentId = paymentId;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.refundedAmount = 0L;
    }
}