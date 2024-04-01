package com.example.event.role;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:4401"})
@RequestMapping("api/v1/role")
public class RoleResource {
    @Autowired
    private RoleService roleService;
    @PostMapping("/user/{userId}")
    public ResponseEntity<Void> addRole(@Valid @RequestBody Role role,@PathVariable Integer userId) {
        roleService.addRole(role,userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping()
    public ResponseEntity<List<Role>> getAllRoles(){
        return ResponseEntity.status(HttpStatus.OK).body(roleService.getAllRoles());
    }
    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id, @PathVariable Integer userId) {
        roleService.deleteById(id,userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
