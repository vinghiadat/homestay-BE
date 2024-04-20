package com.example.homestay.homestayroom;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findTop5ByTypeIdAndIdNot(Integer typeId, Integer roomId);
    Boolean existsByTypeId(Integer typeId);
    List<Room> findAll(Specification<Room> spec);
    List<Room> findByTypeId(Integer typeId);
}
