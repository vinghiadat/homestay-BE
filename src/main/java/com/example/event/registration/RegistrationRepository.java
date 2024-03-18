package com.example.event.registration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Integer> {
    List<Registration> findByUsersId(Integer userId);
    Boolean existsByUsersIdAndEventId(Integer userId,Integer eventId);
    @Query("SELECT COUNT(r) > 0 FROM Registration r " +
            "WHERE r.users.id = :userId " +
            "AND r.event.startDateTime <= :newEndDateTime "+
            "AND r.event.endDateTime >= :newStartDateTime "+
            "AND (r.event.id <> :eventId)")
    boolean existsRegistrationWithSameDate(@Param("userId") Integer userId,@Param("eventId") Integer eventId,@Param("newStartDateTime") LocalDateTime newStartDateTime, @Param("newEndDateTime") LocalDateTime newEndDateTime);
}
