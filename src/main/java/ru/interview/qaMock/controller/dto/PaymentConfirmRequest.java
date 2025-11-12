package ru.interview.qaMock.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequest {
    
    @NotBlank(message = "PaymentId is required")
    private String paymentId;
}