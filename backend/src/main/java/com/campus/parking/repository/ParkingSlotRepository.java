package com.campus.parking.repository;
import com.campus.parking.entity.ParkingSlot;
import com.campus.parking.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot,Long> {
    List<ParkingSlot> findByLotId(Long lotId);
    Optional<ParkingSlot> findFirstByLotIdAndVehicleTypeAndStatusOrderBySlotNumberAsc(Long lotId, VehicleType type, SlotStatus status);
    long countByStatus(SlotStatus status);
}
