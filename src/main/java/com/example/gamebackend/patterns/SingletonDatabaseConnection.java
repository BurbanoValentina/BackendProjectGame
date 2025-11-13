package com.example.gamebackend.patterns;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingletonDatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(SingletonDatabaseConnection.class.getName());
    private static SingletonDatabaseConnection instance;
    private Connection connection;

    private SingletonDatabaseConnection() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:game.db");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "No se pudo crear la conexión SQLite", e);
            throw new IllegalStateException("No se pudo inicializar la conexión a la base de datos", e);
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
