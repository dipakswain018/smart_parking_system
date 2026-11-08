package com.campus.parking.service;

import com.campus.parking.dto.*;
import com.campus.parking.entity.*;
import com.campus.parking.model.*;
import com.campus.parking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.*;
import java.util.*;

@Service
@Transactional
public class ParkingService {
    private final ParkingLotRepository lots; private final ParkingSlotRepository slots;
    private final ParkingSessionRepository sessions; private final UserRepository users;
    private final AuditService audit;
    public ParkingService(ParkingLotRepository lots,ParkingSlotRepository slots,ParkingSessionRepository sessions,
                          UserRepository users,AuditService audit){
        this.lots=lots;this.slots=slots;this.sessions=sessions;this.users=users;this.audit=audit;
    }

    @Transactional(readOnly=true)
    public List<Map<String,Object>> lotViews(){
        return lots.findAll().stream().map(l->{
            List<ParkingSlot> ss=slots.findByLotId(l.getId());
            long free=ss.stream().filter(s->s.getStatus()==SlotStatus.AVAILABLE).count();
            long occupied=ss.stream().filter(s->s.getStatus()==SlotStatus.OCCUPIED).count();
            return Map.<String,Object>of("id",l.getId(),"name",l.getName(),"address",l.getAddress(),
                    "hourlyRate",l.getHourlyRate(),"active",l.isActive(),"totalSlots",ss.size(),
                    "availableSlots",free,"occupiedSlots",occupied);
        }).toList();
    }

    public Map<String,Object> createLot(CreateLotRequest req,String actor){
        ParkingLot lot=new ParkingLot();lot.setName(req.name().trim());lot.setAddress(req.address().trim());
        lot.setHourlyRate(req.hourlyRate());lot=lots.save(lot);
        addSlots(lot,VehicleType.CAR,req.carSlots(),"C");
        addSlots(lot,VehicleType.BIKE,req.bikeSlots(),"B");
        addSlots(lot,VehicleType.EV,req.evSlots(),"E");
        audit.record(actor,"LOT_CREATED","Created lot "+lot.getName());
        Long createdLotId = lot.getId();
        return lotViews().stream().filter(x->x.get("id").equals(createdLotId)).findFirst().orElseThrow();
    }

    private void addSlots(ParkingLot lot,VehicleType type,int count,String prefix){
        for(int i=1;i<=count;i++){ParkingSlot s=new ParkingSlot();s.setLot(lot);s.setVehicleType(type);
            s.setSlotNumber(prefix+String.format("%03d",i));s.setStatus(SlotStatus.AVAILABLE);slots.save(s);}
    }

    @Transactional(readOnly=true)
    public List<Map<String,Object>> slotViews(){
        return slots.findAll().stream().map(s->Map.<String,Object>of("id",s.getId(),"lotId",s.getLot().getId(),
                "lotName",s.getLot().getName(),"slotNumber",s.getSlotNumber(),"vehicleType",s.getVehicleType(),
                "status",s.getStatus())).toList();
    }

    public ParkingSession entry(EntryRequest req,String actor){
        String number=req.vehicleNumber().trim().toUpperCase(Locale.ROOT);
        if(sessions.existsByVehicleNumberIgnoreCaseAndStatus(number,SessionStatus.ACTIVE))
            throw new IllegalArgumentException("This vehicle already has an active parking session.");
        ParkingLot lot=lots.findById(req.lotId()).orElseThrow(()->new IllegalArgumentException("Parking lot not found."));
        if(!lot.isActive()) throw new IllegalArgumentException("Selected parking lot is inactive.");
        ParkingSlot slot=slots.findFirstByLotIdAndVehicleTypeAndStatusOrderBySlotNumberAsc(lot.getId(),req.vehicleType(),SlotStatus.AVAILABLE)
                .orElseThrow(()->new IllegalArgumentException("No available slot for this vehicle type in the selected lot."));
        slot.setStatus(SlotStatus.OCCUPIED); slots.save(slot);
        ParkingSession ps=new ParkingSession();ps.setVehicleNumber(number);ps.setVehicleType(req.vehicleType());
        ps.setLot(lot);ps.setSlot(slot);ps.setEntryTime(LocalDateTime.now());ps.setStatus(SessionStatus.ACTIVE);
        ps.setPaymentStatus(PaymentStatus.PENDING);
        ps.setCheckedInBy(users.findByEmailIgnoreCase(actor).orElse(null));
        ps=sessions.save(ps);audit.record(actor,"VEHICLE_ENTRY","Vehicle "+number+" assigned to "+slot.getSlotNumber());
        return ps;
    }

    public ParkingSession exit(Long id,String actor){
        ParkingSession ps=sessions.findById(id).orElseThrow(()->new IllegalArgumentException("Parking session not found."));
        if(ps.getStatus()!=SessionStatus.ACTIVE)throw new IllegalArgumentException("This parking session is already completed.");
        LocalDateTime now=LocalDateTime.now();
        long minutes=Math.max(1,Duration.between(ps.getEntryTime(),now).toMinutes());
        long hours=Math.max(1,(long)Math.ceil(minutes/60.0));
        BigDecimal amount=ps.getLot().getHourlyRate().multiply(BigDecimal.valueOf(hours)).setScale(2,RoundingMode.HALF_UP);
        ps.setExitTime(now);ps.setBillableHours(hours);ps.setAmount(amount);ps.setStatus(SessionStatus.COMPLETED);
        ps.getSlot().setStatus(SlotStatus.AVAILABLE);slots.save(ps.getSlot());
        ps=sessions.save(ps);audit.record(actor,"VEHICLE_EXIT","Vehicle "+ps.getVehicleNumber()+" checked out; amount "+amount);
        return ps;
    }

    public ParkingSession pay(Long sessionId,String method,String actor){
        ParkingSession ps=sessions.findById(sessionId).orElseThrow(()->new IllegalArgumentException("Parking session not found."));
        if(ps.getStatus()!=SessionStatus.COMPLETED)throw new IllegalArgumentException("Checkout the vehicle before recording payment.");
        if(ps.getPaymentStatus()==PaymentStatus.PAID)throw new IllegalArgumentException("Payment has already been recorded.");
        ps.setPaymentStatus(PaymentStatus.PAID);ps.setPaymentMethod(method);
        ps=sessions.save(ps);audit.record(actor,"PAYMENT_RECORDED","Payment "+method+" for session "+sessionId);
        return ps;
    }

    @Transactional(readOnly=true)
    public List<ParkingSession> active(){return sessions.findByStatusOrderByEntryTimeDesc(SessionStatus.ACTIVE);}
    @Transactional(readOnly=true)
    public List<ParkingSession> history(){return sessions.findTop100ByOrderByEntryTimeDesc();}

    @Transactional(readOnly=true)
    public Map<String,Object> dashboard(){
        List<ParkingSession> today=sessions.findByExitTimeBetween(LocalDate.now().atStartOfDay(),LocalDate.now().plusDays(1).atStartOfDay());
        BigDecimal revenue=today.stream().filter(s->s.getPaymentStatus()==PaymentStatus.PAID)
                .map(ParkingSession::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO,BigDecimal::add);
        long total=slots.count();long occupied=slots.countByStatus(SlotStatus.OCCUPIED);
        long available=slots.countByStatus(SlotStatus.AVAILABLE);
        return Map.of("totalSlots",total,"occupiedSlots",occupied,"availableSlots",available,
                "activeSessions",sessions.countByStatus(SessionStatus.ACTIVE),"todayRevenue",revenue,
                "occupancyRate",total==0?0:Math.round((occupied*100.0)/total));
    }
}
