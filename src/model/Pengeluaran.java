package model;

import java.time.LocalDate;

public class Pengeluaran {
    private int id;
    private LocalDate tanggal;
    private int idKategori;
    private String namaKategori;
    private double nominal;
    private String keterangan;
    private int createdBy;

    public Pengeluaran() {
    }

    public Pengeluaran(int id, LocalDate tanggal, int idKategori, String namaKategori, double nominal, String keterangan, int createdBy) {
        this.id = id;
        this.tanggal = tanggal;
        this.idKategori = idKategori;
        this.namaKategori = namaKategori;
        this.nominal = nominal;
        this.keterangan = keterangan;
        this.createdBy = createdBy;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public LocalDate getTanggal() { return tanggal; }

    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }

    public int getIdKategori() { return idKategori; }

    public void setIdKategori(int idKategori) { this.idKategori = idKategori; }

    public String getNamaKategori() { return namaKategori; }

    public void setNamaKategori(String namaKategori) { this.namaKategori = namaKategori; }

    public double getNominal() { return nominal; }

    public void setNominal(double nominal) { this.nominal = nominal; }

    public String getKeterangan() { return keterangan; }

    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    public int getCreatedBy() { return createdBy; }

    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

}
