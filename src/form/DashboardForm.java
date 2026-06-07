package form;

import controller.DashboardController;
import java.awt.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import util.UIUtils;

public class DashboardForm extends JPanel {

    private final DashboardController c = new DashboardController();

    private final JPanel saldo   = UIUtils.createKPICard("Saldo Kas",             "Rp 0", UIUtils.BLUE,   "wallet");
    private final JPanel masuk   = UIUtils.createKPICard("Pemasukan Bulan Ini",   "Rp 0", UIUtils.GREEN,  "income");
    private final JPanel keluar  = UIUtils.createKPICard("Pengeluaran Bulan Ini", "Rp 0", UIUtils.RED,    "expense");
    private final JPanel tunggak = UIUtils.createKPICard("Siswa Menunggak",       "0",    UIUtils.ORANGE, "warning");

    private final BarChartPanel barChart = new BarChartPanel();
    private final PieChartPanel pieChart = new PieChartPanel();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Tanggal", "Jenis", "Keterangan", "Nominal"}, 0);

    public DashboardForm() {
        setLayout(new BorderLayout());
        JPanel page = UIUtils.page("Dashboard");
        page.setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JButton refresh = UIUtils.button("Refresh", UIUtils.BLUE);
        refresh.setIcon(UIUtils.makeIcon("refresh", 16, UIUtils.WHITE));
        refresh.addActionListener(e -> load());
        top.add(refresh, BorderLayout.EAST);
        top.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel kpi = new JPanel(new GridLayout(1, 4, 12, 0));
        kpi.setOpaque(false);
        kpi.setPreferredSize(new Dimension(0, 90));
        kpi.add(saldo); kpi.add(masuk); kpi.add(keluar); kpi.add(tunggak);

        JPanel barCard = UIUtils.card();
        barCard.setLayout(new BorderLayout(0, 6));
        barCard.setPreferredSize(new Dimension(0, 280));
        JLabel barTitle = new JLabel("Arus Kas 6 Bulan Terakhir", SwingConstants.LEFT);
        barTitle.setFont(UIUtils.FONT_BOLD);
        barTitle.setForeground(UIUtils.TEXT_DARK);
        barCard.add(barTitle, BorderLayout.NORTH);
        barCard.add(barChart, BorderLayout.CENTER);

        JPanel pieCard = UIUtils.card();
        pieCard.setLayout(new BorderLayout(0, 6));
        pieCard.setPreferredSize(new Dimension(0, 280));
        JLabel pieTitle = new JLabel("Komposisi Keuangan Bulan Ini", SwingConstants.LEFT);
        pieTitle.setFont(UIUtils.FONT_BOLD);
        pieTitle.setForeground(UIUtils.TEXT_DARK);
        pieCard.add(pieTitle, BorderLayout.NORTH);
        pieCard.add(pieChart, BorderLayout.CENTER);

        JPanel chartRow = new JPanel(new GridLayout(1, 2, 12, 0));
        chartRow.setOpaque(false);
        chartRow.setPreferredSize(new Dimension(0, 280));
        chartRow.add(barCard);
        chartRow.add(pieCard);

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        JScrollPane tableScroll = UIUtils.tableScroll(table);
        tableScroll.setPreferredSize(new Dimension(0, 200));

        JPanel tbl = UIUtils.card();
        tbl.setLayout(new BorderLayout(0, 6));
        JLabel tblTitle = new JLabel("10 Transaksi Terbaru", SwingConstants.LEFT);
        tblTitle.setFont(UIUtils.FONT_BOLD);
        tblTitle.setForeground(UIUtils.TEXT_DARK);
        tbl.add(tblTitle, BorderLayout.NORTH);
        tbl.add(tableScroll, BorderLayout.CENTER);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(kpi);
        content.add(Box.createVerticalStrut(14));
        content.add(chartRow);
        content.add(Box.createVerticalStrut(14));
        content.add(tbl);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        page.add(top, BorderLayout.NORTH);
        page.add(scroll, BorderLayout.CENTER);
        add(page);
        load();
    }

