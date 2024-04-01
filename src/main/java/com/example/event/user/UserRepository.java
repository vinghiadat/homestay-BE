package com.example.event.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.event.role.Role;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsername(String username);
    
    Optional<User> findByUsername(String username); // Giai quyết vấn đề bị null

    @Query("SELECT r.name FROM User u JOIN u.roles r WHERE u.username = :username")
    List<String> findRoleNamesByUsername(@Param("username") String username);
    List<User> findByFullname(String fullname);
    Boolean existsByRolesContaining(Role role);
    
}
