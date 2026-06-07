package form;

import controller.AuthController;
import controller.TabunganController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Siswa;
import model.Tabungan;
import util.UIUtils;

public class TabunganForm extends JPanel {

    private final TabunganController c = new TabunganController();
    private Siswa selected;

    // Info siswa
    private final JLabel labelNama   = new JLabel("—");
    private final JLabel labelNis    = new JLabel("—");
    private final JLabel labelSaldo  = new JLabel("Rp 0");

    // Input
    private final JTextField q       = UIUtils.textField(22);
    private final JTextField nominal = UIUtils.textField(14);
    private final JTextArea  ket     = new JTextArea(3, 30);

    // Toggle setor/tarik
    private final JToggleButton setor = new JToggleButton("⬆ SETOR", true);
    private final JToggleButton tarik = new JToggleButton("⬇ TARIK");

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Tanggal", "Jenis", "Nominal", "Saldo Akhir", "Keterangan"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public TabunganForm() {
        setLayout(new BorderLayout());
        JPanel page = UIUtils.page("Tabungan");
        page.setLayout(new BorderLayout(0, 14));

        // ── Panel Cari Siswa ──────────────────────────────────
        JPanel cariCard = UIUtils.card();
        cariCard.setLayout(new BorderLayout(0, 10));

        JLabel cariTitle = new JLabel("Cari Siswa");
        cariTitle.setFont(UIUtils.FONT_BOLD);
        cariTitle.setForeground(UIUtils.TEXT_DARK);

        JButton btnCari = UIUtils.button("CARI", UIUtils.BLUE);
        btnCari.addActionListener(e -> search());

        q.addActionListener(e -> search()); // Enter di field langsung cari

        JPanel cariRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        cariRow.setOpaque(false);
        cariRow.add(UIUtils.formLabel("Nama / NIS"));
        cariRow.add(q);
        cariRow.add(btnCari);

        cariCard.add(cariTitle, BorderLayout.NORTH);
        cariCard.add(cariRow,   BorderLayout.CENTER);

        // ── Panel Info Siswa ──────────────────────────────────
        JPanel infoCard = UIUtils.card();
        infoCard.setLayout(new GridLayout(1, 3, 16, 0));

        infoCard.add(makeInfoBlock("Nama Siswa", labelNama));
        infoCard.add(makeInfoBlock("NIS",        labelNis));

        // Saldo — highlight warna
        JPanel saldoBlock = new JPanel(new BorderLayout(0, 4));
        saldoBlock.setOpaque(false);
        JLabel saldoTitle = UIUtils.formLabel("Saldo Tabungan");
        labelSaldo.setFont(UIUtils.FONT_H2);
        labelSaldo.setForeground(UIUtils.GREEN);
        saldoBlock.add(saldoTitle,  BorderLayout.NORTH);
        saldoBlock.add(labelSaldo,  BorderLayout.CENTER);
        infoCard.add(saldoBlock);

        // ── Panel Transaksi ───────────────────────────────────
        JPanel trxCard = UIUtils.card();
        trxCard.setLayout(new BorderLayout(0, 10));

        JLabel trxTitle = new JLabel("Input Transaksi");
        trxTitle.setFont(UIUtils.FONT_BOLD);
        trxTitle.setForeground(UIUtils.TEXT_DARK);

        // Style toggle button
        styleToggle(setor, UIUtils.GREEN);
        styleToggle(tarik, UIUtils.RED);
        ButtonGroup bg = new ButtonGroup();
        bg.add(setor);
        bg.add(tarik);

        setor.addActionListener(e -> updateToggleStyle());
        tarik.addActionListener(e -> updateToggleStyle());

        JPanel toggleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toggleRow.setOpaque(false);
        toggleRow.add(setor);
        toggleRow.add(tarik);

        JPanel inputGrid = new JPanel(new GridBagLayout());
        inputGrid.setOpaque(false);
        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(6, 0, 4, 12);
        lc.gridx = 0; lc.gridy = 0; lc.weightx = 0;
        GridBagConstraints fc = new GridBagConstraints();
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.insets = new Insets(6, 0, 4, 0);
        fc.gridx = 1; fc.gridy = 0; fc.weightx = 1;

        // Baris 0 — Jenis
        inputGrid.add(UIUtils.formLabel("Jenis Transaksi"), lc);
        inputGrid.add(toggleRow, fc);

        // Baris 1 — Nominal
        lc.gridy = 1; fc.gridy = 1;
        inputGrid.add(UIUtils.formLabel("Nominal"), lc);
        inputGrid.add(nominal, fc);

