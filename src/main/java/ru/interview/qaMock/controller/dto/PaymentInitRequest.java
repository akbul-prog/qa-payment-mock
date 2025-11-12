package ru.interview.qaMock.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")  // БАГ: валидация на уровне аннотаций не работает
    private Long amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "OrderId is required")
    private String orderId;

    @NotBlank(message = "Client email is required")
    private String clientEmail;
}