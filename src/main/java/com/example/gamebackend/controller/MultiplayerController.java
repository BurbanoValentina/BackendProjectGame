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
 * Controlador REST para el modo multijugador
 * Patrón: Controller (MVC)
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
     * Crea una nueva sala de juego
     */
    @PostMapping("/rooms/create")
    public ResponseEntity<Map<String, Object>> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        MultiplayerRoom room = roomService.createRoom(request.getPlayerId(), request.getUsername());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("roomCode", room.getRoomCode());
        response.put("room", room);
        response.put("message", "Sala creada exitosamente");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Permite a un jugador unirse a una sala
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
        response.put("message", "Te has unido a la sala exitosamente");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Inicia el juego en una sala
     */
    @PostMapping("/rooms/{roomCode}/start")
    public ResponseEntity<Map<String, Object>> startGame(@PathVariable String roomCode) {
        MultiplayerRoom room = roomService.startGame(roomCode);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("room", room);
        response.put("currentQuestion", room.getCurrentQuestion());
        response.put("message", "Juego iniciado");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Envía una respuesta a la pregunta actual
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
     * Obtiene el estado actual de una sala
     */
    @GetMapping("/rooms/{roomCode}")
    public ResponseEntity<Map<String, Object>> getRoom(@PathVariable String roomCode) {
        MultiplayerRoom room = roomService.getRoom(roomCode);
        
        if (room == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Sala no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("room", room);
        response.put("currentQuestion", room.getCurrentQuestion());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene el ranking de una sala
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
     * Permite a un jugador salir de una sala
     */
    @PostMapping("/rooms/{roomCode}/leave/{playerId}")
    public ResponseEntity<Map<String, Object>> leaveRoom(
            @PathVariable String roomCode,
            @PathVariable String playerId) {
        
        roomService.leaveRoom(roomCode, playerId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Has salido de la sala");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene todas las salas activas (para debugging)
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
        response.put("message", "Error interno del servidor: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
