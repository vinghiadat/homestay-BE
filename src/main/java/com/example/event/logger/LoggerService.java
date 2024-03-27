package com.example.event.logger;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.event.user.User;

@Service
public class LoggerService {
    @Autowired
    private LoggerRepository loggerRepository;

    public void addLogger(User user, String content, String roleName) {
        Logger logger = new Logger();
        logger.setUsers(user);
        logger.setContent(content);
        logger.setRoleName(roleName);
        loggerRepository.save(logger);
    }
    public List<Logger> getAllLogger() {
        return loggerRepository.findAllByOrderByDateTimeDesc();
    }
}

