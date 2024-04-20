package com.example.homestay.homestaytype;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/type")
@CrossOrigin(origins = {"http://localhost:4200"})
public class TypeResource {
    @Autowired
    private TypeService typeService;

    @GetMapping()
    public ResponseEntity<List<Type>> getAllTypes(@RequestParam(required = false) String typeName) {
        return ResponseEntity.status(HttpStatus.OK).body(typeService.getAllTypes(typeName));
    }
    @GetMapping("{id}")
    public ResponseEntity<Type> getInfoById(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(typeService.getInfoById(id));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Void> addType(@Valid @RequestBody Type type,@PathVariable Integer userId) {
        typeService.addType(type,userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> deleteTypeById(@PathVariable Integer id,@PathVariable Integer userId) {
        typeService.deleteTypeById(id,userId);
        return ResponseEntity.noContent().build();
    }
    //Cập nhật có 2 loại PUT & PATCH
    @PatchMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> updateTypeById(@PathVariable Integer id,@PathVariable Integer userId,@Valid @RequestBody Type type) {
        typeService.updateTypeById(id,userId,type);
        return ResponseEntity.noContent().build();
    }
}
