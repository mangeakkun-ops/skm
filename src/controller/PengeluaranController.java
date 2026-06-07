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
import model.Pengeluaran;
import util.UIUtils;

public class PengeluaranController {
    private Pengeluaran map(ResultSet rs) throws SQLException { return new Pengeluaran(rs.getInt("id"), rs.getDate("tanggal").toLocalDate(), rs.getInt("id_kategori"), rs.getString("nama_kategori"), rs.getDouble("nominal"), rs.getString("keterangan"), rs.getInt("created_by")); }
    public List<Pengeluaran> getAll() { return getByPeriode(LocalDate.of(1900,1,1), LocalDate.of(2999,12,31)); }
    public List<Pengeluaran> getByPeriode(LocalDate dari, LocalDate sampai) {
        List<Pengeluaran> list=new ArrayList<>(); String sql="SELECT p.*,k.nama_kategori FROM pengeluaran p JOIN kategori_pengeluaran k ON k.id=p.id_kategori WHERE p.tanggal BETWEEN ? AND ? ORDER BY p.tanggal DESC,p.id DESC";
        try { Connection c=Koneksi.getConnection(); if(c==null)return list; try(PreparedStatement ps=c.prepareStatement(sql)){ ps.setDate(1, java.sql.Date.valueOf(dari)); ps.setDate(2, java.sql.Date.valueOf(sampai)); try(ResultSet rs=ps.executeQuery()){ while(rs.next()) list.add(map(rs)); } } }
        catch(SQLException ex){ System.err.println("getByPeriode pengeluaran: "+ex.getMessage()); } return list;
    }
    public boolean save(Pengeluaran p) {
        String sql=p.getId()==0?"INSERT INTO pengeluaran(tanggal,id_kategori,nominal,keterangan,created_by) VALUES(?,?,?,?,?)":"UPDATE pengeluaran SET tanggal=?,id_kategori=?,nominal=?,keterangan=?,created_by=? WHERE id=?";
        try { Connection c=Koneksi.getConnection(); if(c==null)return false; try(PreparedStatement ps=c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){ ps.setDate(1, java.sql.Date.valueOf(p.getTanggal())); ps.setInt(2,p.getIdKategori()); ps.setDouble(3,p.getNominal()); ps.setString(4,p.getKeterangan()); ps.setInt(5,p.getCreatedBy()); if(p.getId()>0) ps.setInt(6,p.getId()); boolean ok=ps.executeUpdate()>0; if(ok) AuditLogger.log(p.getId()==0?"INSERT":"UPDATE","pengeluaran","",p.getKeterangan()); return ok; } }
        catch(SQLException ex){ System.err.println("save pengeluaran: "+ex.getMessage()); return false; }
    }
    public boolean delete(int id) { try { Connection c=Koneksi.getConnection(); if(c==null)return false; try(PreparedStatement ps=c.prepareStatement("DELETE FROM pengeluaran WHERE id=?")){ ps.setInt(1,id); boolean ok=ps.executeUpdate()>0; if(ok) AuditLogger.log("DELETE","pengeluaran",String.valueOf(id),""); return ok; } } catch(SQLException ex){ System.err.println("delete pengeluaran: "+ex.getMessage()); return false; } }
    public List<UIUtils.Option> getKategori() { List<UIUtils.Option> list=new ArrayList<>(); try { Connection c=Koneksi.getConnection(); if(c==null)return list; try(PreparedStatement ps=c.prepareStatement("SELECT id,nama_kategori FROM kategori_pengeluaran ORDER BY nama_kategori"); ResultSet rs=ps.executeQuery()){ while(rs.next()) list.add(new UIUtils.Option(rs.getInt(1), rs.getString(2))); } } catch(SQLException ex){ System.err.println("kategori pengeluaran: "+ex.getMessage()); } return list; }
    public double getTotalBulanIni() { double total=0; String sql="SELECT COALESCE(SUM(nominal),0) FROM pengeluaran WHERE YEAR(tanggal)=YEAR(CURDATE()) AND MONTH(tanggal)=MONTH(CURDATE())"; try { Connection c=Koneksi.getConnection(); if(c==null)return 0; try(PreparedStatement ps=c.prepareStatement(sql); ResultSet rs=ps.executeQuery()){ if(rs.next()) total=rs.getDouble(1); } } catch(SQLException ex){ System.err.println("total pengeluaran: "+ex.getMessage()); } return total; }
}
