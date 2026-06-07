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
import model.Siswa;

public class SiswaController {
    private Siswa map(ResultSet rs) throws SQLException { return new Siswa(rs.getInt("id"), rs.getString("nis"), rs.getString("nama_siswa"), rs.getInt("id_kelas"), rs.getString("nama_kelas"), rs.getString("jenis_kelamin"), rs.getString("alamat")); }
    public List<Siswa> getAll() { return search(""); }
    public Siswa getById(int id) {
        String sql = "SELECT s.*,k.nama_kelas FROM siswa s LEFT JOIN kelas k ON k.id=s.id_kelas WHERE s.id=?";
        try { Connection c=Koneksi.getConnection(); if(c==null)return null; try(PreparedStatement ps=c.prepareStatement(sql)){ ps.setInt(1,id); try(ResultSet rs=ps.executeQuery()){ if(rs.next()) return map(rs); } } }
        catch(SQLException ex){ System.err.println("getById siswa: "+ex.getMessage()); }
        return null;
    }
    public List<Siswa> search(String keyword) {
        List<Siswa> list = new ArrayList<>();
        String sql = "SELECT s.*,k.nama_kelas FROM siswa s LEFT JOIN kelas k ON k.id=s.id_kelas WHERE s.nis LIKE ? OR s.nama_siswa LIKE ? ORDER BY s.nama_siswa";
        try { Connection c=Koneksi.getConnection(); if(c==null)return list; try(PreparedStatement ps=c.prepareStatement(sql)){ String q="%"+keyword+"%"; ps.setString(1,q); ps.setString(2,q); try(ResultSet rs=ps.executeQuery()){ while(rs.next()) list.add(map(rs)); } } }
        catch(SQLException ex){ System.err.println("search siswa: "+ex.getMessage()); }
        return list;
    }
    public boolean save(Siswa s) {
        String sql = s.getId()==0 ? "INSERT INTO siswa(nis,nama_siswa,id_kelas,jenis_kelamin,alamat) VALUES(?,?,?,?,?)" : "UPDATE siswa SET nis=?,nama_siswa=?,id_kelas=?,jenis_kelamin=?,alamat=? WHERE id=?";
        try { Connection c=Koneksi.getConnection(); if(c==null)return false; try(PreparedStatement ps=c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){ ps.setString(1,s.getNis()); ps.setString(2,s.getNamaSiswa()); ps.setInt(3,s.getIdKelas()); ps.setString(4,s.getJenisKelamin()); ps.setString(5,s.getAlamat()); if(s.getId()>0) ps.setInt(6,s.getId()); boolean ok=ps.executeUpdate()>0; if(ok) AuditLogger.log(s.getId()==0?"INSERT":"UPDATE","siswa","",s.getNis()); return ok; } }
        catch(SQLException ex){ System.err.println("save siswa: "+ex.getMessage()); return false; }
    }
    public boolean delete(int id) {
        try { Connection c=Koneksi.getConnection(); if(c==null)return false; try(PreparedStatement ps=c.prepareStatement("DELETE FROM siswa WHERE id=?")){ ps.setInt(1,id); boolean ok=ps.executeUpdate()>0; if(ok) AuditLogger.log("DELETE","siswa",String.valueOf(id),""); return ok; } }
        catch(SQLException ex){ System.err.println("delete siswa: "+ex.getMessage()); return false; }
    }
    public List<Kelas> getAllKelas() { return new KelasController().getAll(); }
}
