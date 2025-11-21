package com.example.gamebackend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.gamebackend.model.MultiplayerPlayer;
import com.example.gamebackend.model.MultiplayerQuestion;
import com.example.gamebackend.model.MultiplayerRoom;

/**
 * MultiplayerRoomService implements several patterns:
 * - Singleton via Spring's @Service lifecycle.
 * - Factory Method to create rooms and math questions.
 * - Repository Pattern by storing rooms inside an in-memory ConcurrentHashMap.
 */
@Service
public class MultiplayerRoomService {

    // Repository Pattern: in-memory storage for active rooms
    private final ConcurrentHashMap<String, MultiplayerRoom> rooms = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private static final int TOTAL_QUESTIONS = 5;
    private static final int POINTS_PER_CORRECT = 10;

    /**
     * Factory Method: creates a new room with a unique code.
     */
    public MultiplayerRoom createRoom(String hostPlayerId, String hostUsername) {
        String roomCode = generateRoomCode();
        
        MultiplayerRoom room = new MultiplayerRoom(roomCode, hostPlayerId);
        
        // Add the host as the first human player
        MultiplayerPlayer host = new MultiplayerPlayer(hostPlayerId, hostUsername);
        room.addPlayer(host);
        
        // Add the bot automatically
        MultiplayerPlayer bot = new MultiplayerPlayer.Builder("bot-" + roomCode, "ChatBot")
                .bot(true)
                .build();
        room.addPlayer(bot);
        
        rooms.put(roomCode, room);
        
        return room;
    }

    /**
     * Allows a human player to join an existing room.
     */
    public MultiplayerRoom joinRoom(String roomCode, String playerId, String username) {
        MultiplayerRoom room = rooms.get(roomCode.toUpperCase());
        
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }
        
        if (room.getStatus() != MultiplayerRoom.RoomStatus.WAITING) {
            throw new IllegalStateException("The room is already playing");
        }
        
        if (room.getPlayers().size() >= 5) {
            throw new IllegalStateException("The room is already full");
        }
        
        MultiplayerPlayer player = new MultiplayerPlayer(playerId, username);
        room.addPlayer(player);
        
        return room;
    }

    /**
     * Starts the multiplayer match for the provided room code.
     */
    public MultiplayerRoom startGame(String roomCode) {
        MultiplayerRoom room = rooms.get(roomCode.toUpperCase());
        
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }
        
        if (!room.canStart()) {
            throw new IllegalStateException("The game cannot be started yet");
        }
        
        // Factory Method: generate the math questions used in the match
        List<MultiplayerQuestion> questions = generateQuestions(TOTAL_QUESTIONS);
        room.setQuestions(questions);
        room.setStatus(MultiplayerRoom.RoomStatus.PLAYING);
        room.setStartedAt(java.time.LocalDateTime.now());
        
        return room;
    }

    /**
     * Processes a player answer and advances the question flow.
     */
    public MultiplayerRoom submitAnswer(String roomCode, String playerId, int answer, long responseTime) {
        MultiplayerRoom room = rooms.get(roomCode.toUpperCase());
        
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }
        
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
        
        return room;
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
    public List<MultiplayerPlayer> getRanking(String roomCode) {
        MultiplayerRoom room = rooms.get(roomCode.toUpperCase());
        
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }
        
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
    public MultiplayerRoom getRoom(String roomCode) {
        return rooms.get(roomCode.toUpperCase());
    }

    /**
     * Removes a player from the referenced room.
     */
    public void leaveRoom(String roomCode, String playerId) {
        MultiplayerRoom room = rooms.get(roomCode.toUpperCase());
        
        if (room != null) {
            room.removePlayer(playerId);
            
            // Remove the room if no human players remain
            boolean hasHumanPlayers = room.getPlayers().stream()
                    .anyMatch(p -> !p.isBot());
            
            if (!hasHumanPlayers) {
                rooms.remove(roomCode.toUpperCase());
            }
        }
    }

    /**
     * Factory Method: generates a random 6-character room code.
     */
    private String generateRoomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        
        do {
            code.setLength(0);
            for (int i = 0; i < 6; i++) {
                code.append(characters.charAt(random.nextInt(characters.length())));
            }
        } while (rooms.containsKey(code.toString()));
        
        return code.toString();
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
    public List<MultiplayerRoom> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }
}
