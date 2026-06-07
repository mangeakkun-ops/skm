package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import koneksi.Koneksi;
import model.Siswa;
import model.Tabungan;

public class TabunganController {
    public List<Tabungan> getMutasiSiswa(int idSiswa) { List<Tabungan> list=new ArrayList<>(); String sql="SELECT t.*,s.nama_siswa FROM tabungan t JOIN siswa s ON s.id=t.id_siswa WHERE id_siswa=? ORDER BY tanggal DESC,id DESC"; try{ Connection c=Koneksi.getConnection(); if(c==null)return list; try(PreparedStatement ps=c.prepareStatement(sql)){ ps.setInt(1,idSiswa); try(ResultSet rs=ps.executeQuery()){ while(rs.next()) list.add(new Tabungan(rs.getInt("id"), rs.getInt("id_siswa"), rs.getString("nama_siswa"), rs.getDate("tanggal").toLocalDate(), rs.getString("jenis"), rs.getDouble("nominal"), rs.getDouble("saldo_akhir"), rs.getString("keterangan"))); } } } catch(SQLException ex){ System.err.println("mutasi tabungan: "+ex.getMessage()); } return list; }
    public double getSaldoSiswa(int idSiswa) { String sql="SELECT COALESCE(saldo_akhir,0) FROM tabungan WHERE id_siswa=? ORDER BY tanggal DESC,id DESC LIMIT 1"; try{ Connection c=Koneksi.getConnection(); if(c==null)return 0; try(PreparedStatement ps=c.prepareStatement(sql)){ ps.setInt(1,idSiswa); try(ResultSet rs=ps.executeQuery()){ if(rs.next()) return rs.getDouble(1); } } } catch(SQLException ex){ System.err.println("saldo tabungan: "+ex.getMessage()); } return 0; }
    public boolean setor(int idSiswa,double nominal,String ket,int createdBy){ return mutasi(idSiswa,"SETOR",nominal,ket,createdBy); }
    public boolean tarik(int idSiswa,double nominal,String ket,int createdBy){ if(getSaldoSiswa(idSiswa)<nominal) return false; return mutasi(idSiswa,"TARIK",nominal,ket,createdBy); }
    private boolean mutasi(int idSiswa,String jenis,double nominal,String ket,int createdBy){ double saldo=getSaldoSiswa(idSiswa)+("SETOR".equals(jenis)?nominal:-nominal); String sql="INSERT INTO tabungan(id_siswa,tanggal,jenis,nominal,saldo_akhir,keterangan,created_by) VALUES(?,CURDATE(),?,?,?,?,?)"; try{ Connection c=Koneksi.getConnection(); if(c==null)return false; try(PreparedStatement ps=c.prepareStatement(sql)){ ps.setInt(1,idSiswa); ps.setString(2,jenis); ps.setDouble(3,nominal); ps.setDouble(4,saldo); ps.setString(5,ket); ps.setInt(6,createdBy); boolean ok=ps.executeUpdate()>0; if(ok) AuditLogger.log(jenis,"tabungan","",ket); return ok; } } catch(SQLException ex){ System.err.println("mutasi tabungan: "+ex.getMessage()); return false; } }
    public List<Siswa> searchSiswa(String keyword){ return new SiswaController().search(keyword); }
}
