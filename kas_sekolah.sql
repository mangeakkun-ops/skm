CREATE DATABASE IF NOT EXISTS kas_sekolah CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE kas_sekolah;

DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS tabungan;
DROP TABLE IF EXISTS spp_tagihan;
DROP TABLE IF EXISTS pengeluaran;
DROP TABLE IF EXISTS pemasukan;
DROP TABLE IF EXISTS siswa;
DROP TABLE IF EXISTS kategori_pengeluaran;
DROP TABLE IF EXISTS kategori_pemasukan;
DROP TABLE IF EXISTS kelas;
DROP TABLE IF EXISTS pengaturan;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nama VARCHAR(100) NOT NULL,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  role ENUM('ADMIN','KASIR') NOT NULL DEFAULT 'KASIR'
) ENGINE=InnoDB;

CREATE TABLE kelas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  kode_kelas VARCHAR(20) NOT NULL UNIQUE,
  nama_kelas VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE siswa (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nis VARCHAR(30) NOT NULL UNIQUE,
  nama_siswa VARCHAR(120) NOT NULL,
  id_kelas INT NOT NULL,
  jenis_kelamin ENUM('L','P') NOT NULL,
  alamat TEXT,
  CONSTRAINT fk_siswa_kelas FOREIGN KEY (id_kelas) REFERENCES kelas(id) ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE kategori_pemasukan (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nama_kategori VARCHAR(100) NOT NULL,
  nominal_default DECIMAL(15,2) DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE kategori_pengeluaran (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nama_kategori VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE pemasukan (
  id INT AUTO_INCREMENT PRIMARY KEY,
  tanggal DATE NOT NULL,
  id_siswa INT NULL,
  id_kategori INT NOT NULL,
  nominal DECIMAL(15,2) NOT NULL,
  keterangan TEXT,
  created_by INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_siswa) REFERENCES siswa(id) ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (id_kategori) REFERENCES kategori_pemasukan(id) ON UPDATE CASCADE,
  FOREIGN KEY (created_by) REFERENCES users(id) ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE pengeluaran (
  id INT AUTO_INCREMENT PRIMARY KEY,
  tanggal DATE NOT NULL,
  id_kategori INT NOT NULL,
  nominal DECIMAL(15,2) NOT NULL,
  keterangan TEXT,
  created_by INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_kategori) REFERENCES kategori_pengeluaran(id) ON UPDATE CASCADE,
  FOREIGN KEY (created_by) REFERENCES users(id) ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE spp_tagihan (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_siswa INT NOT NULL,
  bulan INT NOT NULL,
  tahun INT NOT NULL,
  nominal DECIMAL(15,2) NOT NULL,
  status ENUM('BELUM','LUNAS') NOT NULL DEFAULT 'BELUM',
  FOREIGN KEY (id_siswa) REFERENCES siswa(id) ON DELETE CASCADE ON UPDATE CASCADE,
  UNIQUE KEY uk_spp (id_siswa, bulan, tahun)
) ENGINE=InnoDB;

CREATE TABLE tabungan (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_siswa INT NOT NULL,
  tanggal DATE NOT NULL,
  jenis ENUM('SETOR','TARIK') NOT NULL,
  nominal DECIMAL(15,2) NOT NULL,
  saldo_akhir DECIMAL(15,2) NOT NULL,
  keterangan TEXT,
  created_by INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_siswa) REFERENCES siswa(id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (created_by) REFERENCES users(id) ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE pengaturan (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nama_sekolah VARCHAR(150) NOT NULL,
  alamat TEXT,
  telepon VARCHAR(50),
  email VARCHAR(100),
  kepala_sekolah VARCHAR(100),
  bendahara VARCHAR(100),
  logo_path VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE audit_log (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NULL,
  aksi VARCHAR(50) NOT NULL,
  tabel_target VARCHAR(80) NOT NULL,
  data_lama TEXT,
  data_baru TEXT,
  waktu DATETIME NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

INSERT INTO users(nama, username, password, role) VALUES
('Administrator', 'admin', '$2a$12$hjx/GIf9f8nFVZQchxnPLeC6UaQv.gemMEz0HkmTMCNQgd0/OQtvC', 'ADMIN'),
('Kasir Madrasah', 'kasir', '$2a$12$JoJg83/9r7h.n/9IsBZORuVT6vmdlwU2Oe7xC8vetYPla2b/stK4S', 'KASIR');

INSERT INTO kelas(kode_kelas, nama_kelas) VALUES
('X-A','X-A'),('X-B','X-B'),('XI-A','XI-A'),('XI-B','XI-B'),('XII-A','XII-A'),('XII-B','XII-B');

INSERT INTO siswa(nis,nama_siswa,id_kelas,jenis_kelamin,alamat) VALUES
('1001','Ahmad Fauzi',1,'L','Jl. Melati 1'),('1002','Siti Aminah',1,'P','Jl. Mawar 2'),('1003','Budi Santoso',2,'L','Jl. Kenanga 3'),('1004','Dewi Lestari',2,'P','Jl. Anggrek 4'),('1005','Rizky Ramadhan',3,'L','Jl. Dahlia 5'),('1006','Nabila Putri',3,'P','Jl. Cempaka 6'),('1007','Hasan Basri',4,'L','Jl. Teratai 7'),('1008','Aisyah Nur',4,'P','Jl. Flamboyan 8'),('1009','Ilham Maulana',5,'L','Jl. Sakura 9'),('1010','Zahra Khairunnisa',6,'P','Jl. Kamboja 10');

INSERT INTO kategori_pemasukan(nama_kategori, nominal_default) VALUES
('SPP',350000),('Dana BOS',0),('Donasi',0),('Sewa Fasilitas',0),('Uang Gedung',1500000),('Lainnya',0);
INSERT INTO kategori_pengeluaran(nama_kategori) VALUES
('Listrik'),('Internet'),('ATK'),('Gaji'),('Perbaikan'),('Kebersihan'),('Lainnya');

INSERT INTO pemasukan(tanggal,id_siswa,id_kategori,nominal,keterangan,created_by) VALUES
(CURDATE() - INTERVAL 80 DAY,1,1,350000,'SPP Ahmad',1),(CURDATE() - INTERVAL 65 DAY,2,1,350000,'SPP Siti',2),(CURDATE() - INTERVAL 45 DAY,NULL,2,12000000,'Dana BOS triwulan',1),(CURDATE() - INTERVAL 25 DAY,3,5,1500000,'Uang Gedung Budi',1),(CURDATE() - INTERVAL 10 DAY,NULL,3,2000000,'Donasi wali murid',2),(CURDATE(),4,1,350000,'SPP Dewi',2);
INSERT INTO pengeluaran(tanggal,id_kategori,nominal,keterangan,created_by) VALUES
(CURDATE() - INTERVAL 70 DAY,1,850000,'Tagihan listrik',1),(CURDATE() - INTERVAL 50 DAY,3,1250000,'Pembelian ATK',2),(CURDATE() - INTERVAL 35 DAY,4,8000000,'Gaji honorer',1),(CURDATE() - INTERVAL 15 DAY,2,500000,'Internet sekolah',2),(CURDATE() - INTERVAL 5 DAY,5,1750000,'Perbaikan kelas',1);
INSERT INTO tabungan(id_siswa,tanggal,jenis,nominal,saldo_akhir,keterangan,created_by) VALUES
(1,CURDATE()-INTERVAL 20 DAY,'SETOR',100000,100000,'Setoran awal',2),(1,CURDATE()-INTERVAL 5 DAY,'SETOR',50000,150000,'Setoran tambahan',2),(2,CURDATE()-INTERVAL 15 DAY,'SETOR',200000,200000,'Setoran awal',2),(2,CURDATE()-INTERVAL 3 DAY,'TARIK',50000,150000,'Beli buku',2);
INSERT INTO spp_tagihan(id_siswa,bulan,tahun,nominal,status) VALUES
(1,MONTH(CURDATE()),YEAR(CURDATE()),350000,'LUNAS'),(2,MONTH(CURDATE()),YEAR(CURDATE()),350000,'BELUM'),(3,MONTH(CURDATE()),YEAR(CURDATE()),350000,'BELUM'),(4,MONTH(CURDATE()),YEAR(CURDATE()),350000,'LUNAS'),(5,MONTH(CURDATE()),YEAR(CURDATE()),350000,'BELUM');
INSERT INTO pengaturan(nama_sekolah,alamat,telepon,email,kepala_sekolah,bendahara,logo_path) VALUES
('Madrasah Contoh Nusantara','Jl. Pendidikan No. 10','021-123456','info@madrasah.sch.id','Drs. H. Abdullah','Siti Maryam, S.E.','');
