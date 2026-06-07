package form;

import controller.PemasukanController;
import controller.AuthController;
import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Pemasukan;
import util.UIUtils;

public class PemasukanForm extends JPanel {

    private final PemasukanController c = new PemasukanController();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Tanggal", "Siswa", "Kategori", "Nominal", "Keterangan"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel total = new JLabel("Total: Rp 0");
    private final JTextField dari   = UIUtils.textField(10);
    private final JTextField sampai = UIUtils.textField(10);

    public PemasukanForm() {
        setLayout(new BorderLayout());
        JPanel p = UIUtils.page("Pemasukan");

        dari.setText(LocalDate.now().withDayOfMonth(1).toString());
        sampai.setText(LocalDate.now().toString());

        JButton filter = UIUtils.button("FILTER", UIUtils.BLUE);
        JButton add    = UIUtils.button("TAMBAH", UIUtils.GREEN);
        JButton del    = UIUtils.button("HAPUS",  UIUtils.RED);

        p.add(UIUtils.toolbar(
                UIUtils.formLabel("Dari"), dari,
                UIUtils.formLabel("Sampai"), sampai,
                filter, add, del), BorderLayout.NORTH);

        JPanel card = UIUtils.card();
        card.setLayout(new BorderLayout());
        card.add(UIUtils.tableScroll(table), BorderLayout.CENTER);
        p.add(card);

        total.setFont(UIUtils.FONT_H2);
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        foot.setOpaque(false);
        foot.add(total);
        p.add(foot, BorderLayout.SOUTH);

        add(p);
        table.removeColumn(table.getColumnModel().getColumn(0));

        filter.addActionListener(e -> load());
        add.addActionListener(e -> showDialog());
        del.addActionListener(e -> delete());
        load();
    }

    private void load() {
        model.setRowCount(0);
        double sum = 0;
        for (Pemasukan x : c.getByPeriode(
                LocalDate.parse(dari.getText()),
                LocalDate.parse(sampai.getText()))) {
            sum += x.getNominal();
            model.addRow(new Object[]{
                x.getId(), x.getTanggal(), x.getNamaSiswa(),
                x.getNamaKategori(), UIUtils.rupiah(x.getNominal()), x.getKeterangan()
            });
        }
        total.setText("Total: " + UIUtils.rupiah(sum));
    }

    private void showDialog() {
        // Cari parent window
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog((Frame) (owner instanceof Frame ? owner : null),
                "Tambah Pemasukan", true);
        dlg.setLayout(new BorderLayout());
        dlg.setSize(460, 400);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        // ── Form panel 2 kolom ──
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));
        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(6, 0, 4, 12);
        lc.gridx = 0; lc.gridy = 0; lc.weightx = 0;

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.insets = new Insets(6, 0, 4, 0);
        fc.gridx = 1; fc.gridy = 0; fc.weightx = 1;

        JTextField tgl     = UIUtils.textField(14);
        JTextField nominal = UIUtils.textField(14);
        JTextField siswa   = UIUtils.textField(10);
        JTextArea  ket     = new JTextArea(3, 20);
        ket.setFont(UIUtils.FONT_PLAIN);
        ket.setLineWrap(true);
        ket.setWrapStyleWord(true);
        ket.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        JComboBox<UIUtils.Option> kat = UIUtils.comboBox(
                c.getKategori().toArray(new UIUtils.Option[0]));
        tgl.setText(LocalDate.now().toString());

        // Baris 0 — Tanggal
        form.add(UIUtils.formLabel("Tanggal"), lc);
        form.add(tgl, fc);

        // Baris 1 — Kategori
        lc.gridy = 1; fc.gridy = 1;
        form.add(UIUtils.formLabel("Kategori"), lc);
        form.add(kat, fc);

        // Baris 2 — ID Siswa
        lc.gridy = 2; fc.gridy = 2;
        form.add(UIUtils.formLabel("ID Siswa (opsional)"), lc);
        form.add(siswa, fc);

        // Baris 3 — Nominal
        lc.gridy = 3; fc.gridy = 3;
        form.add(UIUtils.formLabel("Nominal"), lc);
        form.add(nominal, fc);

        // Baris 4 — Keterangan (span 1 kolom, tinggi lebih)
        lc.gridy = 4; fc.gridy = 4;
        fc.ipady = 10;
        form.add(UIUtils.formLabel("Keterangan"), lc);
        form.add(new JScrollPane(ket), fc);

        // ── Tombol bawah ──
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        btnPanel.setBackground(UIUtils.WHITE);
        JButton batal  = UIUtils.buttonOutline("Batal",  UIUtils.RED);
        JButton simpan = UIUtils.button("SIMPAN", UIUtils.GREEN);

        batal.addActionListener(e -> dlg.dispose());
        simpan.addActionListener(e -> {
            try {
                UIUtils.Option ko = (UIUtils.Option) kat.getSelectedItem();
                int uid = AuthController.getCurrentUser() == null
                        ? 0 : AuthController.getCurrentUser().getId();
                Integer sid = siswa.getText().trim().isEmpty()
                        ? null : Integer.valueOf(siswa.getText().trim());
                Pemasukan val = new Pemasukan(
                        0, LocalDate.parse(tgl.getText()),
                        sid, "", ko.id, "",
                        UIUtils.parseNumber(nominal.getText()),
                        ket.getText(), uid);
                if (c.save(val)) {
                    UIUtils.showSuccess(dlg, "Data tersimpan");
                    dlg.dispose();
                    load();
                } else {
                    UIUtils.showError(dlg, "Gagal menyimpan");
                }
            } catch (Exception ex) {
                UIUtils.showError(dlg, "Input tidak valid: " + ex.getMessage());
            }
        });

        btnPanel.add(batal);
        btnPanel.add(simpan);

        dlg.add(form, BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void delete() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        int m  = table.convertRowIndexToModel(r);
        int id = (int) model.getValueAt(m, 0);
        if (UIUtils.showConfirm(this, "Hapus transaksi ini?")) {
            c.delete(id);
            load();
        }
    }
}