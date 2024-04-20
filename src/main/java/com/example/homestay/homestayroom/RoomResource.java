package com.example.homestay.homestayroom;
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
@RequestMapping("api/v1/room")
@CrossOrigin(origins = {"http://localhost:4200"})
public class RoomResource {
    @Autowired
    private RoomService roomService;

    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoomById(@PathVariable Integer roomId) {
        Room room = roomService.getRoomById(roomId);

        if (room != null) {
            return ResponseEntity.ok(room);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // @GetMapping("/status")
    // public ResponseEntity<List<Room>> getRoomsByStatus(@RequestParam RoomStatus status) {
    //     List<Room> rooms = roomService.getRoomsByStatus(status);
    //     return ResponseEntity.ok(rooms);
    // }
    @GetMapping("/filter")
    public ResponseEntity<List<Room>> getRoomsByMaxQuantityAndTypeIdAndName(
        @RequestParam(required = false) Integer maxQuantity,
        @RequestParam(required = false) Integer typeId,
        @RequestParam(required = false) String roomName
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.getRoomsByMaxQuantityAndTypeIdAndName(maxQuantity,typeId,roomName));
    }
    @GetMapping("/by-type-excluding")
    public ResponseEntity<List<Room>> getRoomsByTypeIdExcludingRoomId(@RequestParam Integer typeId, @RequestParam Integer roomId) {
        return ResponseEntity.status(HttpStatus.OK).body(roomService.getRoomsByTypeIdExcludingRoomId(typeId, roomId));
    }
    @PostMapping("/user/{userId}")
    public ResponseEntity<Void> addRoom(@Valid @RequestBody Room room,
    @PathVariable Integer userId) {
        
        roomService.addRoom(room,userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    //Cập nhật có 2 loại PUT & PATCH
    @PatchMapping("/{id}/user/{userId}")
    public ResponseEntity<Void> updateRoomById(@PathVariable Integer id,@PathVariable Integer userId,@Valid @RequestBody Room room) {
        roomService.updateRoomById(id,userId,room);
        return ResponseEntity.noContent().build();
    }
    // @DeleteMapping("/{id}/user/{userId}")
    // public ResponseEntity<Void> deleteRoomById(@PathVariable Integer id,@PathVariable Integer userId) {
    //     roomService.deleteRoomById(id,userId);
    //     return ResponseEntity.noContent().build();
    // }
    

}
