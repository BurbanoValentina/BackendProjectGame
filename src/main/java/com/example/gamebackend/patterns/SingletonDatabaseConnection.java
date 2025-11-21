package com.example.gamebackend.patterns;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton Pattern: keeps a single SQLite connection for legacy imports.
 */
public class SingletonDatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(SingletonDatabaseConnection.class.getName());
    private static SingletonDatabaseConnection instance;
    private Connection connection;

    private SingletonDatabaseConnection() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:game.db");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Unable to create the SQLite connection", e);
            throw new IllegalStateException("Failed to initialize the database connection", e);
        }
    }

    public static SingletonDatabaseConnection getInstance() {
        if (instance == null) {
            instance = new SingletonDatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
