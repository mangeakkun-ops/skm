package model;

public class Kelas {
    private int id;
    private String kodeKelas;
    private String namaKelas;

    public Kelas() {
    }

    public Kelas(int id, String kodeKelas, String namaKelas) {
        this.id = id;
        this.kodeKelas = kodeKelas;
        this.namaKelas = namaKelas;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getKodeKelas() { return kodeKelas; }

    public void setKodeKelas(String kodeKelas) { this.kodeKelas = kodeKelas; }

    public String getNamaKelas() { return namaKelas; }

    public void setNamaKelas(String namaKelas) { this.namaKelas = namaKelas; }

}
