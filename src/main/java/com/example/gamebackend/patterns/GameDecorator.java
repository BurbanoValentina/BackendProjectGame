package com.example.gamebackend.patterns;

import com.example.gamebackend.model.Game;

@SuppressWarnings("unused")
public abstract class GameDecorator extends Game {
    protected final Game decoratedGame;

    public GameDecorator(Game decoratedGame) {
        this.decoratedGame = decoratedGame;
        setId(decoratedGame.getId());
        setPlayerName(decoratedGame.getPlayerName());
        setDifficulty(decoratedGame.getDifficulty());
        setScore(decoratedGame.getScore());
        setCorrectAnswers(decoratedGame.getCorrectAnswers());
        setTotalQuestions(decoratedGame.getTotalQuestions());
        setDurationSeconds(decoratedGame.getDurationSeconds());
        setCreatedAt(decoratedGame.getCreatedAt());
    }

    @Override
    public String getName() {
        return decoratedGame.getName();
    }

    @Override
    public String getGenre() {
        return decoratedGame.getGenre();
    }
}

@SuppressWarnings("unused")
class SpecialEditionGameDecorator extends GameDecorator {
    public SpecialEditionGameDecorator(Game decoratedGame) {
        super(decoratedGame);
    }

    @Override
    public String getName() {
        return decoratedGame.getName() + " - Special Edition";
    }
}
