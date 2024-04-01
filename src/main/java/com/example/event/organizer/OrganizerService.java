package com.example.event.organizer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.event.event.EventRepository;
import com.example.event.exception.AlreadyExistsException;
import com.example.event.exception.NotFoundException;
import com.example.event.logger.Logger;
import com.example.event.logger.LoggerService;
import com.example.event.role.Role;
import com.example.event.user.User;
import com.example.event.user.UserRepository;

@Service
public class OrganizerService {
    private OrganizerRepository organizerRepository;
    private EventRepository eventRepository;
    private LoggerService loggerService;
    private UserRepository userRepository;
    @Autowired
    public OrganizerService(OrganizerRepository organizerRepository, EventRepository eventRepository,LoggerService loggerService,UserRepository userRepository) {
        this.organizerRepository = organizerRepository;
        this.eventRepository = eventRepository;
        this.loggerService = loggerService;
        this.userRepository = userRepository;
    }
    public List<Organizer> getAllOrganizers(String organizerName) {
        List<Organizer> organizers = organizerRepository.findAll();
        organizers.removeIf((o) -> !o.getOrganizerName().contains(organizerName));
        return organizers;
    }
    public void deleteOrganizerById(Integer id, Integer userId) {
        
        Organizer o = organizerRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại nhà tổ chức với id "+id));
        if(eventRepository.existsByOrganizerId(id)) {
            throw new AlreadyExistsException("Dữ liệu nhà tổ chức \""+o.getOrganizerName()+"\" đang được sử dụng");
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
            loggerService.addLogger(user, "- Xóa nhà tổ chức: "+o.getOrganizerName(),roleName);
            organizerRepository.deleteById(id);
        } 
        
    }
    public void updateOrganizerById(Integer id, Integer userId,Organizer organizer) {
        //Kiểm tra id có tồn tại không
        Organizer organizer2 = organizerRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại nhà tổ chức với id: "+id));
        organizer2.setAddress(organizer.getAddress());
        organizer2.setEmail(organizer.getEmail());
        organizer2.setImg(organizer.getImg());
        organizer2.setOrganizerName(organizer.getOrganizerName());
        organizer2.setPhone(organizer.getPhone());
        organizerRepository.save(organizer2);
        //Viết logger
        String content = "- Cập nhật nhà tổ chức: \"Tên nhà tổ chức: "+organizer.getOrganizerName()+"\"";
        addLogger(userId, content);
    }
    public void addOrganizer(Organizer organizer,Integer userId) {
        organizerRepository.save(organizer);
        addLogger(userId, "- Thêm nhà tổ chức: \"Tên nhà tổ chức: "+organizer.getOrganizerName()+"\"");
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
    public Organizer getInfoById(Integer id) {
        return organizerRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại nhà tổ chức với id: "+id));
    }
}
