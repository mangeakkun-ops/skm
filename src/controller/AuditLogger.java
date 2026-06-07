package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import koneksi.Koneksi;
import model.User;

public class AuditLogger {
    public static void log(String aksi, String tabel, String dataLama, String dataBaru) {
        String sql = "INSERT INTO audit_log(user_id, aksi, tabel_target, data_lama, data_baru, waktu) VALUES(?,?,?,?,?,NOW())";
        User u = AuthController.getCurrentUser();
        try {
            Connection c = Koneksi.getConnection();
            if (c == null) return;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                if (u == null) ps.setNull(1, java.sql.Types.INTEGER); else ps.setInt(1, u.getId());
                ps.setString(2, aksi);
                ps.setString(3, tabel);
                ps.setString(4, dataLama);
                ps.setString(5, dataBaru);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            System.err.println("Audit log gagal: " + ex.getMessage());
        }
    }
}
