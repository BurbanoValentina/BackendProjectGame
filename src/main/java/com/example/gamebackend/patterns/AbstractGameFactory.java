package com.example.gamebackend.patterns;

import com.example.gamebackend.model.Game;

@SuppressWarnings("unused")
public abstract class AbstractGameFactory {
    public abstract Game createGame();
}

@SuppressWarnings("unused")
class ActionGameFactory extends AbstractGameFactory {
    @Override
    public Game createGame() {
        return new Game(0, "Action Game", "Action");
    }
}

@SuppressWarnings("unused")
class AdventureGameFactory extends AbstractGameFactory {
    @Override
    public Game createGame() {
        return new Game(0, "Adventure Game", "Adventure");
    }
}
