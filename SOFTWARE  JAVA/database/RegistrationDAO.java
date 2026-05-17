package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import models.Registration;
import models.Registration.Status;

/**
 * Data Access Object for `registrations` and `attendance` tables.
 */
public class RegistrationDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ── Registrations ──────────────────────────────────────────

    /**
     * Registers a student for an event.
     * Generates a unique QR token and increments the event counter.
     * Returns the new registration_id, or -1 on failure.
     */
    public int register(int eventId, int studentId) {
        if (isAlreadyRegistered(eventId, studentId)) return -2; // duplicate

        String qrToken = UUID.randomUUID().toString().toUpperCase();
        String sql = """
            INSERT INTO registrations (event_id, student_id, qr_code_data)
            VALUES (?, ?, ?)
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,    eventId);
            ps.setInt(2,    studentId);
            ps.setString(3, qrToken);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int regId = keys.getInt(1);
                new EventDAO().incrementRegisteredCount(eventId);
                sendConfirmationNotification(eventId, studentId);
                return regId;
            }
        } catch (SQLException e) {
            System.err.println("[RegistrationDAO.register] " + e.getMessage());
        }
        return -1;
    }

    public boolean cancel(int registrationId, int studentId) {
        String sql = """
            UPDATE registrations
            SET status = 'cancelled', cancelled_at = NOW()
            WHERE registration_id = ? AND student_id = ?
              AND status IN ('approved','pending')
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, registrationId);
            ps.setInt(2, studentId);
            if (ps.executeUpdate() > 0) {
                // Retrieve event_id, then decrement
                Registration r = findById(registrationId);
                if (r != null) new EventDAO().decrementRegisteredCount(r.getEventId());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[RegistrationDAO.cancel] " + e.getMessage());
        }
        return false;
    }

    public boolean isAlreadyRegistered(int eventId, int studentId) {
        String sql = """
            SELECT 1 FROM registrations
            WHERE event_id = ? AND student_id = ? AND status != 'cancelled'
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, studentId);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            System.err.println("[RegistrationDAO.isAlreadyRegistered] " + e.getMessage());
        }
        return false;
    }

    public Registration findById(int registrationId) {
        String sql = """
            SELECT r.*,
                   CONCAT(s.first_name,' ',s.last_name) AS student_name,
                   s.student_number,
                   e.title AS event_title
            FROM registrations r
            JOIN students s ON r.student_id = s.student_id
            JOIN events   e ON r.event_id   = e.event_id
            WHERE r.registration_id = ?
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, registrationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[RegistrationDAO.findById] " + e.getMessage());
        }
        return null;
    }

    /** Find by QR token — used during attendance verification. */
    public Registration findByQrToken(String qrToken) {
        String sql = """
            SELECT r.*,
                   CONCAT(s.first_name,' ',s.last_name) AS student_name,
                   s.student_number,
                   e.title AS event_title
            FROM registrations r
            JOIN students s ON r.student_id = s.student_id
            JOIN events   e ON r.event_id   = e.event_id
            WHERE r.qr_code_data = ? AND r.status = 'approved'
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, qrToken);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[RegistrationDAO.findByQrToken] " + e.getMessage());
        }
        return null;
    }

    public List<Registration> findByStudent(int studentId) {
        List<Registration> list = new ArrayList<>();
        String sql = """
            SELECT r.*,
                   CONCAT(s.first_name,' ',s.last_name) AS student_name,
                   s.student_number,
                   e.title AS event_title
            FROM registrations r
            JOIN students s ON r.student_id = s.student_id
            JOIN events   e ON r.event_id   = e.event_id
            WHERE r.student_id = ?
            ORDER BY r.registered_at DESC
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[RegistrationDAO.findByStudent] " + e.getMessage());
        }
        return list;
    }

    public List<Registration> findByEvent(int eventId) {
        List<Registration> list = new ArrayList<>();
        String sql = """
            SELECT r.*,
                   CONCAT(s.first_name,' ',s.last_name) AS student_name,
                   s.student_number,
                   e.title AS event_title
            FROM registrations r
            JOIN students s ON r.student_id = s.student_id
            JOIN events   e ON r.event_id   = e.event_id
            WHERE r.event_id = ? AND r.status != 'cancelled'
            ORDER BY s.last_name
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[RegistrationDAO.findByEvent] " + e.getMessage());
        }
        return list;
    }

    public int countAllRegistrations() {
        String sql = "SELECT COUNT(*) FROM registrations WHERE status != 'cancelled'";
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[RegistrationDAO.countAll] " + e.getMessage());
        }
        return 0;
    }

    // ── Attendance ─────────────────────────────────────────────

    /**
     * Marks a student as attended.
     * method: "qr_scan" or "manual"
     * markedBy: admin_id (0 = QR self-scan, set to NULL)
     */
    public boolean markAttendance(int registrationId, int eventId, int studentId,
                                  String method, int markedBy) {
        String sql = """
            INSERT IGNORE INTO attendance
              (registration_id, event_id, student_id, method, marked_by)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, registrationId);
            ps.setInt(2, eventId);
            ps.setInt(3, studentId);
            ps.setString(4, method);
            if (markedBy > 0) ps.setInt(5, markedBy);
            else              ps.setNull(5, Types.INTEGER);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[RegistrationDAO.markAttendance] " + e.getMessage());
        }
        return false;
    }

    public boolean hasAttended(int registrationId) {
        String sql = "SELECT 1 FROM attendance WHERE registration_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, registrationId);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            System.err.println("[RegistrationDAO.hasAttended] " + e.getMessage());
        }
        return false;
    }

    public int countAttendanceForEvent(int eventId) {
        String sql = "SELECT COUNT(*) FROM attendance WHERE event_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[RegistrationDAO.countAttendance] " + e.getMessage());
        }
        return 0;
    }

    public int countTotalAttendance() {
        String sql = "SELECT COUNT(*) FROM attendance";
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[RegistrationDAO.countTotalAttendance] " + e.getMessage());
        }
        return 0;
    }

    // ── Notifications ──────────────────────────────────────────

    private void sendConfirmationNotification(int eventId, int studentId) {
        String sql = """
            INSERT INTO notifications (student_id, event_id, title, message)
            SELECT ?, e.event_id,
                   CONCAT('Registration Confirmed: ', e.title),
                   CONCAT('You have successfully registered for ', e.title,
                          ' on ', DATE_FORMAT(e.event_date,'%M %d, %Y'), '.')
            FROM events e WHERE e.event_id = ?
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, eventId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[RegistrationDAO.sendConfirmation] " + e.getMessage());
        }
    }

    // ── Mapping ────────────────────────────────────────────────

    private Registration mapRow(ResultSet rs) throws SQLException {
        Registration r = new Registration();
        r.setRegistrationId(rs.getInt("registration_id"));
        r.setEventId(rs.getInt("event_id"));
        r.setStudentId(rs.getInt("student_id"));
        r.setStatus(Status.valueOf(rs.getString("status")));
        r.setQrCodeData(rs.getString("qr_code_data"));
        r.setRegisteredAt(rs.getTimestamp("registered_at"));
        r.setCancelledAt(rs.getTimestamp("cancelled_at"));
        r.setStudentName(rs.getString("student_name"));
        r.setStudentNumber(rs.getString("student_number"));
        r.setEventTitle(rs.getString("event_title"));
        return r;
    }
}