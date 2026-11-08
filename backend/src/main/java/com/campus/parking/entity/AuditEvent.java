package com.campus.parking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="audit_events")
public class AuditEvent {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=120) private String actor;
    @Column(nullable=false, length=80) private String action;
    @Column(nullable=false, length=500) private String details;
    @Column(nullable=false, updatable=false) private LocalDateTime createdAt;
    @PrePersist void created(){createdAt=LocalDateTime.now();}
    public Long getId(){return id;} public String getActor(){return actor;} public void setActor(String v){actor=v;}
    public String getAction(){return action;} public void setAction(String v){action=v;}
    public String getDetails(){return details;} public void setDetails(String v){details=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}
