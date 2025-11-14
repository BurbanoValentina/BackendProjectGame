package com.example.gamebackend.patterns;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.gamebackend.model.Game;

public abstract class AbstractGameFactory {

    private static final Map<String, AbstractGameFactory> FACTORIES = createDefaultFactories();

    public abstract Game createGame(String playerName);

    public static AbstractGameFactory resolve(String difficulty) {
        AbstractGameFactory factory = FACTORIES.get(difficulty);
        return factory != null ? factory : new DefaultGameFactory(difficulty);
    }

    public static void registerFactory(String difficulty, AbstractGameFactory factory) {
        FACTORIES.put(difficulty, factory);
    }

    private static Map<String, AbstractGameFactory> createDefaultFactories() {
        Map<String, AbstractGameFactory> factories = new ConcurrentHashMap<>();
        factories.put("Action", new ActionGameFactory());
        factories.put("Adventure", new AdventureGameFactory());
        return factories;
    }

    private static final class ActionGameFactory extends AbstractGameFactory {
        @Override
        public Game createGame(String playerName) {
            return buildGame(playerName, "Action");
        }
    }

    private static final class AdventureGameFactory extends AbstractGameFactory {
        @Override
        public Game createGame(String playerName) {
            return buildGame(playerName, "Adventure");
        }
    }

    private static final class DefaultGameFactory extends AbstractGameFactory {
        private final String difficulty;

        private DefaultGameFactory(String difficulty) {
            this.difficulty = difficulty;
        }

        @Override
        public Game createGame(String playerName) {
            return buildGame(playerName, difficulty);
        }
    }

    private static Game buildGame(String playerName, String difficulty) {
        return new Game(playerName, difficulty, 0, 0, 0, 0);
    }
}
