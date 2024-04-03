package com.example.event.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.event.authentication.AuthResponseDTO;
import com.example.event.authentication.AuthenticatedUserDTO;
import com.example.event.message.SuccessMessage;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:4401"})
@RequestMapping("api/v1/user")
public class UserResource {
    
    private UserService userService;

    @Autowired //tiêm phụ thuộc vào
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public ResponseEntity<SuccessMessage> register( @Valid @RequestBody UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setAddress(userRequestDTO.getAddress());
        user.setEmail(userRequestDTO.getEmail());
        user.setFullname(userRequestDTO.getFullname());
        user.setPassword(userRequestDTO.getPassword());
        user.setUsername(userRequestDTO.getUsername());
        user.setPhoneNumber(userRequestDTO.getPhoneNumber());
        // Kiểm tra nếu roleNames không được cung cấp, mặc định sử dụng vai trò 'CUSTOMER
        List<String> roleNames = new ArrayList<>();
        roleNames = userRequestDTO.getRoleNames();
        if(roleNames.isEmpty()) {
            roleNames = Collections.singletonList("USER");
        }
        userService.register(user,roleNames);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessMessage("Bạn đã đăng ký thành công",HttpStatusCode.valueOf(201).value()));
    }

    @PostMapping("login")
    public ResponseEntity<AuthenticatedUserDTO> login(@Valid @RequestBody UserLoginDTO userRequestDTO){ 
        User user = new User();
        user.setPassword(userRequestDTO.getPassword());
        user.setUsername(userRequestDTO.getUsername());
        AuthenticatedUserDTO authenticatedUserDTO = userService.login(user);
        return ResponseEntity.status(HttpStatus.OK).body(authenticatedUserDTO);
    }

    @GetMapping("role/{username}")
    public ResponseEntity<List<String>> getRoleByUsername(@PathVariable("username") String username) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getRoleByUsername(username));
    }
    @GetMapping("/{username}")
    public ResponseEntity<User> getInfoByUsername(@PathVariable("username") String username) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getInfoByUsername(username));
    }
    @PutMapping("change-password/{username}")
    public ResponseEntity<SuccessMessage> changePassword(
            @PathVariable("username") String username,
            @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(username, changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers( @RequestParam(required = false) String username) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.getAllUsers(username));
    }
    @DeleteMapping("{id}/user/{userId}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id,@PathVariable Integer userId) {
        userService.deleteById(id,userId);
        return ResponseEntity.noContent().build();
    }   
    @PatchMapping("{id}/user/{userId}")
    public ResponseEntity<Void> updateById(@PathVariable Integer id,@PathVariable Integer userId,@RequestBody User user) {
        userService.updateById(id,userId,user);
        return ResponseEntity.noContent().build();
    }

}
