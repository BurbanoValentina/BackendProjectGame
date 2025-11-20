package com.example.gamebackend.migration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Locale;

/**
 * Entry point to run the SQLite -> MongoDB migration from the command line.
 */
public final class SqliteToMongoMigrationApp {

    private static final String DEFAULT_SQLITE_PATH = "game.db";
    private static final String DEFAULT_MONGO_URI = "mongodb://localhost:27017";
    private static final String DEFAULT_DATABASE = "game";

    private SqliteToMongoMigrationApp() {
    }

    public static void main(String[] args) throws SQLException {
        MigrationOptions options = MigrationOptions.fromSystemProperties();
        System.out.printf(Locale.ROOT,
                "SQLITE=%s | MONGO_URI=%s | DATABASE=%s | DROP_COLLECTIONS=%s%n",
                options.sqlitePath(), options.mongoUri(), options.databaseName(), options.dropCollections());
        new SqliteToMongoMigrator(options.sqlitePath(), options.mongoUri(), options.databaseName(), options.dropCollections())
                .migrate();
    }

    private record MigrationOptions(Path sqlitePath, String mongoUri, String databaseName, boolean dropCollections) {

        static MigrationOptions fromSystemProperties() {
            String sqlitePathProp = firstNonBlank(System.getProperty("sqlite.path"),
                    System.getenv("SQLITE_PATH"), DEFAULT_SQLITE_PATH);
            String mongoUriProp = firstNonBlank(System.getProperty("mongo.uri"),
                    System.getenv("MONGO_URI"), DEFAULT_MONGO_URI);
            String databaseProp = firstNonBlank(System.getProperty("mongo.database"),
                    System.getenv("MONGO_DATABASE"), DEFAULT_DATABASE);
            boolean dropCollections = Boolean.parseBoolean(firstNonBlank(System.getProperty("mongo.dropCollections"),
                    System.getenv("MONGO_DROP_COLLECTIONS"), "true"));
            Path sqlitePath = Paths.get(sqlitePathProp);
            return new MigrationOptions(sqlitePath, mongoUriProp, databaseProp, dropCollections);
        }

        private static String firstNonBlank(String... candidates) {
            for (String candidate : candidates) {
                if (candidate != null && !candidate.isBlank()) {
                    return candidate;
                }
            }
            return "";
        }
    }
}