        // Baris 2 — Keterangan
        lc.gridy = 2; fc.gridy = 2; fc.ipady = 10;
        ket.setFont(UIUtils.FONT_PLAIN);
        ket.setLineWrap(true);
        ket.setWrapStyleWord(true);
        ket.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        inputGrid.add(UIUtils.formLabel("Keterangan"), lc);
        inputGrid.add(new JScrollPane(ket), fc);

        JButton btnProses = UIUtils.button("PROSES TRANSAKSI", UIUtils.GREEN);
        btnProses.setFont(UIUtils.FONT_BOLD);
        btnProses.addActionListener(e -> process());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnProses);

        trxCard.add(trxTitle,  BorderLayout.NORTH);
        trxCard.add(inputGrid, BorderLayout.CENTER);
        trxCard.add(btnRow,    BorderLayout.SOUTH);

        // ── Panel Mutasi ──────────────────────────────────────
        JPanel mutasiCard = UIUtils.card();
        mutasiCard.setLayout(new BorderLayout(0, 8));
        JLabel mutasiTitle = new JLabel("Riwayat Mutasi");
        mutasiTitle.setFont(UIUtils.FONT_BOLD);
        mutasiTitle.setForeground(UIUtils.TEXT_DARK);
        mutasiCard.add(mutasiTitle, BorderLayout.NORTH);
        mutasiCard.add(UIUtils.tableScroll(table), BorderLayout.CENTER);

        // ── Susun layout ──────────────────────────────────────
        JPanel topSection = new JPanel(new GridLayout(1, 2, 14, 0));
        topSection.setOpaque(false);

        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);
        leftCol.add(cariCard);
        leftCol.add(Box.createVerticalStrut(14));
        leftCol.add(infoCard);

        topSection.add(leftCol);
        topSection.add(trxCard);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(topSection);
        content.add(Box.createVerticalStrut(14));
        content.add(mutasiCard);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        page.add(scroll, BorderLayout.CENTER);
        add(page);

        updateToggleStyle();
    }

    private JPanel makeInfoBlock(String title, JLabel value) {
        JPanel block = new JPanel(new BorderLayout(0, 4));
        block.setOpaque(false);
        value.setFont(UIUtils.FONT_BOLD);
        value.setForeground(UIUtils.TEXT_DARK);
        block.add(UIUtils.formLabel(title), BorderLayout.NORTH);
        block.add(value, BorderLayout.CENTER);
        return block;
    }

    private void styleToggle(JToggleButton btn, Color color) {
        btn.setFocusPainted(false);
        btn.setFont(UIUtils.FONT_BOLD);
        btn.setPreferredSize(new Dimension(110, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        btn.setBackground(Color.WHITE);
        btn.setForeground(color);
    }

    private void updateToggleStyle() {
        if (setor.isSelected()) {
            setor.setBackground(UIUtils.GREEN);
            setor.setForeground(Color.WHITE);
            tarik.setBackground(Color.WHITE);
            tarik.setForeground(UIUtils.RED);
        } else {
            tarik.setBackground(UIUtils.RED);
            tarik.setForeground(Color.WHITE);
            setor.setBackground(Color.WHITE);
            setor.setForeground(UIUtils.GREEN);
        }
    }

    private void search() {
        List<Siswa> list = c.searchSiswa(q.getText());
        if (list.isEmpty()) {
            UIUtils.showError(this, "Siswa tidak ditemukan");
            return;
        }
        selected = list.get(0);
        load();
    }

    private void load() {
        model.setRowCount(0);
        double saldo = c.getSaldoSiswa(selected.getId());
        labelNama.setText(selected.getNamaSiswa());
        labelNis.setText(selected.getNis());
        labelSaldo.setText(UIUtils.rupiah(saldo));
        for (Tabungan t : c.getMutasiSiswa(selected.getId()))
            model.addRow(new Object[]{
                t.getTanggal(), t.getJenis(),
                UIUtils.rupiah(t.getNominal()),
                UIUtils.rupiah(t.getSaldoAkhir()),
                t.getKeterangan()
            });
    }

    private void process() {
        if (selected == null) { UIUtils.showError(this, "Pilih siswa dahulu"); return; }
        double n = UIUtils.parseNumber(nominal.getText());
        int uid = AuthController.getCurrentUser() == null
                ? 0 : AuthController.getCurrentUser().getId();
        boolean ok = setor.isSelected()
                ? c.setor(selected.getId(), n, ket.getText(), uid)
                : c.tarik(selected.getId(), n, ket.getText(), uid);
        if (ok) {
            UIUtils.showSuccess(this, "Transaksi berhasil");
            nominal.setText("");
            ket.setText("");
            load();
        } else {
            UIUtils.showError(this, "Transaksi gagal / saldo tidak cukup");
        }
    }
}