package database;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import models.Event;

/**
 * Data Access Object for the `events` table.
 */
public class EventDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ── Create ─────────────────────────────────────────────────

    public int insert(Event e) {
        String sql = """
            INSERT INTO events
              (category_id, title, description, venue, event_date, event_time,
               organizer, max_participants, registration_open,
               registration_deadline, created_by)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,       e.getCategoryId());
            ps.setString(2,    e.getTitle());
            ps.setString(3,    e.getDescription());
            ps.setString(4,    e.getVenue());
            ps.setDate(5,      e.getEventDate());
            ps.setTime(6,      e.getEventTime());
            ps.setString(7,    e.getOrganizer());
            ps.setInt(8,       e.getMaxParticipants());
            ps.setBoolean(9,   e.isRegistrationOpen());
            ps.setTimestamp(10, e.getRegistrationDeadline());
            ps.setInt(11,      e.getCreatedBy());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException ex) {
            System.err.println("[EventDAO.insert] " + ex.getMessage());
        }
        return -1;
    }

    // ── Read ───────────────────────────────────────────────────

    public Event findById(int eventId) {
        String sql = """
            SELECT e.*, c.name AS category_name
            FROM events e
            JOIN event_categories c ON e.category_id = c.category_id
            WHERE e.event_id = ?
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException ex) {
            System.err.println("[EventDAO.findById] " + ex.getMessage());
        }
        return null;
    }

    /** Upcoming active events (today onward) */
    public List<Event> findUpcoming() {
        List<Event> list = new ArrayList<>();
        String sql = """
            SELECT e.*, c.name AS category_name
            FROM events e
            JOIN event_categories c ON e.category_id = c.category_id
            WHERE e.is_active = TRUE AND e.event_date >= CURDATE()
            ORDER BY e.event_date, e.event_time
            """;
        return executeQuery(sql, list);
    }

    public List<Event> findAll() {
        List<Event> list = new ArrayList<>();
        String sql = """
            SELECT e.*, c.name AS category_name
            FROM events e
            JOIN event_categories c ON e.category_id = c.category_id
            ORDER BY e.event_date DESC
            """;
        return executeQuery(sql, list);
    }

    public List<Event> searchByTitle(String keyword) {
        List<Event> list = new ArrayList<>();
        String sql = """
            SELECT e.*, c.name AS category_name
            FROM events e
            JOIN event_categories c ON e.category_id = c.category_id
            WHERE e.is_active = TRUE AND e.title LIKE ?
            ORDER BY e.event_date
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException ex) {
            System.err.println("[EventDAO.searchByTitle] " + ex.getMessage());
        }
        return list;
    }

    public List<Event> findByCategory(int categoryId) {
        List<Event> list = new ArrayList<>();
        String sql = """
            SELECT e.*, c.name AS category_name
            FROM events e
            JOIN event_categories c ON e.category_id = c.category_id
            WHERE e.is_active = TRUE AND e.category_id = ?
            ORDER BY e.event_date
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException ex) {
            System.err.println("[EventDAO.findByCategory] " + ex.getMessage());
        }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM events WHERE is_active = TRUE";
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {
            System.err.println("[EventDAO.countAll] " + ex.getMessage());
        }
        return 0;
    }

    // ── Update ─────────────────────────────────────────────────

    public boolean update(Event e) {
        String sql = """
            UPDATE events SET category_id=?, title=?, description=?, venue=?,
            event_date=?, event_time=?, organizer=?, max_participants=?,
            registration_open=?, registration_deadline=?
            WHERE event_id=?
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1,       e.getCategoryId());
            ps.setString(2,    e.getTitle());
            ps.setString(3,    e.getDescription());
            ps.setString(4,    e.getVenue());
            ps.setDate(5,      e.getEventDate());
            ps.setTime(6,      e.getEventTime());
            ps.setString(7,    e.getOrganizer());
            ps.setInt(8,       e.getMaxParticipants());
            ps.setBoolean(9,   e.isRegistrationOpen());
            ps.setTimestamp(10, e.getRegistrationDeadline());
            ps.setInt(11,      e.getEventId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[EventDAO.update] " + ex.getMessage());
        }
        return false;
    }

    public boolean softDelete(int eventId) {
        String sql = "UPDATE events SET is_active = FALSE WHERE event_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, eventId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("[EventDAO.softDelete] " + ex.getMessage());
        }
        return false;
    }

    /** Increments registered_count by 1; also auto-closes when full. */
    public void incrementRegisteredCount(int eventId) {
        String sql = """
            UPDATE events
            SET registered_count = registered_count + 1,
                registration_open = IF(registered_count + 1 >= max_participants, FALSE, registration_open)
            WHERE event_id = ?
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("[EventDAO.incrementRegisteredCount] " + ex.getMessage());
        }
    }

    public void decrementRegisteredCount(int eventId) {
        String sql = "UPDATE events SET registered_count = registered_count - 1 WHERE event_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("[EventDAO.decrementRegisteredCount] " + ex.getMessage());
        }
    }

    // ── Helpers ────────────────────────────────────────────────

    private List<Event> executeQuery(String sql, List<Event> list) {
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException ex) {
            System.err.println("[EventDAO.executeQuery] " + ex.getMessage());
        }
        return list;
    }

    private Event mapRow(ResultSet rs) throws SQLException {
        Event e = new Event();
        e.setEventId(rs.getInt("event_id"));
        e.setCategoryId(rs.getInt("category_id"));
        e.setCategoryName(rs.getString("category_name"));
        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        e.setVenue(rs.getString("venue"));
        e.setEventDate(rs.getDate("event_date"));
        e.setEventTime(rs.getTime("event_time"));
        e.setOrganizer(rs.getString("organizer"));
        e.setMaxParticipants(rs.getInt("max_participants"));
        e.setRegisteredCount(rs.getInt("registered_count"));
        e.setRegistrationOpen(rs.getBoolean("registration_open"));
        e.setRegistrationDeadline(rs.getTimestamp("registration_deadline"));
        e.setActive(rs.getBoolean("is_active"));
        e.setCreatedBy(rs.getInt("created_by"));
        e.setCreatedAt(rs.getTimestamp("created_at"));
        return e;
    }
}