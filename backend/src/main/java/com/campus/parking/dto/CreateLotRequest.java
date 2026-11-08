package com.campus.parking.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record CreateLotRequest(@NotBlank @Size(max=100) String name,
                               @NotBlank @Size(max=180) String address,
                               @NotNull @DecimalMin("1.00") BigDecimal hourlyRate,
                               @NotNull @Min(1) @Max(500) Integer carSlots,
                               @NotNull @Min(0) @Max(500) Integer bikeSlots,
                               @NotNull @Min(0) @Max(500) Integer evSlots) {}
