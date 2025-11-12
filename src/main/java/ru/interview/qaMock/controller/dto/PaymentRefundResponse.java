package ru.interview.qaMock.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.interview.qaMock.model.PaymentStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRefundResponse {
    private String paymentId;
    private PaymentStatus status;
    private Long refundedAmount;  // Минорные единицы (копейки)
}