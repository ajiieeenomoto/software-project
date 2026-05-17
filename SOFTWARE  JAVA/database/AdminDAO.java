// ════════════════════════════════════════════════════════════════
// FILE: src/database/AdminDAO.java
// ════════════════════════════════════════════════════════════════
package database;

import models.Admin;
import utils.PasswordUtil;

import java.sql.*;

public class AdminDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public Admin authenticate(String username, String plainPassword) {
        String sql = "SELECT * FROM admins WHERE username = ? AND is_active = TRUE";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && PasswordUtil.verify(plainPassword, rs.getString("password_hash"))) {
                Admin a = new Admin();
                a.setAdminId(rs.getInt("admin_id"));
                a.setUsername(rs.getString("username"));
                a.setPasswordHash(rs.getString("password_hash"));
                a.setFullName(rs.getString("full_name"));
                a.setEmail(rs.getString("email"));
                a.setActive(rs.getBoolean("is_active"));
                a.setCreatedAt(rs.getTimestamp("created_at"));
                return a;
            }
        } catch (SQLException e) {
            System.err.println("[AdminDAO.authenticate] " + e.getMessage());
        }
        return null;
    }
}