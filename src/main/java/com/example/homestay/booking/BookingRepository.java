package com.example.homestay.booking;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByOrderByRegistrationDateAsc();
    Boolean existsByRoomId(Integer roomId);
    List<Booking> findByRoomId(Integer roomId);
}
