package com.example.homestay.homestayroom;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.homestay.booking.Booking;
import com.example.homestay.booking.BookingRepository;
import com.example.homestay.exception.AlreadyExistsException;
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
    @Autowired
    private BookingRepository bookingRepository;
    public List<Room> getByTypeId(Integer typeId) {
        return roomRepository.findByTypeId(typeId);
    }
    public Room getRoomById(Integer roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> new NotFoundException("Không tôn tại homestay này"));
    }
    public void deleteRoomById(Integer id, Integer userId) {
        if(bookingRepository.existsByRoomId(id)) {
            throw new AlreadyExistsException("Homestay này đang được sử dụng trong phần đặt phòng của khách hàng");
        }
        Room room = roomRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại homestay với id "+id));
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
            loggerService.addLogger(user, "- Xóa homestay: "+room.getRoomName(),roleName);
            roomRepository.deleteById(id);
            
        } 
        
    }
    public List<Room> getRoomsByMaxQuantityAndTypeIdAndName(
        Integer maxQuantity,
         Integer typeId,
         String roomName,
         String price,
         LocalDateTime startDateTime,
        LocalDateTime endDateTime
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
        if (roomName != null) {
            rooms.removeIf(e -> !e.getRoomName().toUpperCase().contains(roomName.toUpperCase()));
        }
        
        if(price !=null ) {
            if(price.contains("=")) {
                System.out.println(1);
                price = price.substring(price.indexOf("=")+1);
                Float p = Float.parseFloat(price);
                if(price.contains(">")) {
                    rooms.removeIf(e -> e.getPrice()<p);
                } else if(price.contains("<")) {
                    rooms.removeIf(e -> e.getPrice()>p);
                } else {
                    rooms.removeIf(e -> e.getPrice()!=p);
                }
            } else {
                if(price.contains(">")) {
                    price = price.substring(price.indexOf(">")+1);
                    Float p = Float.parseFloat(price);
                    rooms.removeIf(e -> e.getPrice()<=p);
                } else if(price.contains("<")) {
                    price = price.substring(price.indexOf("<")+1);
                    Float p = Float.parseFloat(price);
                    rooms.removeIf(e -> e.getPrice()>=p);
                }
            }
        }
        if(startDateTime != null && endDateTime !=null) {
            rooms.removeIf(room -> isRoomBooked(room, startDateTime, endDateTime));
        }
        return rooms;
    }
    public boolean isRoomBooked(Room room, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Booking> bookings = bookingRepository.findByRoomId(room.getId());
        if (bookings == null || bookings.isEmpty()) {
            return false; // Nếu không có booking nào thì phòng không được đặt
        }

        // Kiểm tra xem phòng đã được đặt trong khoảng thời gian này hay chưa
        for (Booking booking : bookings) {
            LocalDateTime bookingStart = booking.getStartDateTime();
            LocalDateTime bookingEnd = booking.getEndDateTime();
            if (startDateTime.isBefore(bookingEnd) && endDateTime.isAfter(bookingStart) && booking.getStatus()!=-1) {
                return true; // Phòng đã được đặt trong khoảng thời gian này
            }
        }
        return false; // Phòng chưa được đặt trong khoảng thời gian này
    }
    public List<Room> getRoomsByTypeIdExcludingRoomId(Integer typeId, Integer roomId) {
        return roomRepository.findTop5ByTypeIdAndIdNot(typeId, roomId);
    }
    @Transactional
    public void addRoom( RoomRequestDTO roomRequestDTO,
    Integer userId) {
        Room room = convertRequestDTOToModel(roomRequestDTO);
        Type t = typeRepository.findById(room.getType().getId()).orElseThrow(() -> new NotFoundException("Không tồn tại loại homestay với id: "+room.getType().getId()));
        
        if(room.getMaxQuantity()<=0) {
            throw new InvalidValueException("Số lượng tối đa phải >=0");
        }
        if(room.getPrice()<=0 || room.getPrice()%1000!=0) {
            throw new InvalidValueException("Số lượng tối đa phải >=0 và chia hết cho 1000");
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
    public void updateRoomById(Integer id, Integer userId,RoomRequestDTO roomRequestDTO) {
        Room room = convertRequestDTOToModel(roomRequestDTO);
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
        rById.setMaxQuantity(room.getMaxQuantity());
        rById.setRoomName(room.getRoomName());
        rById.setStatus(room.getStatus());
        rById.setPrice(room.getPrice());
        roomRepository.save(rById);
        
    }
    private Room convertRequestDTOToModel(RoomRequestDTO requestDTO) {
        Room room = new Room();
        room.setDescription(requestDTO.getDescription());
        room.setImg(requestDTO.getImg());
        room.setMaxQuantity(requestDTO.getMaxQuantity());
        room.setPrice(requestDTO.getPrice());
        room.setRoomName(requestDTO.getRoomName());
        room.setStatus(requestDTO.getStatus());
        Type type = typeRepository.findById(requestDTO.getTypeId()).orElseThrow(() -> new NotFoundException("Không tồn tại loại homestay với id: "+requestDTO.getTypeId()));
        room.setType(type);
        return room;
    }
}
