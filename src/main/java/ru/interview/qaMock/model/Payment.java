package ru.interview.qaMock.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private String paymentId;
    private String orderId;
    private long amount;
    private Currency currency;
    private String clientEmail;
    private PaymentStatus status;
    private long refundedAmount;
}