    private void load() {
        UIUtils.updateKPICard(saldo,   UIUtils.rupiah(c.getSaldoKas()));
        UIUtils.updateKPICard(masuk,   UIUtils.rupiah(c.getPemasukanBulanIni()));
        UIUtils.updateKPICard(keluar,  UIUtils.rupiah(c.getPengeluaranBulanIni()));
        UIUtils.updateKPICard(tunggak, String.valueOf(c.getJumlahSiswaTunggak()));
        barChart.setData(c.getData6BulanTerakhir());
        pieChart.setData(c.getPemasukanBulanIni(), c.getPengeluaranBulanIni());
        model.setRowCount(0);
        for (String[] r : c.getTransaksiTerbaru(10)) model.addRow(r);
    }

    // ============================================================
    // BAR CHART
    // ============================================================
    private static class BarChartPanel extends JPanel {
        private Map<String, double[]> data;
        private int hoverGroup = -1, hoverBar = -1;
        private final java.util.List<int[]> barRects = new java.util.ArrayList<>();

        BarChartPanel() {
            setPreferredSize(new Dimension(400, 240));
            setBackground(Color.WHITE);
            setOpaque(true);
            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override public void mouseMoved(java.awt.event.MouseEvent e) {
                    int mx = e.getX(), my = e.getY();
                    int pg = hoverGroup, pb = hoverBar;
                    hoverGroup = -1; hoverBar = -1;
                    for (int[] r : barRects) {
                        if (mx >= r[2] && mx <= r[2]+r[4] && my >= r[3] && my <= r[3]+r[5]) {
                            hoverGroup = r[0]; hoverBar = r[1]; break;
                        }
                    }
                    if (hoverGroup != pg || hoverBar != pb) repaint();
                }
            });
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    hoverGroup = -1; hoverBar = -1; repaint();
                }
            });
        }

        void setData(Map<String, double[]> d) { data = d; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                g.setColor(UIUtils.TEXT_GRAY);
                g.setFont(UIUtils.FONT_PLAIN);
                g.drawString("Belum ada data", getWidth()/2 - 50, getHeight()/2);
                return;
            }
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int left = 60, right = 20, top = 40, bottom = h - 40;
            int chartW = w - left - right, chartH = bottom - top;

            double max = 1;
            for (double[] v : data.values()) max = Math.max(max, Math.max(v[0], v[1]));

            g2.setFont(UIUtils.FONT_SMALL);
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{4f}, 0f));
            for (int line = 0; line <= 4; line++) {
                int y = bottom - (int)(line / 4.0 * chartH);
                g2.setColor(new Color(226, 232, 240));
                g2.drawLine(left, y, w - right, y);
                long val = (long)(line / 4.0 * max);
                String lbl = val >= 1_000_000 ? (val/1_000_000)+"jt" : val >= 1_000 ? (val/1_000)+"rb" : String.valueOf(val);
                g2.setColor(UIUtils.TEXT_GRAY);
                g2.drawString(lbl, 2, y + 4);
            }

            g2.setStroke(new BasicStroke(1f));
            barRects.clear();
            int n = data.size();
            int groupW = chartW / Math.max(1, n);
            int barW = Math.min(22, groupW / 3);
            int gap = 4, i = 0;

            for (Map.Entry<String, double[]> e : data.entrySet()) {
                int gx  = left + i * groupW + groupW/2 - barW - gap/2;
                int bh1 = (int)(e.getValue()[0] / max * chartH);
                int bh2 = (int)(e.getValue()[1] / max * chartH);
                boolean h0 = hoverGroup == i && hoverBar == 0;
                boolean h1 = hoverGroup == i && hoverBar == 1;

                g2.setColor(h0 ? UIUtils.BLUE.darker() : UIUtils.BLUE);
                g2.fillRoundRect(gx, bottom - bh1, barW, Math.max(2, bh1), 4, 4);
                barRects.add(new int[]{i, 0, gx, bottom - bh1, barW, Math.max(2, bh1)});

                g2.setColor(h1 ? UIUtils.RED.darker() : UIUtils.RED);
                g2.fillRoundRect(gx + barW + gap, bottom - bh2, barW, Math.max(2, bh2), 4, 4);
                barRects.add(new int[]{i, 1, gx + barW + gap, bottom - bh2, barW, Math.max(2, bh2)});

                g2.setColor(UIUtils.TEXT_GRAY);
                g2.setFont(UIUtils.FONT_SMALL);
                g2.drawString(e.getKey(), gx - 2, bottom + 16);
                i++;
            }

            // Legend
            int lx = left, ly = top - 18;
            g2.setColor(UIUtils.BLUE);   g2.fillRoundRect(lx, ly, 12, 12, 4, 4);
            g2.setColor(UIUtils.TEXT_DARK); g2.drawString("Pemasukan", lx + 16, ly + 11);
            g2.setColor(UIUtils.RED);    g2.fillRoundRect(lx + 100, ly, 12, 12, 4, 4);
            g2.setColor(UIUtils.TEXT_DARK); g2.drawString("Pengeluaran", lx + 116, ly + 11);

            // Tooltip
            if (hoverGroup >= 0 && hoverBar >= 0) {
                String[] keys = data.keySet().toArray(new String[0]);
                if (hoverGroup < keys.length) {
                    double[] vals = data.get(keys[hoverGroup]);
                    double val = hoverBar == 0 ? vals[0] : vals[1];
                    String label = (hoverBar == 0 ? "Pemasukan" : "Pengeluaran")
                                 + " " + keys[hoverGroup] + ": " + UIUtils.rupiah(val);
                    int[] rect = null;
                    for (int[] r : barRects)
                        if (r[0] == hoverGroup && r[1] == hoverBar) { rect = r; break; }
                    if (rect != null)
                        drawTooltip(g2, label, rect[2] + rect[4]/2, rect[3] - 8, w, h);
                }
            }
        }

        private void drawTooltip(Graphics2D g2, String text, int x, int y, int pw, int ph) {
            FontMetrics fm = g2.getFontMetrics(UIUtils.FONT_SMALL);
            int tw = fm.stringWidth(text) + 16, th = fm.getHeight() + 10;
            int tx = Math.min(Math.max(x - tw/2, 4), pw - tw - 4);
            int ty = Math.max(y - th, 4);
            g2.setColor(new Color(15, 23, 42, 220));
            g2.fillRoundRect(tx, ty, tw, th, 8, 8);
            g2.setColor(Color.WHITE);
            g2.setFont(UIUtils.FONT_SMALL);
            g2.drawString(text, tx + 8, ty + fm.getAscent() + 4);
        }
    }

    // ============================================================
    // PIE CHART
    // ============================================================
    private static class PieChartPanel extends JPanel {
        private double pemasukan = 0, pengeluaran = 0;
        private int hoverSlice = -1;

        // Simpan info slice untuk hit-test
        private int pCx, pCy, pSize, pAngMasuk;

        PieChartPanel() {
            setPreferredSize(new Dimension(300, 240));
            setBackground(Color.WHITE);
            setOpaque(true);
            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override public void mouseMoved(java.awt.event.MouseEvent e) {
                    int prev = hoverSlice;
                    hoverSlice = getSliceAt(e.getX(), e.getY());
                    if (hoverSlice != prev) repaint();
                }
            });
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    hoverSlice = -1; repaint();
                }
            });
        }

        private int getSliceAt(int mx, int my) {
            double total = pemasukan + pengeluaran;
            if (total <= 0 || pSize == 0) return -1;
            double dx = mx - pCx, dy = my - pCy;
            double dist = Math.sqrt(dx*dx + dy*dy);
            if (dist > pSize / 2.0 || dist < pSize / 6.0) return -1;
            // Sudut dari atas (12 o'clock), searah jarum jam
            double angle = Math.toDegrees(Math.atan2(dx, -dy));
            if (angle < 0) angle += 360;
            return angle <= pAngMasuk ? 0 : 1;
        }

        void setData(double masuk, double keluar) {
            this.pemasukan = masuk; this.pengeluaran = keluar; repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            double total = pemasukan + pengeluaran;

            if (total <= 0) {
                g2.setColor(UIUtils.TEXT_GRAY);
                g2.setFont(UIUtils.FONT_PLAIN);
                g2.drawString("Belum ada data bulan ini", w/2 - 80, h/2);
                return;
            }

            int size = Math.min(w, h) - 80;
            int cx = w/2 - 20, cy = h/2 - 10;

            // Simpan untuk hit-test
            pCx = cx; pCy = cy; pSize = size;

            double pctMasuk  = pemasukan  / total;
            double pctKeluar = pengeluaran / total;
            int angMasuk  = (int) Math.round(pctMasuk  * 360);
            int angKeluar = 360 - angMasuk;
            pAngMasuk = angMasuk;

            // Hitung offset hover — geser slice ke arah luar pusat
            int off = 8;
            // Sudut tengah slice 0 (pemasukan): mulai 90°, arc = -angMasuk
            double midRad0 = Math.toRadians(90 - angMasuk / 2.0);
            // Sudut tengah slice 1 (pengeluaran)
            double midRad1 = Math.toRadians(90 - angMasuk - angKeluar / 2.0);

            int ox0 = hoverSlice == 0 ? (int)(Math.cos(midRad0) * off) : 0;
            int oy0 = hoverSlice == 0 ? -(int)(Math.sin(midRad0) * off) : 0;
            int ox1 = hoverSlice == 1 ? (int)(Math.cos(midRad1) * off) : 0;
            int oy1 = hoverSlice == 1 ? -(int)(Math.sin(midRad1) * off) : 0;

            int x0 = cx - size/2 + ox0, y0 = cy - size/2 + oy0;
            int x1 = cx - size/2 + ox1, y1 = cy - size/2 + oy1;

            g2.setColor(UIUtils.GREEN);
            g2.fillArc(x0, y0, size, size, 90, -angMasuk);
            g2.setColor(UIUtils.RED);
            g2.fillArc(x1, y1, size, size, 90 - angMasuk, -angKeluar);

            // Donut hole — gambar di posisi tengah asli (tidak ikut offset)
            int hole = size / 3;
            g2.setColor(Color.WHITE);
            g2.fillOval(cx - hole/2, cy - hole/2, hole, hole);

            // Teks tengah
            g2.setColor(UIUtils.TEXT_DARK);
            g2.setFont(UIUtils.FONT_BOLD);
            String pctStr = String.format("%.0f%%", pctMasuk * 100);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(pctStr, cx - fm.stringWidth(pctStr)/2, cy - 2);
            g2.setFont(UIUtils.FONT_SMALL);
            g2.setColor(UIUtils.TEXT_GRAY);
            g2.drawString("masuk", cx - 14, cy + 14);

            // Legend
            int ly = cy + size/2 + 16;
            g2.setFont(UIUtils.FONT_SMALL);
            g2.setColor(UIUtils.GREEN);  g2.fillRoundRect(cx - 90, ly, 12, 12, 4, 4);
            g2.setColor(UIUtils.TEXT_DARK);
            g2.drawString(String.format("Pemasukan %.0f%%", pctMasuk*100), cx - 74, ly + 11);
            g2.setColor(UIUtils.RED);    g2.fillRoundRect(cx + 20, ly, 12, 12, 4, 4);
            g2.setColor(UIUtils.TEXT_DARK);
            g2.drawString(String.format("Pengeluaran %.0f%%", pctKeluar*100), cx + 36, ly + 11);

            // Tooltip
            if (hoverSlice >= 0) {
                double val = hoverSlice == 0 ? pemasukan : pengeluaran;
                String label = (hoverSlice == 0 ? "Pemasukan" : "Pengeluaran")
                             + ": " + UIUtils.rupiah(val);
                FontMetrics fm2 = g2.getFontMetrics(UIUtils.FONT_SMALL);
                int tw = fm2.stringWidth(label) + 16, th = fm2.getHeight() + 10;
                int tx = Math.min(Math.max(cx - tw/2, 4), w - tw - 4);
                int ty = Math.max(cy - size/2 - th - 8, 4);
                g2.setColor(new Color(15, 23, 42, 220));
                g2.fillRoundRect(tx, ty, tw, th, 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(UIUtils.FONT_SMALL);
                g2.drawString(label, tx + 8, ty + fm2.getAscent() + 4);
            }
        }
    }
}