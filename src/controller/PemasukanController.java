package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import koneksi.Koneksi;
import model.Pemasukan;
import util.UIUtils;

public class PemasukanController {
    private Pemasukan map(ResultSet rs) throws SQLException { return new Pemasukan(rs.getInt("id"), rs.getDate("tanggal").toLocalDate(), (Integer) rs.getObject("id_siswa"), rs.getString("nama_siswa"), rs.getInt("id_kategori"), rs.getString("nama_kategori"), rs.getDouble("nominal"), rs.getString("keterangan"), rs.getInt("created_by")); }
    public List<Pemasukan> getAll() { return getByPeriode(LocalDate.of(1900,1,1), LocalDate.of(2999,12,31)); }
    public List<Pemasukan> getByPeriode(LocalDate dari, LocalDate sampai) {
        List<Pemasukan> list=new ArrayList<>(); String sql="SELECT p.*,s.nama_siswa,k.nama_kategori FROM pemasukan p LEFT JOIN siswa s ON s.id=p.id_siswa JOIN kategori_pemasukan k ON k.id=p.id_kategori WHERE p.tanggal BETWEEN ? AND ? ORDER BY p.tanggal DESC,p.id DESC";
        try { Connection c=Koneksi.getConnection(); if(c==null)return list; try(PreparedStatement ps=c.prepareStatement(sql)){ ps.setDate(1, java.sql.Date.valueOf(dari)); ps.setDate(2, java.sql.Date.valueOf(sampai)); try(ResultSet rs=ps.executeQuery()){ while(rs.next()) list.add(map(rs)); } } }
        catch(SQLException ex){ System.err.println("getByPeriode pemasukan: "+ex.getMessage()); } return list;
    }
    public boolean save(Pemasukan p) {
        String sql=p.getId()==0?"INSERT INTO pemasukan(tanggal,id_siswa,id_kategori,nominal,keterangan,created_by) VALUES(?,?,?,?,?,?)":"UPDATE pemasukan SET tanggal=?,id_siswa=?,id_kategori=?,nominal=?,keterangan=?,created_by=? WHERE id=?";
        try { Connection c=Koneksi.getConnection(); if(c==null)return false; try(PreparedStatement ps=c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){ ps.setDate(1, java.sql.Date.valueOf(p.getTanggal())); if (p.getIdSiswa()==null) ps.setNull(2, java.sql.Types.INTEGER); else ps.setInt(2,p.getIdSiswa()); ps.setInt(3,p.getIdKategori()); ps.setDouble(4,p.getNominal()); ps.setString(5,p.getKeterangan()); ps.setInt(6,p.getCreatedBy()); if(p.getId()>0) ps.setInt(7,p.getId()); boolean ok=ps.executeUpdate()>0; if(ok) AuditLogger.log(p.getId()==0?"INSERT":"UPDATE","pemasukan","",p.getKeterangan()); return ok; } }
        catch(SQLException ex){ System.err.println("save pemasukan: "+ex.getMessage()); return false; }
    }
    public boolean delete(int id) { try { Connection c=Koneksi.getConnection(); if(c==null)return false; try(PreparedStatement ps=c.prepareStatement("DELETE FROM pemasukan WHERE id=?")){ ps.setInt(1,id); boolean ok=ps.executeUpdate()>0; if(ok) AuditLogger.log("DELETE","pemasukan",String.valueOf(id),""); return ok; } } catch(SQLException ex){ System.err.println("delete pemasukan: "+ex.getMessage()); return false; } }
    public List<UIUtils.Option> getKategori() { List<UIUtils.Option> list=new ArrayList<>(); try { Connection c=Koneksi.getConnection(); if(c==null)return list; try(PreparedStatement ps=c.prepareStatement("SELECT id,nama_kategori FROM kategori_pemasukan ORDER BY nama_kategori"); ResultSet rs=ps.executeQuery()){ while(rs.next()) list.add(new UIUtils.Option(rs.getInt(1), rs.getString(2))); } } catch(SQLException ex){ System.err.println("kategori pemasukan: "+ex.getMessage()); } return list; }
    public double getTotalBulanIni() { double total=0; String sql="SELECT COALESCE(SUM(nominal),0) FROM pemasukan WHERE YEAR(tanggal)=YEAR(CURDATE()) AND MONTH(tanggal)=MONTH(CURDATE())"; try { Connection c=Koneksi.getConnection(); if(c==null)return 0; try(PreparedStatement ps=c.prepareStatement(sql); ResultSet rs=ps.executeQuery()){ if(rs.next()) total=rs.getDouble(1); } } catch(SQLException ex){ System.err.println("total pemasukan: "+ex.getMessage()); } return total; }
}
