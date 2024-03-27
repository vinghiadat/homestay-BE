package com.example.event.organizer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.event.event.EventRepository;
import com.example.event.exception.AlreadyExistsException;
import com.example.event.exception.NotFoundException;
import com.example.event.logger.LoggerService;

@Service
public class OrganizerService {
    private OrganizerRepository organizerRepository;
    private EventRepository eventRepository;
    private LoggerService loggerService;
    @Autowired
    public OrganizerService(OrganizerRepository organizerRepository, EventRepository eventRepository,LoggerService loggerService) {
        this.organizerRepository = organizerRepository;
        this.eventRepository = eventRepository;
        this.loggerService = loggerService;
    }
    public List<Organizer> getAllOrganizers() {
        return organizerRepository.findAll();
    }
    public void deleteOrganizerById(Integer id) {
        if(eventRepository.existsByOrganizerId(id)) {
            throw new AlreadyExistsException("Dữ liệu nhà tổ chức với id: "+id+" đang được sử dụng");
        }
        loggerService.addLogger(null, null, null);
        organizerRepository.deleteById(id);
    }
    public void updateOrganizerById(Integer id,Organizer organizer) {
        //Kiểm tra id có tồn tại không
        Organizer organizer2 = organizerRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tồn tại nhà tổ chức với id: "+id));
        organizer2.setAddress(organizer.getAddress());
        organizer2.setEmail(organizer.getEmail());
        organizer2.setImg(organizer.getImg());
        organizer2.setOrganizerName(organizer.getOrganizerName());
        organizer2.setPhone(organizer.getPhone());
        organizerRepository.save(organizer2);

    }
}
