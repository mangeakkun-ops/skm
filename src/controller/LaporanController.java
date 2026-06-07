package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import koneksi.Koneksi;
import util.UIUtils;

public class LaporanController {
    public List<String[]> getBukuKas(LocalDate dari, LocalDate sampai) { List<String[]> list=new ArrayList<>(); double saldo=0; String sql="SELECT tanggal,keterangan,nominal debit,0 kredit FROM pemasukan WHERE tanggal BETWEEN ? AND ? UNION ALL SELECT tanggal,keterangan,0 debit,nominal kredit FROM pengeluaran WHERE tanggal BETWEEN ? AND ? ORDER BY tanggal"; try{ Connection c=Koneksi.getConnection(); if(c==null)return list; try(PreparedStatement ps=c.prepareStatement(sql)){ ps.setDate(1,java.sql.Date.valueOf(dari)); ps.setDate(2,java.sql.Date.valueOf(sampai)); ps.setDate(3,java.sql.Date.valueOf(dari)); ps.setDate(4,java.sql.Date.valueOf(sampai)); try(ResultSet rs=ps.executeQuery()){ while(rs.next()){ double d=rs.getDouble("debit"), k=rs.getDouble("kredit"); saldo+=d-k; list.add(new String[]{rs.getString("tanggal"),rs.getString("keterangan"),UIUtils.rupiah(d),UIUtils.rupiah(k),UIUtils.rupiah(saldo)}); } } } } catch(SQLException ex){ System.err.println("buku kas: "+ex.getMessage()); } return list; }
    public List<String[]> getTunggakanSPP(int bulan,int tahun){ List<String[]> list=new ArrayList<>(); String sql="SELECT s.nis,s.nama_siswa,k.nama_kelas,t.bulan,t.nominal FROM spp_tagihan t JOIN siswa s ON s.id=t.id_siswa LEFT JOIN kelas k ON k.id=s.id_kelas WHERE t.bulan=? AND t.tahun=? AND t.status='BELUM' ORDER BY k.kode_kelas,s.nama_siswa"; try{ Connection c=Koneksi.getConnection(); if(c==null)return list; try(PreparedStatement ps=c.prepareStatement(sql)){ ps.setInt(1,bulan); ps.setInt(2,tahun); try(ResultSet rs=ps.executeQuery()){ while(rs.next()) list.add(new String[]{rs.getString(1),rs.getString(2),rs.getString(3),String.valueOf(rs.getInt(4)),UIUtils.rupiah(rs.getDouble(5))}); } } } catch(SQLException ex){ System.err.println("tunggakan: "+ex.getMessage()); } return list; }
    public boolean exportCSV(List<String[]> data,String[] headers,File file){ try(BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))){ bw.write(String.join(",", headers)); bw.newLine(); for(String[] row:data){ for(int i=0;i<row.length;i++){ if(i>0) bw.write(','); bw.write('"'); bw.write(row[i].replace("\"","\"\"")); bw.write('"'); } bw.newLine(); } return true; } catch(Exception ex){ System.err.println("CSV: "+ex.getMessage()); return false; } }
    public boolean exportPDF(List<String[]> data,String[] headers,String judul,File file){
        try { return exportPDFWithIText(data, headers, judul, file); } catch (Exception ex) { return exportSimplePDF(data, headers, judul, file); }
    }
    private boolean exportPDFWithIText(List<String[]> data,String[] headers,String judul,File file) throws Exception {
        Class<?> writerCls=Class.forName("com.itextpdf.kernel.pdf.PdfWriter"); Class<?> pdfDocCls=Class.forName("com.itextpdf.kernel.pdf.PdfDocument"); Class<?> docCls=Class.forName("com.itextpdf.layout.Document"); Class<?> paraCls=Class.forName("com.itextpdf.layout.element.Paragraph"); Class<?> tableCls=Class.forName("com.itextpdf.layout.element.Table");
        Object writer=writerCls.getConstructor(String.class).newInstance(file.getAbsolutePath()); Object pdf=pdfDocCls.getConstructor(writerCls).newInstance(writer); Object doc=docCls.getConstructor(pdfDocCls).newInstance(pdf);
        Object p=paraCls.getConstructor(String.class).newInstance(judul); docCls.getMethod("add", Class.forName("com.itextpdf.layout.element.IBlockElement")).invoke(doc,p);
        Constructor<?> tc=tableCls.getConstructor(int.class); Object table=tc.newInstance(headers.length); Method addCell=tableCls.getMethod("addCell", String.class); for(String h:headers)addCell.invoke(table,h); for(String[] r:data) for(String v:r)addCell.invoke(table,v); docCls.getMethod("add", Class.forName("com.itextpdf.layout.element.IBlockElement")).invoke(doc,table); docCls.getMethod("close").invoke(doc); return true;
    }
    private boolean exportSimplePDF(List<String[]> data,String[] headers,String judul,File file){ try(BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.ISO_8859_1))){ bw.write("%PDF-1.4\n1 0 obj<<>>endobj\n2 0 obj<< /Length 2000 >>stream\nBT /F1 10 Tf 40 780 Td ("+judul+") Tj\n"); int y=760; bw.write("0 -20 Td ("+String.join(" | ",headers).replace("(","[").replace(")","]")+") Tj\n"); for(String[] r:data){ y-=16; if(y<40)break; bw.write("0 -16 Td ("+String.join(" | ",r).replace("(","[").replace(")","]")+") Tj\n"); } bw.write("ET\nendstream endobj\n3 0 obj<< /Type /Page /Parent 4 0 R /Contents 2 0 R /Resources << /Font << /F1 5 0 R >> >> >>endobj\n4 0 obj<< /Type /Pages /Kids [3 0 R] /Count 1 >>endobj\n5 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>endobj\n6 0 obj<< /Type /Catalog /Pages 4 0 R >>endobj\ntrailer<< /Root 6 0 R >>\n%%EOF"); return true; } catch(Exception ex){ System.err.println("PDF: "+ex.getMessage()); return false; } }
}
