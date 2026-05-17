package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Student;
import utils.PasswordUtil;

/**
 * Data Access Object for the `students` table.
 * All queries use PreparedStatement to prevent SQL injection.
 */
public class StudentDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ── Authentication ─────────────────────────────────────────

    /**
     * Authenticates a student by student number and plain password.
     * Returns the Student object on success, null on failure.
     */
    public Student authenticate(String studentNumber, String plainPassword) {
        String sql = "SELECT * FROM students WHERE student_number = ? AND is_active = TRUE";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, studentNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                if (PasswordUtil.verify(plainPassword, hash)) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[StudentDAO.authenticate] " + e.getMessage());
        }
        return null;
    }

    // ── Create ─────────────────────────────────────────────────

    /**
     * Inserts a new student. Password is hashed before storage.
     * Returns the generated student_id, or -1 on failure.
     */
    public int insert(Student s, String plainPassword) {
        String sql = """
            INSERT INTO students
              (student_number, password_hash, first_name, last_name,
               email, course, year_level, section, contact_number)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getStudentNumber());
            ps.setString(2, PasswordUtil.hash(plainPassword));
            ps.setString(3, s.getFirstName());
            ps.setString(4, s.getLastName());
            ps.setString(5, s.getEmail());
            ps.setString(6, s.getCourse());
            ps.setInt(7,    s.getYearLevel());
            ps.setString(8, s.getSection());
            ps.setString(9, s.getContactNumber());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("[StudentDAO.insert] " + e.getMessage());
        }
        return -1;
    }

    // ── Read ───────────────────────────────────────────────────

    public Student findById(int studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[StudentDAO.findById] " + e.getMessage());
        }
        return null;
    }

    public Student findByStudentNumber(String studentNumber) {
        String sql = "SELECT * FROM students WHERE student_number = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, studentNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[StudentDAO.findByStudentNumber] " + e.getMessage());
        }
        return null;
    }

    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE is_active = TRUE ORDER BY last_name";
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[StudentDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM students WHERE is_active = TRUE";
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[StudentDAO.countAll] " + e.getMessage());
        }
        return 0;
    }

    // ── Update ─────────────────────────────────────────────────

    public boolean update(Student s) {
        String sql = """
            UPDATE students SET first_name=?, last_name=?, email=?,
            course=?, year_level=?, section=?, contact_number=?
            WHERE student_id=?
            """;
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getCourse());
            ps.setInt(5,    s.getYearLevel());
            ps.setString(6, s.getSection());
            ps.setString(7, s.getContactNumber());
            ps.setInt(8,    s.getStudentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[StudentDAO.update] " + e.getMessage());
        }
        return false;
    }

    public boolean updatePassword(int studentId, String newPlainPassword) {
        String sql = "UPDATE students SET password_hash=? WHERE student_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hash(newPlainPassword));
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[StudentDAO.updatePassword] " + e.getMessage());
        }
        return false;
    }

    public boolean isStudentNumberTaken(String studentNumber) {
        String sql = "SELECT 1 FROM students WHERE student_number = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, studentNumber);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            System.err.println("[StudentDAO.isStudentNumberTaken] " + e.getMessage());
        }
        return false;
    }

    public boolean isEmailTaken(String email) {
        String sql = "SELECT 1 FROM students WHERE email = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            System.err.println("[StudentDAO.isEmailTaken] " + e.getMessage());
        }
        return false;
    }

    // ── Mapping ────────────────────────────────────────────────

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setStudentNumber(rs.getString("student_number"));
        s.setPasswordHash(rs.getString("password_hash"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setEmail(rs.getString("email"));
        s.setCourse(rs.getString("course"));
        s.setYearLevel(rs.getInt("year_level"));
        s.setSection(rs.getString("section"));
        s.setContactNumber(rs.getString("contact_number"));
        s.setActive(rs.getBoolean("is_active"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));
        return s;
    }
}