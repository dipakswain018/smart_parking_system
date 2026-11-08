package com.campus.parking.dto;
import com.campus.parking.model.VehicleType;
import jakarta.validation.constraints.*;
public record EntryRequest(@NotBlank @Size(max=20) @Pattern(regexp="^[A-Za-z0-9 -]+$") String vehicleNumber,
                           @NotNull VehicleType vehicleType, @NotNull Long lotId) {}
