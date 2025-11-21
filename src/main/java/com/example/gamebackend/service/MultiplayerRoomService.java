package com.example.gamebackend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.gamebackend.model.MultiplayerPlayer;
import com.example.gamebackend.model.MultiplayerQuestion;
import com.example.gamebackend.model.MultiplayerRoom;
import com.example.gamebackend.repository.MultiplayerRoomRepository;

/**
 * MultiplayerRoomService implements several patterns:
 * - Singleton via Spring's @Service lifecycle.
 * - Factory Method to create rooms and math questions.
 * - Repository Pattern via Spring Data JPA repositories persisted in SQLite.
 */
@Service
public class MultiplayerRoomService {

    private final MultiplayerRoomRepository roomRepository;
    private final Random random = new Random();
    private static final int TOTAL_QUESTIONS = 5;
    private static final int POINTS_PER_CORRECT = 10;

    public MultiplayerRoomService(MultiplayerRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * Factory Method: creates a new room with a unique code.
     * WARNING: Only one active room is allowed at a time.
     */
    @Transactional
    public MultiplayerRoom createRoom(String hostPlayerId, String hostUsername) {
        List<MultiplayerRoom> activeRooms = roomRepository.findAll().stream()
                .filter(r -> r.getStatus() != MultiplayerRoom.RoomStatus.FINISHED)
                .toList();
        
        if (!activeRooms.isEmpty()) {
            MultiplayerRoom existingRoom = activeRooms.get(0);
            throw new IllegalStateException("Only one active room is allowed. Join room: " + existingRoom.getRoomCode());
        }
        
        String roomCode = generateRoomCode();
        
        MultiplayerRoom room = new MultiplayerRoom(roomCode, hostPlayerId);
        
        MultiplayerPlayer host = new MultiplayerPlayer(hostPlayerId, hostUsername);
        room.addPlayer(host);
        
        MultiplayerPlayer bot = new MultiplayerPlayer.Builder("bot-" + roomCode, "ChatBot")
                .bot(true)
                .build();
        room.addPlayer(bot);
        
        return hydrateRoom(roomRepository.save(room));
    }

    /**
     * Allows a human player to join an existing room.
     */
    @Transactional
    public MultiplayerRoom joinRoom(String roomCode, String playerId, String username) {
        String normalizedCode = normalizeRoomCode(roomCode);
        MultiplayerRoom room = roomRepository.findById(Objects.requireNonNull(normalizedCode))
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        
        if (room.getStatus() != MultiplayerRoom.RoomStatus.WAITING) {
            throw new IllegalStateException("The room is already playing");
        }
        
        if (room.getPlayers().size() >= 5) {
            throw new IllegalStateException("The room is already full");
        }
        
        MultiplayerPlayer player = new MultiplayerPlayer(playerId, username);
        room.addPlayer(player);
        
        return hydrateRoom(roomRepository.save(room));
    }

    /**
     * Starts the multiplayer match for the provided room code.
     * Only the host (admin) can start the game.
     */
    @Transactional
    public MultiplayerRoom startGame(String roomCode, String playerId) {
        String normalizedCode = normalizeRoomCode(roomCode);
        MultiplayerRoom room = roomRepository.findById(Objects.requireNonNull(normalizedCode))
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        
        if (!room.getHostPlayerId().equals(playerId)) {
            throw new IllegalStateException("Only the host can start the game");
        }
        
        if (!room.canStart()) {
            throw new IllegalStateException("The game cannot be started yet");
        }
        
        List<MultiplayerQuestion> questions = generateQuestions(TOTAL_QUESTIONS);
        room.setQuestions(questions);
        room.setStatus(MultiplayerRoom.RoomStatus.PLAYING);
        room.setStartedAt(java.time.LocalDateTime.now());
        
        return hydrateRoom(roomRepository.save(room));
    }

    /**
     * Processes a player answer and advances the question flow.
     */
    @Transactional
    public MultiplayerRoom submitAnswer(String roomCode, String playerId, int answer, long responseTime) {
        String normalizedCode = normalizeRoomCode(roomCode);
        MultiplayerRoom room = roomRepository.findById(Objects.requireNonNull(normalizedCode))
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        
        MultiplayerQuestion currentQuestion = room.getCurrentQuestion();
        if (currentQuestion == null) {
            throw new IllegalStateException("There is no active question");
        }

        if (answer < 0 || answer > 999) {
            throw new IllegalArgumentException("Answers must contain between 1 and 3 numeric digits");
        }
        
        // Locate the player inside the room roster
        MultiplayerPlayer player = room.getPlayers().stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));
        
        // Update per-player statistics based on the attempt
        player.incrementAnsweredCount();
        player.addResponseTime(responseTime);
        
        if (currentQuestion.isCorrect(answer)) {
            player.addScore(POINTS_PER_CORRECT);
        }
        
        // Check whether all human players already answered
        boolean allAnswered = room.getPlayers().stream()
                .filter(p -> !p.isBot())
                .allMatch(p -> p.getAnsweredCount() > room.getCurrentQuestionIndex());
        
