package com.example.event.organizer;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/organizer")
@CrossOrigin(origins = {"http://localhost:4200"})
public class OrganizerResource {
    @Autowired
    private OrganizerService organizerService;

    @GetMapping()
    public List<Organizer> getAllOrganizers() {
        return organizerService.getAllOrganizers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganizerById(@PathVariable Integer id) {
        organizerService.deleteOrganizerById(id);
        return ResponseEntity.noContent().build();
    }
    //Cập nhật có 2 loại PUT & PATCH
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateOrganizerById(@PathVariable Integer id,@Valid @RequestBody Organizer organizer) {
        organizerService.updateOrganizerById(id,organizer);
        return ResponseEntity.noContent().build();
    }
}
