package com.campus.parking.entity;

import com.campus.parking.model.*;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="parking_sessions")
public class ParkingSession {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=20) private String vehicleNumber;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private VehicleType vehicleType;
    @ManyToOne(optional=false, fetch=FetchType.EAGER) @JoinColumn(name="slot_id") private ParkingSlot slot;
    @ManyToOne(optional=false, fetch=FetchType.EAGER) @JoinColumn(name="lot_id") private ParkingLot lot;
    @Column(nullable=false) private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    @Column(precision=10, scale=2) private BigDecimal amount;
    private Long billableHours;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private SessionStatus status = SessionStatus.ACTIVE;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    @Column(length=20) private String paymentMethod;
    @JsonIgnore @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="checked_in_by") private AppUser checkedInBy;

    public Long getId(){return id;} public String getVehicleNumber(){return vehicleNumber;} public void setVehicleNumber(String v){vehicleNumber=v;}
    public VehicleType getVehicleType(){return vehicleType;} public void setVehicleType(VehicleType v){vehicleType=v;}
    public ParkingSlot getSlot(){return slot;} public void setSlot(ParkingSlot v){slot=v;}
    public ParkingLot getLot(){return lot;} public void setLot(ParkingLot v){lot=v;}
    public LocalDateTime getEntryTime(){return entryTime;} public void setEntryTime(LocalDateTime v){entryTime=v;}
    public LocalDateTime getExitTime(){return exitTime;} public void setExitTime(LocalDateTime v){exitTime=v;}
    public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
    public Long getBillableHours(){return billableHours;} public void setBillableHours(Long v){billableHours=v;}
    public SessionStatus getStatus(){return status;} public void setStatus(SessionStatus v){status=v;}
    public PaymentStatus getPaymentStatus(){return paymentStatus;} public void setPaymentStatus(PaymentStatus v){paymentStatus=v;}
    public String getPaymentMethod(){return paymentMethod;} public void setPaymentMethod(String v){paymentMethod=v;}
    public AppUser getCheckedInBy(){return checkedInBy;} public void setCheckedInBy(AppUser v){checkedInBy=v;}
}
