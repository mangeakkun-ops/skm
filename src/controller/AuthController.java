package controller;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import koneksi.Koneksi;
import model.User;

public class AuthController {
    private static User currentUser;

    public User login(String username, String password) {
        String sql = "SELECT id,nama,username,password,role FROM users WHERE username=?";
        try {
            Connection c = Koneksi.getConnection();
            if (c == null) return null;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String hash = rs.getString("password");
                        if (checkPassword(password, hash)) {
                            currentUser = new User(rs.getInt("id"), rs.getString("nama"), rs.getString("username"), hash, rs.getString("role"));
                            AuditLogger.log("LOGIN", "users", "", currentUser.getUsername());
                            return currentUser;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Login gagal: " + ex.getMessage());
        }
        return null;
    }

    public static User getCurrentUser() { return currentUser; }
    public void logout() { AuditLogger.log("LOGOUT", "users", currentUser == null ? "" : currentUser.getUsername(), ""); currentUser = null; }

    public static String hashPassword(String password) {
        try {
            Class<?> bcrypt = Class.forName("org.mindrot.jbcrypt.BCrypt");
            Method gensalt = bcrypt.getMethod("gensalt");
            Method hashpw = bcrypt.getMethod("hashpw", String.class, String.class);
            return (String) hashpw.invoke(null, password, (String) gensalt.invoke(null));
        } catch (ReflectiveOperationException ex) {
            return "{plain}" + password;
        }
    }

    private boolean checkPassword(String password, String stored) {
        if (stored == null) return false;
        if (stored.startsWith("{plain}")) return stored.substring(7).equals(password);
        try {
            Class<?> bcrypt = Class.forName("org.mindrot.jbcrypt.BCrypt");
            Method checkpw = bcrypt.getMethod("checkpw", String.class, String.class);
            return (Boolean) checkpw.invoke(null, password, stored);
        } catch (ReflectiveOperationException ex) {
            return stored.equals(password);
        }
    }
}
