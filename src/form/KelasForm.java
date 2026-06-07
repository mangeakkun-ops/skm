package form;

import controller.KelasController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Kelas;
import util.UIUtils;

public class KelasForm extends JPanel {

    private final KelasController c = new KelasController();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Kode Kelas", "Nama Kelas"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public KelasForm() {
        setLayout(new BorderLayout());
        JPanel page = UIUtils.page("Kelas");
        page.setLayout(new BorderLayout(0, 14));

        // ── Toolbar ───────────────────────────────────────────
        JTextField search = UIUtils.textField(22);
        search.putClientProperty("JTextField.placeholderText", "Cari kode / nama kelas...");

        JButton add  = UIUtils.button("＋ TAMBAH", UIUtils.GREEN);
        JButton edit = UIUtils.button("✎ EDIT",    UIUtils.BLUE);
        JButton del  = UIUtils.button("✕ HAPUS",   UIUtils.RED);

        JPanel toolbar = new JPanel(new BorderLayout(8, 0));
        toolbar.setOpaque(false);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchRow.setOpaque(false);
        searchRow.add(UIUtils.formLabel("🔍 Cari"));
        searchRow.add(search);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(add);
        btnRow.add(edit);
        btnRow.add(del);

        toolbar.add(searchRow, BorderLayout.WEST);
        toolbar.add(btnRow,    BorderLayout.EAST);

        // ── Statistik kecil ───────────────────────────────────
        JLabel statLabel = new JLabel("Total: 0 kelas");
        statLabel.setFont(UIUtils.FONT_SMALL);
        statLabel.setForeground(UIUtils.TEXT_GRAY);

        // ── Tabel ─────────────────────────────────────────────
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        // Sembunyikan kolom ID
        table.removeColumn(table.getColumnModel().getColumn(0));

        JPanel tableCard = UIUtils.card();
        tableCard.setLayout(new BorderLayout(0, 8));

        JLabel tableTitle = new JLabel("Daftar Kelas");
        tableTitle.setFont(UIUtils.FONT_BOLD);
        tableTitle.setForeground(UIUtils.TEXT_DARK);

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.add(tableTitle, BorderLayout.WEST);
        tableHeader.add(statLabel,  BorderLayout.EAST);

        tableCard.add(tableHeader,            BorderLayout.NORTH);
        tableCard.add(UIUtils.tableScroll(table), BorderLayout.CENTER);

        // ── Susun layout ──────────────────────────────────────
        page.add(toolbar,   BorderLayout.NORTH);
        page.add(tableCard, BorderLayout.CENTER);
        add(page);

        // ── Sorter & search ───────────────────────────────────
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        UIUtils.bindSearch(search, sorter);

        // Update stat saat filter berubah
        sorter.addRowSorterListener(e ->
            statLabel.setText("Total: " + table.getRowCount() + " kelas"));

        // ── Double click untuk edit ───────────────────────────
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Kelas k = selected();
                    if (k != null) showDialog(k);
                }
            }
        });

        // ── Action listeners ──────────────────────────────────
        add.addActionListener(e -> showDialog(null));
        edit.addActionListener(e -> {
            Kelas k = selected();
            if (k != null) showDialog(k);
            else UIUtils.showError(this, "Pilih kelas yang ingin diedit");
        });
        del.addActionListener(e -> {
            Kelas k = selected();
            if (k == null) { UIUtils.showError(this, "Pilih kelas yang ingin dihapus"); return; }
            if (UIUtils.showConfirm(this, "Hapus kelas \"" + k.getNamaKelas() + "\"?")) {
                c.delete(k.getId());
                load();
            }
        });

        load(statLabel);
    }

    private Kelas selected() {
        int r = table.getSelectedRow();
        if (r < 0) return null;
        int m = table.convertRowIndexToModel(r);
        return new Kelas(
            (int) model.getValueAt(m, 0),
            String.valueOf(model.getValueAt(m, 1)),
            String.valueOf(model.getValueAt(m, 2))
        );
    }

    private void load(JLabel statLabel) {
        model.setRowCount(0);
        for (Kelas k : c.getAll())
            model.addRow(new Object[]{k.getId(), k.getKodeKelas(), k.getNamaKelas()});
        if (statLabel != null)
            statLabel.setText("Total: " + model.getRowCount() + " kelas");
    }

    private void showDialog(Kelas k) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog((Frame) (owner instanceof Frame ? owner : null),
                k == null ? "Tambah Kelas" : "Edit Kelas", true);
        dlg.setLayout(new BorderLayout());
        dlg.setSize(380, 240);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(8, 0, 4, 12);
        lc.gridx = 0; lc.gridy = 0; lc.weightx = 0;

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.insets = new Insets(8, 0, 4, 0);
        fc.gridx = 1; fc.gridy = 0; fc.weightx = 1;

        JTextField kode = UIUtils.textField(15);
        JTextField nama = UIUtils.textField(20);

        if (k != null) {
            kode.setText(k.getKodeKelas());
            nama.setText(k.getNamaKelas());
        }

        // Baris 0 — Kode Kelas
        form.add(UIUtils.formLabel("Kode Kelas"), lc);
        form.add(kode, fc);

        // Baris 1 — Nama Kelas
        lc.gridy = 1; fc.gridy = 1;
        form.add(UIUtils.formLabel("Nama Kelas"), lc);
        form.add(nama, fc);

        // Tombol
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        btnPanel.setBackground(UIUtils.WHITE);
        JButton batal  = UIUtils.buttonOutline("Batal",  UIUtils.RED);
        JButton simpan = UIUtils.button("SIMPAN", UIUtils.GREEN);

        batal.addActionListener(e -> dlg.dispose());
        simpan.addActionListener(e -> {
            if (kode.getText().trim().isEmpty() || nama.getText().trim().isEmpty()) {
                UIUtils.showError(dlg, "Kode dan Nama kelas tidak boleh kosong");
                return;
            }
            Kelas val = new Kelas(k == null ? 0 : k.getId(),
                    kode.getText().trim(), nama.getText().trim());
            if (c.save(val)) {
                UIUtils.showSuccess(dlg, "Data tersimpan");
                dlg.dispose();
                load(null);
            } else {
                UIUtils.showError(dlg, "Gagal menyimpan");
            }
        });

        btnPanel.add(batal);
        btnPanel.add(simpan);

        dlg.add(form,     BorderLayout.CENTER);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // Overload untuk load tanpa update stat
    private void load() { load(null); }
}