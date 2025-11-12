package ru.interview.qaMock.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRefundRequest {
    @NotNull(message = "PaymentId is required")
    private String paymentId;
    @NotNull(message = "Amount is required")
    private Long amount;
}
