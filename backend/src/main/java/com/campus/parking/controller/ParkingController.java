package com.campus.parking.controller;

import com.campus.parking.dto.*;
import com.campus.parking.entity.ParkingSession;
import com.campus.parking.repository.AuditRepository;
import com.campus.parking.service.ParkingService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api")
public class ParkingController {
    private final ParkingService service; private final AuditRepository audits;
    public ParkingController(ParkingService service,AuditRepository audits){this.service=service;this.audits=audits;}
    @GetMapping("/health") public Map<String,String> health(){return Map.of("status","UP","service","smart-parking-api");}
    @GetMapping("/dashboard") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')") public Map<String,Object> dashboard(){return service.dashboard();}
    @GetMapping("/lots") public List<Map<String,Object>> lots(){return service.lotViews();}
    @PostMapping("/lots") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String,Object>> createLot(@Valid @RequestBody CreateLotRequest req,Authentication auth){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createLot(req,auth.getName()));
    }
    @GetMapping("/slots") public List<Map<String,Object>> slots(){return service.slotViews();}
    @PostMapping("/parking/entry") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')") public ResponseEntity<ParkingSession> entry(@Valid @RequestBody EntryRequest req,Authentication auth){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.entry(req,auth.getName()));
    }
    @GetMapping("/parking/active") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')") public List<ParkingSession> active(){return service.active();}
    @GetMapping("/parking/sessions") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')") public List<ParkingSession> history(){return service.history();}
    @PostMapping("/parking/{id}/exit") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')") public ParkingSession exit(@PathVariable Long id,Authentication auth){return service.exit(id,auth.getName());}
    @PostMapping("/payments/{sessionId}") @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')") public ParkingSession pay(@PathVariable Long sessionId,@Valid @RequestBody PaymentRequest req,Authentication auth){
        return service.pay(sessionId,req.method(),auth.getName());
    }
    @GetMapping("/audit") @PreAuthorize("hasRole('ADMIN')")
    public List<?> audit(){return audits.findTop100ByOrderByCreatedAtDesc();}
}
