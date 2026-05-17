package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton JDBC connection manager for the RTU Event Registration System.
 * Usage:  Connection conn = DatabaseConnection.getInstance().getConnection();
 */
public class DatabaseConnection {

    // ── Configuration ──────────────────────────────────────────
    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "rtu_event_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";          // change as needed

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
        + "?useSSL=false&serverTimezone=Asia/Manila&allowPublicKeyRetrieval=true";

    // ── Singleton ──────────────────────────────────────────────
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("[DB] Connected to rtu_event_system successfully.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found. Add connector/j to classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database: " + e.getMessage(), e);
        }
    }

    /** Returns the singleton instance, creating it on first call. */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Returns a live connection, re-connecting automatically if the
     * existing one has been closed or timed out.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("[DB] Reconnected to database.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to obtain database connection.", e);
        }
        return connection;
    }

    /** Closes the underlying connection (call on application shutdown). */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}