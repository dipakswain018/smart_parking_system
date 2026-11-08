package com.campus.parking.service;
import com.campus.parking.entity.AuditEvent;
import com.campus.parking.repository.AuditRepository;
import org.springframework.stereotype.Service;
@Service
public class AuditService {
    private final AuditRepository repo;
    public AuditService(AuditRepository repo){this.repo=repo;}
    public void record(String actor,String action,String details){
        AuditEvent e=new AuditEvent(); e.setActor(actor); e.setAction(action); e.setDetails(details); repo.save(e);
    }
}
