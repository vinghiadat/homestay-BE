package com.example.homestay.homestaytype;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.homestay.exception.AlreadyExistsException;
import com.example.homestay.exception.NotFoundException;
import com.example.homestay.homestayroom.RoomRepository;
import com.example.homestay.logger.LoggerService;
import com.example.homestay.role.Role;
import com.example.homestay.user.User;
import com.example.homestay.user.UserRepository;

@Service
public class TypeService {
    private TypeRepository typeRepository;
    private RoomRepository roomRepository;
    private LoggerService loggerService;
    private UserRepository userRepository;
    @Autowired
    public TypeService(TypeRepository typeRepository, RoomRepository roomRepository,LoggerService loggerService,UserRepository userRepository) {
        this.typeRepository = typeRepository;
        this.roomRepository = roomRepository;
        this.loggerService = loggerService;
        this.userRepository = userRepository;
    }
    public List<Type> getAllTypes(String typeName) {
        List<Type> types = typeRepository.findAll();
        if(typeName!=null) {
            types.removeIf((o) -> !o.getTypeName().contains(typeName));
        }
        
        return types;
    }
    public void deleteTypeById(Integer id, Integer userId) {
        
        Type t = typeRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại loại homestay với id "+id));
        if(roomRepository.existsByTypeId(id)) {
            throw new AlreadyExistsException("Dữ liệu loại homestay \""+t.getTypeName()+"\" đang được sử dụng");
        }
        if(userId!=null) {
            User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Không tồn tại user"));
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
            loggerService.addLogger(user, "- Xóa loại homtstay: "+t.getTypeName(),roleName);
            typeRepository.deleteById(id);
        } 
        
    }
    public void updateTypeById(Integer id, Integer userId,Type type) {
        //Kiểm tra id có tồn tại không
        Type type2 = typeRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại loại homestay với id: "+id));
        type2.setImg(type.getImg());
        type2.setTypeName(type.getTypeName());
        typeRepository.save(type2);
        //Viết logger
        String content = "- Cập nhật loại homestay: \"Tên loại homestay: "+type2.getTypeName()+"\"";
        addLogger(userId, content);
    }
    public void addType(Type type,Integer userId) {
        typeRepository.save(type);
        addLogger(userId, "- Thêm loại homestay: \"Tên loại homestay: "+type.getTypeName()+"\"");
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
    public Type getInfoById(Integer id) {
        return typeRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại loại homestay với id: "+id));
    }
}
