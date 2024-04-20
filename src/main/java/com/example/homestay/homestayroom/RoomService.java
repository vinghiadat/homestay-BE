package com.example.homestay.homestayroom;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.homestay.exception.InvalidValueException;
import com.example.homestay.exception.NotFoundException;
import com.example.homestay.homestaytype.Type;
import com.example.homestay.homestaytype.TypeRepository;
import com.example.homestay.logger.LoggerService;
import com.example.homestay.role.Role;
import com.example.homestay.user.User;
import com.example.homestay.user.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private TypeRepository typeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoggerService loggerService;
    public List<Room> getByTypeId(Integer typeId) {
        return roomRepository.findByTypeId(typeId);
    }
    public Room getRoomById(Integer roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> new NotFoundException("Không tôn tại homestay này"));
    }
    // public void deleteEventById(Integer id, Integer userId) {
    //     if(registrationRepository.existsByEventId(id)) {
    //         throw new AlreadyExistsException("Sự kiện này đang được sử dụng trong phần đăng ký sự kiện của khách hàng");
    //     }
    //     Event e = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại sự kiện với id "+id));
    //     if(userId!=null) {
    //         User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Không tồn tại user"));
    //         String roleName = new String();
    //         boolean lastElement = false;

    //         // Lặp qua các role
    //         for (int i = 0; i < user.getRoles().size(); i++) {
    //             Role r = user.getRoles().get(i);
    //             roleName += r.getName();
                
    //             // Kiểm tra nếu phần tử hiện tại là phần tử cuối cùng
    //             if (i == user.getRoles().size() - 1) {
    //                 lastElement = true;
    //             }
                
    //             // Nếu không phải phần tử cuối cùng, thêm dấu phẩy
    //             if (!lastElement) {
    //                 roleName += " ,";
    //             }
    //         }
    //         loggerService.addLogger(user, "- Xóa sự kiện: "+e.getEventName(),roleName);
    //         eventRepository.deleteById(id);
    //         List<Activity> activities = activityRepository.findByEventId(id);
    //         if(!activities.isEmpty()) {
    //             for (Activity a : activities) {
    //                 activityRepository.delete(a);
    //             }
    //         }
    //     } 
        
    // }
    public List<Room> getRoomsByMaxQuantityAndTypeIdAndName(
        Integer maxQuantity,
         Integer typeId,
         String roomName
    ) {
        
        Specification<Room> spec = (root, query, cb) -> {
            query.orderBy(
                cb.asc(root.get("type").get("id")),
                cb.asc(root.get("roomName"))
            );
            return cb.conjunction();
        };
        
        List<Room> rooms = roomRepository.findAll(spec);
        if (maxQuantity != null) {
            rooms.removeIf(e ->  !e.getMaxQuantity().equals(maxQuantity));
        }
        if (typeId != null) {
            rooms.removeIf(e ->  !e.getType().getId().equals(typeId));
        }
        if (roomName != null && !roomName.isEmpty()) {
            rooms.removeIf(e -> !e.getRoomName().toLowerCase().contains(roomName.toLowerCase()));
        }
        return rooms;
    }
    public List<Room> getRoomsByTypeIdExcludingRoomId(Integer typeId, Integer roomId) {
        return roomRepository.findTop5ByTypeIdAndIdNot(typeId, roomId);
    }
    @Transactional
    public void addRoom( Room room,
    Integer userId) {
        Type t = typeRepository.findById(room.getType().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại loại homestay với id: "+room.getType().getId()));
        
        if(room.getMaxQuantity()<=0) {
            throw new InvalidValueException("Số lượng tối đa phải >=0");
        }
        String content = "- Thêm homestay "+room.getRoomName()+" thuộc loại homestay "+t.getTypeName();
        roomRepository.save(room);
        addLogger(userId,content);
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
    public void updateRoomById(Integer id, Integer userId,Room room) {
        Room rById = roomRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại homestay với id: "+id));
        Type t = typeRepository.findById(room.getType().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại loại homestay với id: "+room.getType().getId()));
       
        if(room.getMaxQuantity()<=0) {
            throw new InvalidValueException("Số lượng tối đa phải >=0");
        }
        //Kiểm tra id có tồn tại không
        
        //Viết logger
        String content = "- Cập nhật homestay \""+rById.getRoomName()+"\" thành \""+room.getRoomName()+"\"";
        addLogger(userId, content);
        rById.setDescription(room.getDescription());
        rById.setImg(room.getImg());
        rById.setListDevices(room.getListDevices());
        rById.setMaxQuantity(room.getMaxQuantity());
        rById.setRoomName(room.getRoomName());
        rById.setStatus(room.getStatus());
        roomRepository.save(rById);
        
    }
}
