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
    @Query("SELECT COUNT(r.id) FROM Registration r " +
            "WHERE r.users.id = :userId " +
            "AND r.event.startDateTime <= :newEndDateTime "+
            "AND r.event.endDateTime >= :newStartDateTime ")
    Integer existsRegistrationWithSameDate(@Param("userId") Integer userId,@Param("newStartDateTime") LocalDateTime newStartDateTime, @Param("newEndDateTime") LocalDateTime newEndDateTime);
    Registration findByUsersIdAndEventId(Integer userId, Integer eventId);
    Boolean existsByEventId(Integer eventId);
    List<Registration> findByUsers_FullnameContainingOrUsers_UsernameContaining(String fullname, String username);
}
