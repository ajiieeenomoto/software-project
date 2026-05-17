package database;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object for the `notifications` table.
 */
public class NotificationDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /** Returns unread notifications for a student, newest first. */
    public List<Map<String, Object>> getUnreadForStudent(int studentId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
            SELECT n.*, e.title AS event_title
            FROM notifications n
            LEFT JOIN events e ON n.event_id = e.event_id
            WHERE (n.student_id = ? OR n.student_id IS NULL)
              AND n.is_read = FALSE
            ORDER BY n.sent_at DESC
            LIMIT 20
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id",    rs.getInt("notification_id"));
                row.put("title", rs.getString("title"));
                row.put("msg",   rs.getString("message"));
                row.put("date",  rs.getTimestamp("sent_at"));
                list.add(row);
            }
        } catch (SQLException e) {
            System.err.println("[NotificationDAO.getUnread] " + e.getMessage());
        }
        return list;
    }

    public int countUnread(int studentId) {
        String sql = """
            SELECT COUNT(*) FROM notifications
            WHERE (student_id = ? OR student_id IS NULL) AND is_read = FALSE
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[NotificationDAO.countUnread] " + e.getMessage());
        }
        return 0;
    }

    public boolean markAllRead(int studentId) {
        String sql = """
            UPDATE notifications SET is_read = TRUE
            WHERE student_id = ? OR student_id IS NULL
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            System.err.println("[NotificationDAO.markAllRead] " + e.getMessage());
        }
        return false;
    }

    /** Sends an announcement to all students (student_id NULL = broadcast). */
    public boolean broadcast(String title, String message, Integer eventId) {
        String sql = "INSERT INTO notifications (event_id, title, message) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            if (eventId != null) ps.setInt(1, eventId);
            else                 ps.setNull(1, Types.INTEGER);
            ps.setString(2, title);
            ps.setString(3, message);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NotificationDAO.broadcast] " + e.getMessage());
        }
        return false;
    }
}