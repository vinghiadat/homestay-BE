package com.example.event.role;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.event.exception.AlreadyExistsException;
import com.example.event.exception.NotFoundException;
import com.example.event.logger.LoggerService;
import com.example.event.user.User;
import com.example.event.user.UserRepository;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoggerService loggerService;
    public void addRole(Role role,Integer userId) {
        if(roleRepository.existsByName(role.getName())) {
            throw new AlreadyExistsException("Tên vai trò đã bị trùng");
        }
        String content = "- Thêm vai trò: "+role.getName();
        addLogger(userId, content);
        roleRepository.save(role);
    } 
    private void addLogger(Integer userId,String content) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Không tồn tại user "));
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
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    public void deleteById(Integer id, Integer userId) {
        Role r = roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại vai trò này"));
        String content = "- Xóa vai trò: "+r.getName();
        if(userRepository.existsByRolesContaining(r)) {
            throw new AlreadyExistsException("Không xóa được! Vai trò này đang được sử dụng.");
        }
        addLogger(userId, content);
        roleRepository.deleteById(id);
    }
}
