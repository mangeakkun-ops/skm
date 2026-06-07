package koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class Koneksi {
    private static Connection conn;

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = "jdbc:mysql://localhost:3306/kas_sekolah?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
                conn = DriverManager.getConnection(url, "root", "");
                System.out.println("Koneksi Berhasil");
            }
        } catch (Exception e) {
            System.out.println("Koneksi Gagal: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Koneksi database gagal!\nPastikan XAMPP MySQL sudah running.\n\n" + e.getMessage(),
                "Error Koneksi", JOptionPane.ERROR_MESSAGE);
        }
        return conn;
    }
}