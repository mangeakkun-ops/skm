package model;

public class Pengaturan {
    private int id;
    private String namaSekolah;
    private String alamat;
    private String telepon;
    private String email;
    private String kepalaSekolah;
    private String bendahara;
    private String logoPath;

    public Pengaturan() {
    }

    public Pengaturan(int id, String namaSekolah, String alamat, String telepon, String email, String kepalaSekolah, String bendahara, String logoPath) {
        this.id = id;
        this.namaSekolah = namaSekolah;
        this.alamat = alamat;
        this.telepon = telepon;
        this.email = email;
        this.kepalaSekolah = kepalaSekolah;
        this.bendahara = bendahara;
        this.logoPath = logoPath;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getNamaSekolah() { return namaSekolah; }

    public void setNamaSekolah(String namaSekolah) { this.namaSekolah = namaSekolah; }

    public String getAlamat() { return alamat; }

    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getTelepon() { return telepon; }

    public void setTelepon(String telepon) { this.telepon = telepon; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getKepalaSekolah() { return kepalaSekolah; }

    public void setKepalaSekolah(String kepalaSekolah) { this.kepalaSekolah = kepalaSekolah; }

    public String getBendahara() { return bendahara; }

    public void setBendahara(String bendahara) { this.bendahara = bendahara; }

    public String getLogoPath() { return logoPath; }

    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

}
