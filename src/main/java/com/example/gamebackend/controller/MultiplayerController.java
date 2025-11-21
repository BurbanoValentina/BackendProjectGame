package com.example.gamebackend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gamebackend.dto.CreateRoomRequest;
import com.example.gamebackend.dto.JoinRoomRequest;
import com.example.gamebackend.dto.SubmitAnswerRequest;
import com.example.gamebackend.model.MultiplayerPlayer;
import com.example.gamebackend.model.MultiplayerRoom;
import com.example.gamebackend.service.MultiplayerRoomService;

import jakarta.validation.Valid;

/**
 * REST Controller applying the MVC Controller Pattern for multiplayer mode.
 */
@RestController
@RequestMapping("/api/multiplayer")
@CrossOrigin(origins = "${app.frontend.url}")
public class MultiplayerController {

    private final MultiplayerRoomService roomService;

    public MultiplayerController(MultiplayerRoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * Creates a new multiplayer room.
     */
    @PostMapping("/rooms/create")
    public ResponseEntity<Map<String, Object>> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        MultiplayerRoom room = roomService.createRoom(request.getPlayerId(), request.getUsername());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("roomCode", room.getRoomCode());
        response.put("room", room);
        response.put("message", "Room created successfully");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Allows a player to join an existing room.
     */
    @PostMapping("/rooms/join")
    public ResponseEntity<Map<String, Object>> joinRoom(@Valid @RequestBody JoinRoomRequest request) {
        MultiplayerRoom room = roomService.joinRoom(
            request.getRoomCode(), 
            request.getPlayerId(), 
            request.getUsername()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("room", room);
        response.put("message", "You joined the room successfully");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Starts the game for the referenced room.
     */
    @PostMapping("/rooms/{roomCode}/start")
    public ResponseEntity<Map<String, Object>> startGame(@PathVariable String roomCode) {
        MultiplayerRoom room = roomService.startGame(roomCode);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("room", room);
        response.put("currentQuestion", room.getCurrentQuestion());
        response.put("message", "Game started");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Submits an answer for the active question.
     */
    @PostMapping("/rooms/answer")
    public ResponseEntity<Map<String, Object>> submitAnswer(@Valid @RequestBody SubmitAnswerRequest request) {
        MultiplayerRoom room = roomService.submitAnswer(
            request.getRoomCode(),
            request.getPlayerId(),
            request.getAnswer(),
            request.getResponseTime()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("room", room);
        response.put("currentQuestion", room.getCurrentQuestion());
        response.put("isFinished", room.isFinished());
        
        if (room.isFinished()) {
            List<MultiplayerPlayer> ranking = roomService.getRanking(request.getRoomCode());
            response.put("ranking", ranking);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Returns the current state for a room.
     */
    @GetMapping("/rooms/{roomCode}")
    public ResponseEntity<Map<String, Object>> getRoom(@PathVariable String roomCode) {
        MultiplayerRoom room = roomService.getRoom(roomCode);
        
        if (room == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Room not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("room", room);
        response.put("currentQuestion", room.getCurrentQuestion());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Fetches the ranking for a room.
     */
    @GetMapping("/rooms/{roomCode}/ranking")
    public ResponseEntity<Map<String, Object>> getRanking(@PathVariable String roomCode) {
        List<MultiplayerPlayer> ranking = roomService.getRanking(roomCode);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("ranking", ranking);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Allows a player to leave the room.
     */
    @PostMapping("/rooms/{roomCode}/leave/{playerId}")
    public ResponseEntity<Map<String, Object>> leaveRoom(
            @PathVariable String roomCode,
            @PathVariable String playerId) {
        
        roomService.leaveRoom(roomCode, playerId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "You left the room");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lists every active room (debug helper).
     */
    @GetMapping("/rooms")
    public ResponseEntity<Map<String, Object>> getAllRooms() {
        List<MultiplayerRoom> rooms = roomService.getAllRooms();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("rooms", rooms);
        response.put("count", rooms.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Manejo de excepciones
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Internal server error: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
