package com.example.gamebackend.migration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility that copies every SQLite table into MongoDB collections with the same name.
 */
public class SqliteToMongoMigrator {

    private static final int INSERT_BATCH_SIZE = 1_000;

    private final Path sqlitePath;
    private final String mongoUri;
    private final String mongoDatabaseName;
    private final boolean dropCollections;

    public SqliteToMongoMigrator(Path sqlitePath, String mongoUri, String mongoDatabaseName, boolean dropCollections) {
        this.sqlitePath = sqlitePath.toAbsolutePath();
        this.mongoUri = mongoUri;
        this.mongoDatabaseName = mongoDatabaseName;
        this.dropCollections = dropCollections;
    }

    public void migrate() throws SQLException {
        if (!Files.exists(sqlitePath)) {
            throw new IllegalStateException("No se encontró el archivo SQLite en " + sqlitePath);
        }
        String jdbcUrl = "jdbc:sqlite:" + sqlitePath;
        System.out.printf(Locale.ROOT, "Conectando a SQLite (%s)%n", jdbcUrl);
        try (Connection sqliteConnection = DriverManager.getConnection(jdbcUrl);
             MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoDatabaseName);
            List<String> tableNames = fetchTableNames(sqliteConnection);
            if (tableNames.isEmpty()) {
                System.out.println("SQLite no tiene tablas migrables.");
                return;
            }
            System.out.printf(Locale.ROOT, "Iniciando migración de %d tablas hacia MongoDB (%s)%n",
                    tableNames.size(), mongoDatabaseName);
            Set<String> existingCollections = fetchCollectionNames(mongoDatabase);
            for (String tableName : tableNames) {
                MongoCollection<Document> collection = mongoDatabase.getCollection(tableName);
                if (dropCollections && existingCollections.contains(tableName)) {
                    System.out.printf(Locale.ROOT, "Eliminando colección existente %s%n", tableName);
                    collection.drop();
                }
                migrateTable(sqliteConnection, collection, tableName);
            }
        }
    }

    private List<String> fetchTableNames(Connection sqliteConnection) throws SQLException {
        DatabaseMetaData metaData = sqliteConnection.getMetaData();
        List<String> tables = new ArrayList<>();
        try (ResultSet resultSet = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                if (tableName == null) {
                    continue;
                }
                String normalized = tableName.toLowerCase(Locale.ROOT);
                if (normalized.startsWith("sqlite_")) {
                    continue; // Skip SQLite internal tables
                }
                tables.add(tableName);
            }
        }
        return tables;
    }

    private Set<String> fetchCollectionNames(MongoDatabase database) {
        return database.listCollectionNames().into(new ArrayList<>())
                .stream()
                .collect(Collectors.toCollection(HashSet::new));
    }

    private void migrateTable(Connection sqliteConnection, MongoCollection<Document> collection, String tableName)
            throws SQLException {
        List<Document> batch = new ArrayList<>(INSERT_BATCH_SIZE);
        int migratedRows = 0;
        try (Statement statement = sqliteConnection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Document document = new Document();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    String columnName = metaData.getColumnLabel(columnIndex);
                    Object value = resolveValue(resultSet, metaData, columnIndex);
                    document.append(columnName, value);
                }
                batch.add(document);
                migratedRows++;
                if (batch.size() >= INSERT_BATCH_SIZE) {
                    collection.insertMany(batch);
                    batch.clear();
                }
            }
        }
        if (!batch.isEmpty()) {
            collection.insertMany(batch);
        }
        System.out.printf(Locale.ROOT, "Tabla %s migrada (%d filas).%n", tableName, migratedRows);
    }

    private Object resolveValue(ResultSet resultSet, ResultSetMetaData metaData, int columnIndex) throws SQLException {
        int columnType = metaData.getColumnType(columnIndex);
        Object value;
        switch (columnType) {
            case Types.BOOLEAN:
            case Types.BIT:
                value = resultSet.getBoolean(columnIndex);
                break;
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.BIGINT:
                value = resultSet.getLong(columnIndex);
                break;
            case Types.FLOAT:
            case Types.REAL:
            case Types.DOUBLE:
                value = resultSet.getDouble(columnIndex);
                break;
            case Types.DECIMAL:
            case Types.NUMERIC:
                value = resultSet.getBigDecimal(columnIndex);
                break;
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                value = resultSet.getTimestamp(columnIndex);
                break;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.BLOB:
                value = resultSet.getBytes(columnIndex);
                break;
            default:
                value = resultSet.getString(columnIndex);
        }
        if (resultSet.wasNull()) {
            return null;
        }
        return value;
    }
}
