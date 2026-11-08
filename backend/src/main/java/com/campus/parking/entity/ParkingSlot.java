package com.campus.parking.entity;

import com.campus.parking.model.SlotStatus;
import com.campus.parking.model.VehicleType;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="parking_slots", uniqueConstraints=@UniqueConstraint(columnNames={"lot_id","slot_number"}))
public class ParkingSlot {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @JsonIgnore @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="lot_id") private ParkingLot lot;
    @Column(nullable=false, length=20) private String slotNumber;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private VehicleType vehicleType;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private SlotStatus status = SlotStatus.AVAILABLE;
    public Long getId(){return id;} public ParkingLot getLot(){return lot;} public void setLot(ParkingLot v){lot=v;}
    public String getSlotNumber(){return slotNumber;} public void setSlotNumber(String v){slotNumber=v;}
    public VehicleType getVehicleType(){return vehicleType;} public void setVehicleType(VehicleType v){vehicleType=v;}
    public SlotStatus getStatus(){return status;} public void setStatus(SlotStatus v){status=v;}
}
