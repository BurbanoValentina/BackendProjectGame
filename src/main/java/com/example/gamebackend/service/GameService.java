package com.example.gamebackend.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.gamebackend.model.Game;
import com.example.gamebackend.patterns.GameBuilder;
import com.example.gamebackend.patterns.GameFactory;
import com.example.gamebackend.repository.GameRepository;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Game getGameById(Integer id) {
        Integer safeId = Objects.requireNonNull(id, "El id no puede ser nulo");
        return gameRepository.findById(safeId).orElse(null);
    }

    public Game createGameSession(String playerName, String difficulty) {
        Game prototype = GameFactory.createGame(playerName, difficulty);
        GameBuilder builder = new GameBuilder()
            .setName(prototype.getPlayerName())
            .setGenre(prototype.getDifficulty())
            .setScore(prototype.getScore())
            .setCorrectAnswers(prototype.getCorrectAnswers())
            .setTotalQuestions(prototype.getTotalQuestions())
            .setDurationSeconds(prototype.getDurationSeconds());
        Game builtGame = Objects.requireNonNull(builder.build(), "El builder no puede producir un juego nulo");
        return gameRepository.save(builtGame);
    }

    public Game addGame(Game game) {
        Game safeGame = Objects.requireNonNull(game, "El juego es obligatorio");
        return gameRepository.save(safeGame);
    }

    public Game updateGameProgress(Integer id, int score, int correctAnswers, int totalQuestions, long durationSeconds) {
        Integer safeId = Objects.requireNonNull(id, "El id no puede ser nulo");
        Game existing = gameRepository.findById(safeId)
            .orElseThrow(() -> new IllegalArgumentException("La partida con id " + id + " no existe."));
        existing.setScore(score);
        existing.setCorrectAnswers(correctAnswers);
        existing.setTotalQuestions(totalQuestions);
        existing.setDurationSeconds(durationSeconds);
        return gameRepository.save(existing);
    }
}
