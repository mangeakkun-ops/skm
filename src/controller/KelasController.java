package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import koneksi.Koneksi;
import model.Kelas;

public class KelasController {
    public List<Kelas> getAll() {
        List<Kelas> list = new ArrayList<>();
        String sql = "SELECT id,kode_kelas,nama_kelas FROM kelas ORDER BY kode_kelas";
        try {
            Connection c = Koneksi.getConnection();
            if (c == null) return list;
            try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Kelas(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException ex) { System.err.println("getAll kelas: " + ex.getMessage()); }
        return list;
    }
    public boolean save(Kelas k) {
        String sql = k.getId() == 0 ? "INSERT INTO kelas(kode_kelas,nama_kelas) VALUES(?,?)" : "UPDATE kelas SET kode_kelas=?, nama_kelas=? WHERE id=?";
        try {
            Connection c = Koneksi.getConnection(); if (c == null) return false;
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, k.getKodeKelas()); ps.setString(2, k.getNamaKelas()); if (k.getId() > 0) ps.setInt(3, k.getId());
                boolean ok = ps.executeUpdate() > 0; if (ok) AuditLogger.log(k.getId()==0?"INSERT":"UPDATE", "kelas", "", k.getKodeKelas()); return ok;
            }
        } catch (SQLException ex) { System.err.println("save kelas: " + ex.getMessage()); return false; }
    }
    public boolean delete(int id) {
        try {
            Connection c = Koneksi.getConnection(); if (c == null) return false;
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM kelas WHERE id=?")) { ps.setInt(1,id); boolean ok=ps.executeUpdate()>0; if(ok) AuditLogger.log("DELETE","kelas",String.valueOf(id),""); return ok; }
        } catch (SQLException ex) { System.err.println("delete kelas: " + ex.getMessage()); return false; }
    }
}
