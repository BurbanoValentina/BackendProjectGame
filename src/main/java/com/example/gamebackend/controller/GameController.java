package com.example.gamebackend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gamebackend.dto.GameStartRequest;
import com.example.gamebackend.dto.GameUpdateRequest;
import com.example.gamebackend.model.Game;
import com.example.gamebackend.patterns.GameFacade;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameFacade gameFacade;

    public GameController(GameFacade gameFacade) {
        this.gameFacade = gameFacade;
    }

    @GetMapping
    public List<Game> getAllGames() {
        return gameFacade.getLeaderboard();
    }

    @GetMapping("/{id}")
    public Game getGameById(@PathVariable String id) {
        return gameFacade.findGame(id);
    }

    @PostMapping("/start")
    public Game startGame(@Valid @RequestBody GameStartRequest request) {
        return gameFacade.startGameSession(request.getPlayerName(), request.getDifficulty());
    }

    @PutMapping("/{id}")
    public Game updateGame(@PathVariable String id, @Valid @RequestBody GameUpdateRequest request) {
        return gameFacade.updateGameSession(id, request.getScore(), request.getCorrectAnswers(),
            request.getTotalQuestions(), request.getDurationSeconds());
    }

    @PostMapping
    public Game addGame(@Valid @RequestBody Game game) {
        Game created = gameFacade.startGameSession(game.getPlayerName(), game.getDifficulty());
        return gameFacade.updateGameSession(created.getId(), game.getScore(), game.getCorrectAnswers(),
            game.getTotalQuestions(), game.getDurationSeconds());
    }
}

