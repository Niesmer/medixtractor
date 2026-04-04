package AHA.medixtractor.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.FileSystemException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class DatabaseBootstrap {

    private static final DateTimeFormatter BACKUP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private static final Map<String, Set<String>> EXPECTED_COLUMNS = Map.of(
        "medicament", Set.of("cis", "nom", "forme", "voie", "statut", "procedure", "commercialisation", "date_amm", "laboratoire"),
        "presentation", Set.of("cip", "cis", "prix", "remboursement"),
        "composition", Set.of("id", "cis", "substance", "dosage", "unite")
    );

    private DatabaseBootstrap() {
    }

    public static void ensureCompatibleSqliteSchema(Path databasePath) {
        if (!Files.exists(databasePath)) {
            return;
        }

        try {
            if (isCompatible(databasePath)) {
                return;
            }
            backupAndDelete(databasePath);
        } catch (FileSystemException exception) {
            throw new IllegalStateException(
                "La base SQLite actuelle est verrouillee par un autre processus. " +
                "Ferme l'autre application Java qui utilise database/bdpm.db puis relance Medixtractor.",
                exception
            );
        } catch (SQLException exception) {
            throw new IllegalStateException("Verification du schema SQLite impossible.", exception);
        } catch (IOException exception) {
            throw new UncheckedIOException("Impossible de sauvegarder l'ancienne base SQLite.", exception);
        }
    }

    private static boolean isCompatible(Path databasePath) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath.toAbsolutePath());
             Statement statement = connection.createStatement()) {
            for (Map.Entry<String, Set<String>> entry : EXPECTED_COLUMNS.entrySet()) {
                Set<String> actualColumns = getColumns(statement, entry.getKey());
                if (!actualColumns.containsAll(entry.getValue())) {
                    return false;
                }
            }
            return true;
        }
    }

    private static Set<String> getColumns(Statement statement, String tableName) throws SQLException {
        Set<String> columns = new LinkedHashSet<>();
        try (ResultSet resultSet = statement.executeQuery("PRAGMA table_info(" + tableName + ")")) {
            while (resultSet.next()) {
                columns.add(resultSet.getString("name"));
            }
        }
        return columns;
    }

    private static void backupAndDelete(Path databasePath) throws IOException {
        Path backupPath = databasePath.resolveSibling(
            databasePath.getFileName().toString().replace(".db", "") + "-backup-" + BACKUP_FORMAT.format(LocalDateTime.now()) + ".db"
        );
        Files.move(databasePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
