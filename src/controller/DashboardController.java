package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import koneksi.Koneksi;
import util.UIUtils;

public class DashboardController {
    private double scalar(String sql) { try { Connection c=Koneksi.getConnection(); if(c==null)return 0; try(PreparedStatement ps=c.prepareStatement(sql); ResultSet rs=ps.executeQuery()){ if(rs.next()) return rs.getDouble(1); } } catch(SQLException ex){ System.err.println("dashboard scalar: "+ex.getMessage()); } return 0; }
    public double getSaldoKas() { return scalar("SELECT (SELECT COALESCE(SUM(nominal),0) FROM pemasukan)-(SELECT COALESCE(SUM(nominal),0) FROM pengeluaran)"); }
    public double getPemasukanBulanIni() { return new PemasukanController().getTotalBulanIni(); }
    public double getPengeluaranBulanIni() { return new PengeluaranController().getTotalBulanIni(); }
    public int getJumlahSiswaTunggak() { return (int) scalar("SELECT COUNT(DISTINCT id_siswa) FROM spp_tagihan WHERE status='BELUM' AND bulan<=MONTH(CURDATE()) AND tahun<=YEAR(CURDATE())"); }
    public Map<String,double[]> getData6BulanTerakhir() {
        Map<String,double[]> map = new LinkedHashMap<>(); LocalDate start = LocalDate.now().minusMonths(5).withDayOfMonth(1);
        for(int i=0;i<6;i++){ LocalDate d=start.plusMonths(i); map.put(d.getMonth().getDisplayName(TextStyle.SHORT, new Locale("id","ID"))+" "+String.valueOf(d.getYear()).substring(2), new double[]{0,0}); }
        fillMonthly(map, "SELECT DATE_FORMAT(tanggal,'%Y-%m') ym,SUM(nominal) total FROM pemasukan WHERE tanggal>=? GROUP BY ym", 0, start);
        fillMonthly(map, "SELECT DATE_FORMAT(tanggal,'%Y-%m') ym,SUM(nominal) total FROM pengeluaran WHERE tanggal>=? GROUP BY ym", 1, start);
        return map;
    }
    private void fillMonthly(Map<String,double[]> map, String sql, int idx, LocalDate start) { try { Connection c=Koneksi.getConnection(); if(c==null)return; try(PreparedStatement ps=c.prepareStatement(sql)){ ps.setDate(1, java.sql.Date.valueOf(start)); try(ResultSet rs=ps.executeQuery()){ while(rs.next()){ LocalDate d=LocalDate.parse(rs.getString(1)+"-01"); String key=d.getMonth().getDisplayName(TextStyle.SHORT,new Locale("id","ID"))+" "+String.valueOf(d.getYear()).substring(2); if(map.containsKey(key)) map.get(key)[idx]=rs.getDouble(2); } } } } catch(SQLException ex){ System.err.println("monthly: "+ex.getMessage()); } }
    public List<String[]> getTransaksiTerbaru(int limit) {
        List<String[]> list=new ArrayList<>(); String sql="SELECT tanggal,keterangan,nominal,'Pemasukan' tipe FROM pemasukan UNION ALL SELECT tanggal,keterangan,nominal,'Pengeluaran' tipe FROM pengeluaran ORDER BY tanggal DESC LIMIT ?";
        try { Connection c=Koneksi.getConnection(); if(c==null)return list; try(PreparedStatement ps=c.prepareStatement(sql)){ ps.setInt(1,limit); try(ResultSet rs=ps.executeQuery()){ while(rs.next()) list.add(new String[]{rs.getString("tanggal"),rs.getString("tipe"),rs.getString("keterangan"),UIUtils.rupiah(rs.getDouble("nominal"))}); } } } catch(SQLException ex){ System.err.println("transaksi terbaru: "+ex.getMessage()); }
        return list;
    }
}
