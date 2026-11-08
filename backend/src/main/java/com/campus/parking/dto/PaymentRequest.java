package com.campus.parking.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
public record PaymentRequest(@NotBlank @Pattern(regexp="CASH|CARD|UPI") String method) {}
