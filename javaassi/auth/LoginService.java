package com.example.javaassi.auth;

import java.sql.*;
import com.example.javaassi.db.Database;

public class LoginService {

    public static String login(String username, String password) {
        try (Connection conn = Database.getConnection()) {

            String sql = "SELECT r.role FROM employees e " +
                    "LEFT JOIN roles r ON e.id = r.employee_id " +
                    "WHERE e.id = ? AND e.password = ? AND e.is_active = 1";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role") != null ? rs.getString("role") : "Employee";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
