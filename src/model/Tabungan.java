package model;

import java.time.LocalDate;

public class Tabungan {
    private int id;
    private int idSiswa;
    private String namaSiswa;
    private LocalDate tanggal;
    private String jenis;
    private double nominal;
    private double saldoAkhir;
    private String keterangan;

    public Tabungan() {
    }

    public Tabungan(int id, int idSiswa, String namaSiswa, LocalDate tanggal, String jenis, double nominal, double saldoAkhir, String keterangan) {
        this.id = id;
        this.idSiswa = idSiswa;
        this.namaSiswa = namaSiswa;
        this.tanggal = tanggal;
        this.jenis = jenis;
        this.nominal = nominal;
        this.saldoAkhir = saldoAkhir;
        this.keterangan = keterangan;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getIdSiswa() { return idSiswa; }

    public void setIdSiswa(int idSiswa) { this.idSiswa = idSiswa; }

    public String getNamaSiswa() { return namaSiswa; }

    public void setNamaSiswa(String namaSiswa) { this.namaSiswa = namaSiswa; }

    public LocalDate getTanggal() { return tanggal; }

    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }

    public String getJenis() { return jenis; }

    public void setJenis(String jenis) { this.jenis = jenis; }

    public double getNominal() { return nominal; }

    public void setNominal(double nominal) { this.nominal = nominal; }

    public double getSaldoAkhir() { return saldoAkhir; }

    public void setSaldoAkhir(double saldoAkhir) { this.saldoAkhir = saldoAkhir; }

    public String getKeterangan() { return keterangan; }

    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

}
