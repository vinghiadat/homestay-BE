package com.example.event.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.management.relation.RoleNotFoundException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.event.authentication.AuthenticatedUserDTO;
import com.example.event.config.JwtGenerator;
import com.example.event.exception.AlreadyExistsException;
import com.example.event.exception.InvalidValueException;
import com.example.event.exception.NotFoundException;
import com.example.event.logger.LoggerService;
import com.example.event.role.Role;
import com.example.event.role.RoleRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private RoleRepository roleRepository;

    private AuthenticationManager authenticationManager;

    private JwtGenerator jwtGenerator;

    private LoggerService loggerService;

    @Autowired //tiêm phụ thuộc vào
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository,
            AuthenticationManager authenticationManager, JwtGenerator jwtGenerator, LoggerService loggerService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
        this.loggerService = loggerService;
    }
    public User getInfoByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Không tồn tại user có username: "+username));
    }
    @Transactional //Đánh dấu là 1 giao dịch, nếu có vấn đề nó rollback hết
    public void register(User user,List<String> roleNames) {

        // Nếu tài khoản tồn tại thì ném ra exception

        if(userRepository.existsByUsername(user.getUsername())) {
            throw new AlreadyExistsException("Tài khoản bạn đăng ký đã tồn tại rồi");
        }

        // Thêm các role được chọn vào user
        List<Role> roles = new ArrayList<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName);
            if (role != null) {
                roles.add(role);
            } else {
                throw new NotFoundException("Role " + roleName + " not found");
            }
        }
        user.setRoles(roles);
        // Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save Mật khẩu
        userRepository.save(user);
    }

    public AuthenticatedUserDTO login(User user) {
        User u = userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new NotFoundException("Không tồn tại tài khoản này"));
        if(!u.getStatus()) {
            throw new InvalidValueException("Tài khoản đã bị khóa");
        }
        Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.toList());

        AuthenticatedUserDTO authenticatedUser = new AuthenticatedUserDTO();
        authenticatedUser.setToken(token);
        authenticatedUser.setRoles(roles);
        return authenticatedUser;
    }

    public List<String> getRoleByUsername(String username) {
        if(!userRepository.existsByUsername(username)) {
            throw new UsernameNotFoundException(username + " not found");
        }else { 
            return userRepository.findRoleNamesByUsername(username);
        }
    }
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));

        // Check if the old password matches
        if (!passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
            throw new InvalidValueException("Mật khẩu cũ không đúng");
        }

        // Set and encode the new password
        existingUser.setPassword(passwordEncoder.encode(newPassword));

        // Save the updated user with the new password
        userRepository.save(existingUser);
    }

    public List<User> getAllUsers(String username) {
        List<User> users = userRepository.findAll();
        users.removeIf((u) -> !(u.getFullname().contains(username) || u.getUsername().contains(username) ));
        return users;
    }
    @Transactional
    public void deleteById(Integer id,Integer userId) {

    }
    public void updateById(Integer id, Integer userId,User user) {
        User user2 = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại người dùng này"));
        user2.setStatus(user.getStatus());
        String trangThai = user.getStatus() ? "Hoạt động": "Khóa";
        addLogger(userId,"- Cập nhật trạng thái tài khoản \""+user2.getUsername()+"\" thành "+trangThai);
        userRepository.save(user2);
    }
    private void addLogger(Integer userId,String content) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Không tồn tại người dùng "));
        String roleName = new String();
            boolean lastElement = false;

            // Lặp qua các role
            for (int i = 0; i < user.getRoles().size(); i++) {
                Role r = user.getRoles().get(i);
                roleName += r.getName();
                
                // Kiểm tra nếu phần tử hiện tại là phần tử cuối cùng
                if (i == user.getRoles().size() - 1) {
                    lastElement = true;
                }
                
                // Nếu không phải phần tử cuối cùng, thêm dấu phẩy
                if (!lastElement) {
                    roleName += " ,";
                }
            }
        loggerService.addLogger(user,content,roleName);
    }
}
