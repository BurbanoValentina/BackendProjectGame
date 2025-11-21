package com.example.gamebackend.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.example.gamebackend.model.Game;
import com.example.gamebackend.patterns.GameFactory;
import com.example.gamebackend.repository.GameRepository;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> getAllGames() {
        List<Game> games = gameRepository.findAll();
        games.sort(Comparator.comparing(Game::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return games;
    }

    public Game getGameById(String id) {
        String safeId = Objects.requireNonNull(id, "El id no puede ser nulo");
        return gameRepository.findById(safeId).orElse(null);
    }

    public Game createGameSession(String playerName, String difficulty) {
        String safePlayerName = Objects.requireNonNull(playerName, "El nombre del jugador es obligatorio");
        String safeDifficulty = Objects.requireNonNull(difficulty, "La dificultad es obligatoria");
        Game builtGame = Objects.requireNonNull(
            GameFactory.createGame(safePlayerName, safeDifficulty),
            "La fábrica no puede producir un juego nulo"
        );
        return gameRepository.save(builtGame);
    }

    public Game addGame(Game game) {
        Game safeGame = Objects.requireNonNull(game, "El juego es obligatorio");
        return gameRepository.save(safeGame);
    }

    public Game updateGameProgress(String id, int score, int correctAnswers, int totalQuestions, long durationSeconds) {
        String safeId = Objects.requireNonNull(id, "El id no puede ser nulo");
        Game existing = gameRepository.findById(safeId)
            .orElseThrow(() -> new IllegalArgumentException("La partida con id " + id + " no existe."));
        existing.setScore(score);
        existing.setCorrectAnswers(correctAnswers);
        existing.setTotalQuestions(totalQuestions);
        existing.setDurationSeconds(durationSeconds);
        return gameRepository.save(existing);
    }
}
