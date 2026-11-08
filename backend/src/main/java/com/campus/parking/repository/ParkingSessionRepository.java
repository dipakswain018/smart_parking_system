package com.campus.parking.repository;
import com.campus.parking.entity.ParkingSession;
import com.campus.parking.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface ParkingSessionRepository extends JpaRepository<ParkingSession,Long> {
    boolean existsByVehicleNumberIgnoreCaseAndStatus(String number, SessionStatus status);
    List<ParkingSession> findByStatusOrderByEntryTimeDesc(SessionStatus status);
    List<ParkingSession> findTop100ByOrderByEntryTimeDesc();
    long countByStatus(SessionStatus status);
    List<ParkingSession> findByExitTimeBetween(LocalDateTime from, LocalDateTime to);
}
