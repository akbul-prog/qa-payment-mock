package ru.interview.qaMock.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitRequest {

    private Long amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "OrderId is required")
    private String orderId;

    @NotBlank(message = "Client email is required")
    private String clientEmail;
}