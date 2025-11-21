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
        String safeId = Objects.requireNonNull(id, "Id cannot be null");
        return gameRepository.findById(safeId).orElse(null);
    }

    public Game createGameSession(String playerName, String difficulty) {
        String safePlayerName = Objects.requireNonNull(playerName, "Player name is required");
        String safeDifficulty = Objects.requireNonNull(difficulty, "Difficulty is required");
        Game builtGame = Objects.requireNonNull(
            GameFactory.createGame(safePlayerName, safeDifficulty),
            "The factory cannot return a null game"
        );
        return gameRepository.save(builtGame);
    }

    public Game addGame(Game game) {
        Game safeGame = Objects.requireNonNull(game, "Game payload is required");
        return gameRepository.save(safeGame);
    }

    public Game updateGameProgress(String id, int score, int correctAnswers, int totalQuestions, long durationSeconds) {
        String safeId = Objects.requireNonNull(id, "Id cannot be null");
        Game existing = gameRepository.findById(safeId)
            .orElseThrow(() -> new IllegalArgumentException("Game session with id " + id + " does not exist"));
        existing.setScore(score);
        existing.setCorrectAnswers(correctAnswers);
        existing.setTotalQuestions(totalQuestions);
        existing.setDurationSeconds(durationSeconds);
        return gameRepository.save(existing);
    }
}
