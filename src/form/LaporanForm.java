package form;

import controller.LaporanController;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import util.UIUtils;

public class LaporanForm extends JPanel {

    private final LaporanController c = new LaporanController();
    private final DefaultTableModel kasModel  = new DefaultTableModel(
            new String[]{"Tanggal", "Keterangan", "Debit", "Kredit", "Saldo"}, 0);
    private final DefaultTableModel tungModel = new DefaultTableModel(
            new String[]{"NIS", "Nama", "Kelas", "Bulan", "Nominal"}, 0);

    public LaporanForm() {
        setLayout(new BorderLayout());
        JPanel p = UIUtils.page("Laporan");
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Buku Kas Umum", bukuKas());
        tabs.addTab("Tunggakan SPP", tunggakan());
        p.add(tabs);
        add(p);
    }

    private JPanel bukuKas() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);

        JTextField dari   = UIUtils.textField(10);
        JTextField sampai = UIUtils.textField(10);
        dari.setText(LocalDate.now().withDayOfMonth(1).toString());
        sampai.setText(LocalDate.now().toString());

        JButton show  = UIUtils.button("TAMPILKAN",         UIUtils.BLUE);
        JButton csv   = UIUtils.buttonOutline("Export CSV",   UIUtils.GREEN);
        JButton excel = UIUtils.buttonOutline("Export Excel", new Color(31, 120, 50));
        JButton pdf   = UIUtils.buttonOutline("Export PDF",   UIUtils.RED);

        JTable table = new JTable(kasModel);
        p.add(UIUtils.toolbar(dari, sampai, show, csv, excel, pdf), BorderLayout.NORTH);
        p.add(UIUtils.tableScroll(table));

        show.addActionListener(e -> {
            kasModel.setRowCount(0);
            for (String[] r : c.getBukuKas(
                    LocalDate.parse(dari.getText()),
                    LocalDate.parse(sampai.getText())))
                kasModel.addRow(r);
        });

        csv.addActionListener(e   -> exportCSV(kasModel,   "buku_kas.csv"));
        excel.addActionListener(e -> exportExcel(kasModel, "buku_kas.xlsx",  "Buku Kas Umum"));
        pdf.addActionListener(e   -> exportPDF(kasModel,   "buku_kas.pdf",   "Buku Kas Umum"));

        show.doClick();
        return p;
    }

    private JPanel tunggakan() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);

        JComboBox<Integer> bulan = new JComboBox<>();
        for (int i = 1; i <= 12; i++) bulan.addItem(i);
        bulan.setSelectedItem(LocalDate.now().getMonthValue()); // default bulan ini

        JComboBox<Integer> tahun = new JComboBox<>();
        for (int i = 2024; i <= 2030; i++) tahun.addItem(i);
        tahun.setSelectedItem(LocalDate.now().getYear());

        JButton show  = UIUtils.button("TAMPILKAN",         UIUtils.BLUE);
        JButton csv   = UIUtils.buttonOutline("Export CSV",   UIUtils.GREEN);
        JButton excel = UIUtils.buttonOutline("Export Excel", new Color(31, 120, 50));
        JButton pdf   = UIUtils.buttonOutline("Export PDF",   UIUtils.RED);

        JTable table = new JTable(tungModel);
        p.add(UIUtils.toolbar(bulan, tahun, show, csv, excel, pdf), BorderLayout.NORTH);
        p.add(UIUtils.tableScroll(table));

        show.addActionListener(e -> {
            tungModel.setRowCount(0);
            for (String[] r : c.getTunggakanSPP(
                    (Integer) bulan.getSelectedItem(),
                    (Integer) tahun.getSelectedItem()))
                tungModel.addRow(r);
        });

        csv.addActionListener(e   -> exportCSV(tungModel,   "tunggakan.csv"));
        excel.addActionListener(e -> exportExcel(tungModel, "tunggakan.xlsx", "Tunggakan SPP"));
        pdf.addActionListener(e   -> exportPDF(tungModel,   "tunggakan.pdf",  "Tunggakan SPP"));

        show.doClick();
        return p;
    }

    // ── Export CSV ──────────────────────────────────────────────
    private void exportCSV(DefaultTableModel m, String defaultName) {
        File file = chooseSaveFile(defaultName);
        if (file == null) return;
        boolean ok = c.exportCSV(toData(m), toHeaders(m), file);
        if (ok) UIUtils.showSuccess(this, "Export CSV berhasil");
        else    UIUtils.showError(this,   "Export CSV gagal");
    }

    // ── Export PDF ──────────────────────────────────────────────
    private void exportPDF(DefaultTableModel m, String defaultName, String judul) {
        File file = chooseSaveFile(defaultName);
        if (file == null) return;
        boolean ok = c.exportPDF(toData(m), toHeaders(m), judul, file);
        if (ok) UIUtils.showSuccess(this, "Export PDF berhasil");
        else    UIUtils.showError(this,   "Export PDF gagal");
    }

    // ── Export Excel ────────────────────────────────────────────
    private void exportExcel(DefaultTableModel m, String defaultName, String judul) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(System.getProperty("user.home") + "/Downloads/" + defaultName));
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Excel Files (*.xlsx)", "xlsx"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".xlsx"))
            file = new File(file.getAbsolutePath() + ".xlsx");

        final File finalFile = file;

        UIUtils.showSuccess(this, "Sedang mengexport, harap tunggu...");
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try (org.apache.poi.xssf.usermodel.XSSFWorkbook wb =
                             new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {

                    org.apache.poi.xssf.usermodel.XSSFSheet sheet = wb.createSheet(judul);

                    // Style header
                    org.apache.poi.xssf.usermodel.XSSFCellStyle headerStyle = wb.createCellStyle();
                    org.apache.poi.xssf.usermodel.XSSFFont headerFont = wb.createFont();
                    headerFont.setBold(true);
                    headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
                    headerStyle.setFont(headerFont);
                    headerStyle.setFillForegroundColor(
                            new org.apache.poi.xssf.usermodel.XSSFColor(
                                    new byte[]{(byte) 31, (byte) 78, (byte) 121}, null));
                    headerStyle.setFillPattern(
                            org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
                    headerStyle.setAlignment(
                            org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
                    headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                    headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                    headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

                    // Style data biasa
                    org.apache.poi.xssf.usermodel.XSSFCellStyle dataStyle = wb.createCellStyle();
                    dataStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                    dataStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
                    dataStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

                    // Style zebra
                    org.apache.poi.xssf.usermodel.XSSFCellStyle zebraStyle = wb.createCellStyle();
                    zebraStyle.cloneStyleFrom(dataStyle);
                    zebraStyle.setFillForegroundColor(
                            new org.apache.poi.xssf.usermodel.XSSFColor(
                                    new byte[]{(byte) 235, (byte) 241, (byte) 250}, null));
                    zebraStyle.setFillPattern(
                            org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

                    // Judul
                    org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(0);
                    org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
                    titleCell.setCellValue(judul);
                    org.apache.poi.xssf.usermodel.XSSFCellStyle titleStyle = wb.createCellStyle();
                    org.apache.poi.xssf.usermodel.XSSFFont titleFont = wb.createFont();
                    titleFont.setBold(true);
                    titleFont.setFontHeightInPoints((short) 14);
                    titleStyle.setFont(titleFont);
                    titleCell.setCellStyle(titleStyle);
                    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                            0, 0, 0, m.getColumnCount() - 1));

                    // Tanggal cetak
                    org.apache.poi.ss.usermodel.Row dateRow = sheet.createRow(1);
                    dateRow.createCell(0).setCellValue("Dicetak: " + LocalDate.now());

                    // Header kolom
                    org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(3);
                    for (int col = 0; col < m.getColumnCount(); col++) {
                        org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(col);
                        cell.setCellValue(m.getColumnName(col));
                        cell.setCellStyle(headerStyle);
                    }

                    // Data
                    for (int row = 0; row < m.getRowCount(); row++) {
                        org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(row + 4);
                        for (int col = 0; col < m.getColumnCount(); col++) {
                            org.apache.poi.ss.usermodel.Cell cell = dataRow.createCell(col);
                            cell.setCellValue(String.valueOf(m.getValueAt(row, col)));
                            cell.setCellStyle(row % 2 == 0 ? dataStyle : zebraStyle);
                        }
                    }

                    // Lebar kolom manual
                    int[] colWidths = {3500, 8000, 4000, 4000, 4000};
                    for (int col = 0; col < m.getColumnCount(); col++) {
                        int width = col < colWidths.length ? colWidths[col] : 4000;
                        sheet.setColumnWidth(col, width);
                    }

                    try (java.io.FileOutputStream fos = new java.io.FileOutputStream(finalFile)) {
                        wb.write(fos);
                    }
                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok)
                        UIUtils.showSuccess(LaporanForm.this,
                                "Export Excel berhasil!\n" + finalFile.getAbsolutePath());
                    else
                        UIUtils.showError(LaporanForm.this, "Export Excel gagal!");
                } catch (Exception e) {
                    UIUtils.showError(LaporanForm.this, "Export Excel gagal: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    // ── Helper ──────────────────────────────────────────────────
    private File chooseSaveFile(String defaultName) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(defaultName));
        return fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION
                ? fc.getSelectedFile() : null;
    }

    private List<String[]> toData(DefaultTableModel m) {
        List<String[]> data = new java.util.ArrayList<>();
        for (int r = 0; r < m.getRowCount(); r++) {
            String[] row = new String[m.getColumnCount()];
            for (int col = 0; col < m.getColumnCount(); col++)
                row[col] = String.valueOf(m.getValueAt(r, col));
            data.add(row);
        }
        return data;
    }

    private String[] toHeaders(DefaultTableModel m) {
        String[] h = new String[m.getColumnCount()];
        for (int i = 0; i < h.length; i++) h[i] = m.getColumnName(i);
        return h;
    }
}