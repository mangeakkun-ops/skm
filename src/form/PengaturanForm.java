package form;

import controller.PengaturanController;
import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.AuditLog;
import model.Pengaturan;
import model.User;
import util.UIUtils;

public class PengaturanForm extends JPanel {

    private final PengaturanController c = new PengaturanController();
    private final DefaultTableModel userModel = new DefaultTableModel(
            new String[]{"ID", "Nama", "Username", "Role"}, 0);
    private final DefaultTableModel auditModel = new DefaultTableModel(
            new String[]{"Waktu", "User", "Aksi", "Tabel", "Data Lama", "Data Baru"}, 0);

    public PengaturanForm() {
        setLayout(new BorderLayout());
        JPanel p = UIUtils.page("Pengaturan");
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Profil Sekolah", profil());
        tabs.addTab("Manajemen User", users());
        tabs.addTab("Audit Log", audit());
        p.add(tabs);
        add(p);
    }

    private JPanel profil() {
        Pengaturan pg = c.getPengaturan();
        JPanel p = UIUtils.card();
        p.setLayout(new BorderLayout(12, 12));

        JPanel f = new JPanel(new GridLayout(0, 2, 8, 8));
        f.setOpaque(false);

        JTextField nama   = UIUtils.textField(24);
        JTextField alamat = UIUtils.textField(24);
        JTextField telp   = UIUtils.textField(16);
        JTextField email  = UIUtils.textField(18);
        JTextField kep    = UIUtils.textField(20);
        JTextField ben    = UIUtils.textField(20);
        JTextField logo   = UIUtils.textField(24);

        nama.setText(pg.getNamaSekolah());
        alamat.setText(pg.getAlamat());
        telp.setText(pg.getTelepon());
        email.setText(pg.getEmail());
        kep.setText(pg.getKepalaSekolah());
        ben.setText(pg.getBendahara());
        logo.setText(pg.getLogoPath());
        logo.setEditable(false);

        // Panel preview logo
        JLabel preview = new JLabel("No Logo", SwingConstants.CENTER);
        preview.setPreferredSize(new Dimension(130, 130));
        preview.setFont(UIUtils.FONT_SMALL);
        preview.setForeground(UIUtils.TEXT_GRAY);
        preview.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER, 1));
        preview.setOpaque(true);
        preview.setBackground(UIUtils.BACKGROUND);

        // Load preview jika sudah ada logo tersimpan
        if (pg.getLogoPath() != null && !pg.getLogoPath().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(pg.getLogoPath());
                Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                preview.setIcon(new ImageIcon(scaled));
                preview.setText("");
            } catch (Exception ignored) {}
        }

        // Tombol upload logo dengan filter format gambar
        JButton choose = UIUtils.buttonOutline("Upload Logo", UIUtils.BLUE);
        choose.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Pilih Logo Sekolah");

            // Hanya terima PNG, JPG, JPEG, GIF
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "File Gambar (PNG, JPG, JPEG, GIF)", "png", "jpg", "jpeg", "gif"
            );
            fc.setFileFilter(filter);
            fc.setAcceptAllFileFilterUsed(false);

            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String path = fc.getSelectedFile().getAbsolutePath();
                logo.setText(path);

                // Update preview langsung setelah pilih file
                try {
                    ImageIcon icon = new ImageIcon(path);
                    Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                    preview.setIcon(new ImageIcon(scaled));
                    preview.setText("");
                } catch (Exception ex) {
                    preview.setIcon(null);
                    preview.setText("Gagal load");
                }
            }
        });

        f.add(UIUtils.formLabel("Nama Sekolah"));   f.add(nama);
        f.add(UIUtils.formLabel("Alamat"));         f.add(alamat);
        f.add(UIUtils.formLabel("Telepon"));        f.add(telp);
        f.add(UIUtils.formLabel("Email"));          f.add(email);
        f.add(UIUtils.formLabel("Kepala Sekolah")); f.add(kep);
        f.add(UIUtils.formLabel("Bendahara"));      f.add(ben);
        f.add(choose);                              f.add(logo);

        JButton save = UIUtils.button("SIMPAN", UIUtils.GREEN);
        save.addActionListener(e -> {
            Pengaturan val = new Pengaturan(
                    pg.getId(),
                    nama.getText(),
                    alamat.getText(),
                    telp.getText(),
                    email.getText(),
                    kep.getText(),
                    ben.getText(),
                    logo.getText()
            );
            if (c.savePengaturan(val)) UIUtils.showSuccess(this, "Pengaturan berhasil disimpan!");
            else UIUtils.showError(this, "Gagal menyimpan pengaturan.");
        });

        // Panel kanan: preview + label format
        JPanel rightPanel = new JPanel(new BorderLayout(0, 6));
        rightPanel.setOpaque(false);
        JLabel formatInfo = new JLabel("PNG / JPG / JPEG / GIF", SwingConstants.CENTER);
        formatInfo.setFont(UIUtils.FONT_SMALL);
        formatInfo.setForeground(UIUtils.TEXT_GRAY);
        rightPanel.add(preview, BorderLayout.CENTER);
        rightPanel.add(formatInfo, BorderLayout.SOUTH);

        p.add(f, BorderLayout.CENTER);
        p.add(save, BorderLayout.SOUTH);
        p.add(rightPanel, BorderLayout.EAST);
        return p;
    }

    private JPanel users() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);

        JTable table = new JTable(userModel);
        table.removeColumn(table.getColumnModel().getColumn(0)); // sembunyikan kolom ID

        JButton add  = UIUtils.button("TAMBAH USER",    UIUtils.GREEN);
        JButton del  = UIUtils.button("HAPUS",          UIUtils.RED);
        JButton pass = UIUtils.button("GANTI PASSWORD", UIUtils.BLUE);

        p.add(UIUtils.toolbar(add, del, pass), BorderLayout.NORTH);
        p.add(UIUtils.tableScroll(table));

        Runnable load = () -> {
            userModel.setRowCount(0);
            for (User u : c.getAllUsers())
                userModel.addRow(new Object[]{u.getId(), u.getNama(), u.getUsername(), u.getRole()});
        };

        // Tambah User
        add.addActionListener(e -> {
            JTextField n  = UIUtils.textField(18);
            JTextField u  = UIUtils.textField(18);
            JPasswordField pw = UIUtils.passwordField(18);
            JComboBox<String> role = new JComboBox<>(new String[]{"ADMIN", "KASIR"});

            JPanel d = new JPanel(new GridLayout(0, 1, 0, 4));
            d.add(UIUtils.formLabel("Nama"));     d.add(n);
            d.add(UIUtils.formLabel("Username")); d.add(u);
            d.add(UIUtils.formLabel("Password")); d.add(pw);
            d.add(UIUtils.formLabel("Role"));     d.add(role);

            if (JOptionPane.showConfirmDialog(this, d, "Tambah User",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                if (n.getText().isEmpty() || u.getText().isEmpty() || pw.getPassword().length == 0) {
                    UIUtils.showError(this, "Nama, username, dan password tidak boleh kosong!");
                    return;
                }
                boolean ok = c.saveUser(new User(0, n.getText(), u.getText(),
                        new String(pw.getPassword()), String.valueOf(role.getSelectedItem())));
                if (ok) UIUtils.showSuccess(this, "User berhasil ditambahkan!");
                else    UIUtils.showError(this, "Gagal menambah user. Username mungkin sudah ada.");
                load.run();
            }
        });

        // Hapus User
        del.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { UIUtils.showError(this, "Pilih user yang ingin dihapus!"); return; }
            int id = (int) userModel.getValueAt(table.convertRowIndexToModel(r), 0);
            if (UIUtils.showConfirm(this, "Yakin ingin menghapus user ini?")) {
                c.deleteUser(id);
                load.run();
            }
        });

        // Ganti Password
        pass.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { UIUtils.showError(this, "Pilih user yang ingin diganti passwordnya!"); return; }
            int id = (int) userModel.getValueAt(table.convertRowIndexToModel(r), 0);
            JPasswordField pwField = UIUtils.passwordField(18);
            int opt = JOptionPane.showConfirmDialog(this, pwField, "Password Baru",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opt == JOptionPane.OK_OPTION) {
                String newPw = new String(pwField.getPassword());
                if (newPw.isEmpty()) { UIUtils.showError(this, "Password tidak boleh kosong!"); return; }
                if (c.gantiPassword(id, newPw)) UIUtils.showSuccess(this, "Password berhasil diubah!");
                else UIUtils.showError(this, "Gagal mengubah password.");
            }
        });

        load.run();
        return p;
    }

    private JPanel audit() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);

        JTextField search = UIUtils.textField(24);
        JTable table = new JTable(auditModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(auditModel);
        table.setRowSorter(sorter);
        UIUtils.bindSearch(search, sorter);

        p.add(UIUtils.toolbar(UIUtils.formLabel("Cari:"), search), BorderLayout.NORTH);
        p.add(UIUtils.tableScroll(table));

        auditModel.setRowCount(0);
        for (AuditLog a : c.getAuditLog())
            auditModel.addRow(new Object[]{
                a.getWaktu(), a.getNamaUser(), a.getAksi(),
                a.getTabelTarget(), a.getDataLama(), a.getDataBaru()
            });

        return p;
    }
}