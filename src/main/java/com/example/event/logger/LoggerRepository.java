package com.example.event.logger;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoggerRepository extends JpaRepository<Logger,Integer> {
    List<Logger> findAllByOrderByDateTimeDesc();
}
