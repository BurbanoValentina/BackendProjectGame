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
 * Servicio para gestionar las salas de juego multijugador
 * Patrones aplicados:
 * - Singleton (a través de @Service de Spring)
 * - Factory (para crear preguntas y salas)
 * - Repository Pattern (almacenamiento en memoria con ConcurrentHashMap)
 */
@Service
public class MultiplayerRoomService {

    // Repository Pattern: Almacenamiento de salas en memoria
    private final ConcurrentHashMap<String, MultiplayerRoom> rooms = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private static final int TOTAL_QUESTIONS = 5;
    private static final int POINTS_PER_CORRECT = 10;

    /**
     * Factory Method: Crea una nueva sala con código único
     */
    public MultiplayerRoom createRoom(String hostPlayerId, String hostUsername) {
        String roomCode = generateRoomCode();
        
        MultiplayerRoom room = new MultiplayerRoom(roomCode, hostPlayerId);
        
        // Agregar el host como primer jugador
        MultiplayerPlayer host = new MultiplayerPlayer(hostPlayerId, hostUsername);
        room.addPlayer(host);
        
        // Agregar bot automáticamente
        MultiplayerPlayer bot = new MultiplayerPlayer.Builder("bot-" + roomCode, "ChatBot")
                .bot(true)
                .build();
        room.addPlayer(bot);
        
        rooms.put(roomCode, room);
        
        return room;
    }

    /**
     * Permite a un jugador unirse a una sala existente
     */
    public MultiplayerRoom joinRoom(String roomCode, String playerId, String username) {
        MultiplayerRoom room = rooms.get(roomCode.toUpperCase());
        
        if (room == null) {
            throw new IllegalArgumentException("Sala no encontrada");
        }
        
        if (room.getStatus() != MultiplayerRoom.RoomStatus.WAITING) {
            throw new IllegalStateException("La sala ya está en juego");
        }
        
        if (room.getPlayers().size() >= 5) {
            throw new IllegalStateException("La sala está llena");
        }
        
        MultiplayerPlayer player = new MultiplayerPlayer(playerId, username);
        room.addPlayer(player);
        
        return room;
    }

    /**
     * Inicia el juego en una sala
     */
    public MultiplayerRoom startGame(String roomCode) {
        MultiplayerRoom room = rooms.get(roomCode.toUpperCase());
        
        if (room == null) {
            throw new IllegalArgumentException("Sala no encontrada");
        }
        
        if (!room.canStart()) {
            throw new IllegalStateException("No se puede iniciar el juego");
        }
        
        // Factory Method: Generar preguntas
        List<MultiplayerQuestion> questions = generateQuestions(TOTAL_QUESTIONS);
        room.setQuestions(questions);
        room.setStatus(MultiplayerRoom.RoomStatus.PLAYING);
        room.setStartedAt(java.time.LocalDateTime.now());
        
        return room;
    }

    /**
     * Procesa la respuesta de un jugador
     */
    public MultiplayerRoom submitAnswer(String roomCode, String playerId, int answer, long responseTime) {
        MultiplayerRoom room = rooms.get(roomCode.toUpperCase());
        
        if (room == null) {
            throw new IllegalArgumentException("Sala no encontrada");
        }
        
        MultiplayerQuestion currentQuestion = room.getCurrentQuestion();
        if (currentQuestion == null) {
            throw new IllegalStateException("No hay pregunta activa");
        }
        
        // Buscar el jugador
        MultiplayerPlayer player = room.getPlayers().stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado"));
        
        // Actualizar estadísticas del jugador
        player.incrementAnsweredCount();
        player.addResponseTime(responseTime);
        
        if (currentQuestion.isCorrect(answer)) {
            player.addScore(POINTS_PER_CORRECT);
        }
        
        // Verificar si todos los jugadores humanos respondieron
        boolean allAnswered = room.getPlayers().stream()
                .filter(p -> !p.isBot())
                .allMatch(p -> p.getAnsweredCount() > room.getCurrentQuestionIndex());
        
        if (allAnswered) {
            // Simular respuesta del bot
            simulateBotAnswer(room);
            
            // Avanzar a la siguiente pregunta
            room.nextQuestion();
            
            // Verificar si el juego terminó
            if (room.isFinished()) {
                room.setStatus(MultiplayerRoom.RoomStatus.FINISHED);
                room.setFinishedAt(java.time.LocalDateTime.now());
            }
        }
        
        return room;
    }

    /**
     * Simula la respuesta del bot con alta precisión
     */
    private void simulateBotAnswer(MultiplayerRoom room) {
        MultiplayerQuestion currentQuestion = room.getCurrentQuestion();
        if (currentQuestion == null) return;
        
        MultiplayerPlayer bot = room.getPlayers().stream()
                .filter(MultiplayerPlayer::isBot)
                .findFirst()
                .orElse(null);
        
        if (bot == null) return;
        
        // Bot tiene 80% de precisión
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
     * Obtiene el ranking de jugadores en una sala
     */
    public List<MultiplayerPlayer> getRanking(String roomCode) {
        MultiplayerRoom room = rooms.get(roomCode.toUpperCase());
        
        if (room == null) {
            throw new IllegalArgumentException("Sala no encontrada");
        }
        
        // Ordenar por: 1) Puntuación descendente, 2) Tiempo promedio ascendente
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
     * Obtiene una sala por su código
     */
    public MultiplayerRoom getRoom(String roomCode) {
        return rooms.get(roomCode.toUpperCase());
    }

    /**
     * Elimina un jugador de una sala
     */
    public void leaveRoom(String roomCode, String playerId) {
        MultiplayerRoom room = rooms.get(roomCode.toUpperCase());
        
        if (room != null) {
            room.removePlayer(playerId);
            
            // Si no quedan jugadores humanos, eliminar la sala
            boolean hasHumanPlayers = room.getPlayers().stream()
                    .anyMatch(p -> !p.isBot());
            
            if (!hasHumanPlayers) {
                rooms.remove(roomCode.toUpperCase());
            }
        }
    }

    /**
     * Factory Method: Genera un código de sala único de 6 caracteres
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
     * Factory Method: Genera una lista de preguntas matemáticas
     */
    private List<MultiplayerQuestion> generateQuestions(int count) {
        List<MultiplayerQuestion> questions = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            questions.add(generateQuestion(i + 1));
        }
        
        return questions;
    }

    /**
     * Factory Method: Genera una pregunta matemática aleatoria
     */
    private MultiplayerQuestion generateQuestion(int id) {
        int a = random.nextInt(20) + 1;
        int b = random.nextInt(20) + 1;
        
        String[] operations = {"+", "-", "*"};
        String operation = operations[random.nextInt(operations.length)];
        
        String prompt;
        int answer;
        
        switch (operation) {
            case "+":
                prompt = a + " + " + b;
                answer = a + b;
                break;
            case "-":
                int max = Math.max(a, b);
                int min = Math.min(a, b);
                prompt = max + " - " + min;
                answer = max - min;
                break;
            case "*":
                prompt = a + " * " + b;
                answer = a * b;
                break;
            default:
                prompt = a + " + " + b;
                answer = a + b;
        }
        
        return new MultiplayerQuestion(id, prompt, answer);
    }

    /**
     * Obtiene todas las salas activas (para debugging)
     */
    public List<MultiplayerRoom> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }
}
