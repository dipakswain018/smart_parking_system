package com.campus.parking.config;

import com.campus.parking.entity.*;
import com.campus.parking.model.*;
import com.campus.parking.repository.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(
            UserRepository users,
            ParkingLotRepository lots,
            ParkingSlotRepository slots,
            PasswordEncoder encoder) {

        return args -> {

            // =========================
            // CREATE ADMIN ACCOUNT
            // =========================

            if (!users.existsByEmailIgnoreCase("ds1137727@gmail.com")) {

                AppUser admin = new AppUser();

                admin.setEmail("ds1137727@gmail.com");
                admin.setFullName("System Administrator");
                admin.setPassword(encoder.encode("Dipak@123"));
                admin.setRole(com.campus.parking.model.Role.ADMIN);

                users.save(admin);

                System.out.println("ADMIN ACCOUNT CREATED SUCCESSFULLY!");
            }

            // =========================
            // CREATE OPERATOR ACCOUNT
            // =========================

            if (!users.existsByEmailIgnoreCase("yhwach574@gmail.com")) {

                AppUser operator = new AppUser();

                operator.setEmail("yhwach574@gmail.com");
                operator.setFullName("Parking Operator");
                operator.setPassword(encoder.encode("Yhwach@123"));
                operator.setRole(com.campus.parking.model.Role.OPERATOR);

                users.save(operator);

                System.out.println("OPERATOR ACCOUNT CREATED SUCCESSFULLY!");
            }

            // =========================
            // CREATE PARKING LOT
            // =========================

            if (lots.count() == 0) {

                ParkingLot lot = new ParkingLot();

                lot.setName("Central Campus Parking");
                lot.setAddress("Main Campus, City Centre");
                lot.setHourlyRate(new BigDecimal("30.00"));

                lot = lots.save(lot);

                createSlots(slots, lot, VehicleType.CAR, "C", 20);

                createSlots(slots, lot, VehicleType.BIKE, "B", 30);

                createSlots(slots, lot, VehicleType.EV, "E", 8);

                System.out.println("PARKING LOT AND SLOTS CREATED SUCCESSFULLY!");
            }
        };
    }

    // =========================
    // CREATE PARKING SLOTS
    // =========================

    private void createSlots(
            ParkingSlotRepository repo,
            ParkingLot lot,
            VehicleType type,
            String prefix,
            int count) {

        for (int i = 1; i <= count; i++) {

            ParkingSlot slot = new ParkingSlot();

            slot.setLot(lot);
            slot.setVehicleType(type);
            slot.setSlotNumber(prefix + String.format("%03d", i));
            slot.setStatus(SlotStatus.AVAILABLE);

            repo.save(slot);
        }
    }
}