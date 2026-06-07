package model;

public class Siswa {
    private int id;
    private String nis;
    private String namaSiswa;
    private int idKelas;
    private String namaKelas;
    private String jenisKelamin;
    private String alamat;

    public Siswa() {
    }

    public Siswa(int id, String nis, String namaSiswa, int idKelas, String namaKelas, String jenisKelamin, String alamat) {
        this.id = id;
        this.nis = nis;
        this.namaSiswa = namaSiswa;
        this.idKelas = idKelas;
        this.namaKelas = namaKelas;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getNis() { return nis; }

    public void setNis(String nis) { this.nis = nis; }

    public String getNamaSiswa() { return namaSiswa; }

    public void setNamaSiswa(String namaSiswa) { this.namaSiswa = namaSiswa; }

    public int getIdKelas() { return idKelas; }

    public void setIdKelas(int idKelas) { this.idKelas = idKelas; }

    public String getNamaKelas() { return namaKelas; }

    public void setNamaKelas(String namaKelas) { this.namaKelas = namaKelas; }

    public String getJenisKelamin() { return jenisKelamin; }

    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }

    public String getAlamat() { return alamat; }

    public void setAlamat(String alamat) { this.alamat = alamat; }

}