        if (allAnswered) {
            // Simulate bot participation so each round advances consistently
            simulateBotAnswer(room);
            
            // Avanzar a la siguiente pregunta
            room.nextQuestion();
            
            // Determine if the match already finished
            if (room.isFinished()) {
                room.setStatus(MultiplayerRoom.RoomStatus.FINISHED);
                room.setFinishedAt(java.time.LocalDateTime.now());
            }
        }
        
        return hydrateRoom(roomRepository.save(room));
    }

    /**
     * Simulates a bot response with configurable accuracy.
     */
    private void simulateBotAnswer(MultiplayerRoom room) {
        MultiplayerQuestion currentQuestion = room.getCurrentQuestion();
        if (currentQuestion == null) return;
        
        MultiplayerPlayer bot = room.getPlayers().stream()
                .filter(MultiplayerPlayer::isBot)
                .findFirst()
                .orElse(null);
        
        if (bot == null) return;
        
        // Bot accuracy is set to roughly 80%
        boolean isCorrect = random.nextDouble() > 0.2;
        
        // Tiempo de respuesta del bot: entre 2 y 5 segundos
        long responseTime = 2000 + random.nextInt(3000);
        
        bot.incrementAnsweredCount();
        bot.addResponseTime(responseTime);
        
        if (isCorrect) {
            bot.addScore(POINTS_PER_CORRECT);
        }
    }

    /**
     * Builds the ranking for a room.
     */
    @Transactional(readOnly = true)
    public List<MultiplayerPlayer> getRanking(String roomCode) {
        String normalizedCode = normalizeRoomCode(roomCode);
        MultiplayerRoom room = roomRepository.findById(Objects.requireNonNull(normalizedCode))
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        
        // Sort by: score (desc) and average response time (asc)
        return room.getPlayers().stream()
                .sorted((p1, p2) -> {
                    int scoreCompare = Integer.compare(p2.getScore(), p1.getScore());
                    if (scoreCompare != 0) return scoreCompare;
                    return Double.compare(p1.getAverageResponseTime(), p2.getAverageResponseTime());
                })
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Looks up a room by its code.
     */
    @Transactional(readOnly = true)
    public MultiplayerRoom getRoom(String roomCode) {
        if (roomCode == null || roomCode.isBlank()) {
            return null;
        }
        String normalizedCode = normalizeRoomCode(roomCode);
        return hydrateRoom(roomRepository.findById(Objects.requireNonNull(normalizedCode)).orElse(null));
    }

    /**
     * Removes a player from the referenced room.
     */
    @Transactional
    public void leaveRoom(String roomCode, String playerId) {
        String normalizedCode = normalizeRoomCode(roomCode);
        MultiplayerRoom room = roomRepository.findById(Objects.requireNonNull(normalizedCode)).orElse(null);
        
        if (room == null) {
            return;
        }

        room.removePlayer(playerId);

        boolean hasHumanPlayers = room.getPlayers().stream()
                .anyMatch(p -> !p.isBot());

        if (!hasHumanPlayers) {
            roomRepository.delete(room);
        } else {
            roomRepository.save(room);
        }
    }

    /**
     * Factory Method: generates a random 6-character room code.
     */
    private String generateRoomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        String candidate;
        
        do {
            code.setLength(0);
            for (int i = 0; i < 6; i++) {
                code.append(characters.charAt(random.nextInt(characters.length())));
            }
            candidate = code.toString();
        } while (roomRepository.existsById(Objects.requireNonNull(candidate)));
        
        return candidate;
    }

    /**
     * Factory Method: generates a list of math questions.
     */
    private List<MultiplayerQuestion> generateQuestions(int count) {
        List<MultiplayerQuestion> questions = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            questions.add(generateQuestion(i + 1));
        }
        
        return questions;
    }

    /**
     * Factory Method: generates a single math question.
     */
    private MultiplayerQuestion generateQuestion(int id) {
        int a = random.nextInt(20) + 1;
        int b = random.nextInt(20) + 1;
        
        String[] operations = {"+", "-", "*"};
        String operation = operations[random.nextInt(operations.length)];
        
        return switch (operation) {
            case "+" -> new MultiplayerQuestion(id, a + " + " + b, a + b);
            case "-" -> {
                int max = Math.max(a, b);
                int min = Math.min(a, b);
                yield new MultiplayerQuestion(id, max + " - " + min, max - min);
            }
            case "*" -> new MultiplayerQuestion(id, a + " * " + b, a * b);
            default -> new MultiplayerQuestion(id, a + " + " + b, a + b);
        };
    }

    /**
     * Exposes all rooms (useful for diagnostics).
     */
    @Transactional(readOnly = true)
    public List<MultiplayerRoom> getAllRooms() {
        List<MultiplayerRoom> rooms = new ArrayList<>(roomRepository.findAll());
        rooms.forEach(this::hydrateRoom);
        return rooms;
    }

    private String normalizeRoomCode(String roomCode) {
        if (roomCode == null || roomCode.isBlank()) {
            throw new IllegalArgumentException("Room code is required");
        }
        return roomCode.toUpperCase();
    }

    private MultiplayerRoom hydrateRoom(MultiplayerRoom room) {
        if (room != null) {
            room.getPlayers().size();
            room.getQuestions().size();
        }
        return room;
    }
}
