package util;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

public final class UIUtils {
    public static final Color NAVY       = new Color(15, 23, 42);
    public static final Color SIDEBAR_BG = new Color(20, 30, 55);
    public static final Color BLUE       = new Color(37, 99, 235);
    public static final Color CYAN       = new Color(6, 182, 212);
    public static final Color GREEN      = new Color(34, 197, 94);
    public static final Color ORANGE     = new Color(249, 115, 22);
    public static final Color RED        = new Color(239, 68, 68);
    public static final Color MUTED      = new Color(100, 116, 139);
    public static final Color WHITE      = Color.WHITE;
    public static final Color SUCCESS    = new Color(34, 197, 94);
    public static final Color DANGER     = new Color(239, 68, 68);
    public static final Color WARNING    = new Color(234, 179, 8);
    public static final Color ACCENT     = new Color(139, 92, 246);
    public static final Color BACKGROUND = new Color(241, 245, 249);
    public static final Color CARD_BG    = new Color(255, 255, 255);
    public static final Color BORDER     = new Color(226, 232, 240);
    public static final Color TEXT_DARK  = new Color(15, 23, 42);
    public static final Color TEXT_GRAY  = new Color(100, 116, 139);

    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_H2     = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_PLAIN  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO   = new Font("Consolas", Font.PLAIN, 13);

    private UIUtils() {}

    // ═══════════════════════════════════════════════════════════
    // ICON GENERATOR — render 4x lalu scale down (smooth)
    // ═══════════════════════════════════════════════════════════
    public static ImageIcon makeIcon(String type, int size, Color color) {
    try {
        java.io.InputStream is = UIUtils.class.getResourceAsStream("/icons/" + type + ".png");
        if (is == null) return makeIconFallback(size, color);
        BufferedImage orig = javax.imageio.ImageIO.read(is);
        BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(orig, 0, 0, size, size, null);
        g2.dispose();
        return new ImageIcon(scaled);
    } catch (Exception e) {
        return makeIconFallback(size, color);
    }
}

private static ImageIcon makeIconFallback(int size, Color color) {
    BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = img.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(color);
    g.fillOval(2, 2, size - 4, size - 4);
    g.dispose();
    return new ImageIcon(img);
}


    // ═══════════════════════════════════════════════════════════
    // BUTTON DENGAN ICON
    // ═══════════════════════════════════════════════════════════
    public static JButton buttonIcon(String text, Color bg, String iconType) {
        JButton b = button(text, bg);
        ImageIcon ic = makeIcon(iconType, 16, WHITE);
        if (ic != null) { b.setIcon(ic); b.setIconTextGap(6); }
        return b;
    }

    public static JButton buttonOutlineIcon(String text, Color color, String iconType) {
        JButton b = buttonOutline(text, color);
        ImageIcon ic = makeIcon(iconType, 16, color);
        if (ic != null) { b.setIcon(ic); b.setIconTextGap(6); }
        return b;
    }

    // ═══════════════════════════════════════════════════════════
    // KPI CARD
    // ═══════════════════════════════════════════════════════════
    public static JPanel createKPICard(String title, String value, Color accent, String iconType) {
        JPanel outer = card();
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(7, 1));
        outer.add(accentBar, BorderLayout.WEST);

        JPanel texts = new JPanel(new GridLayout(2, 1, 0, 6));
        texts.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        titleRow.setOpaque(false);
        if (iconType != null) {
            ImageIcon ic = makeIcon(iconType, 14, TEXT_GRAY);
            titleRow.add(new JLabel(ic));
        }
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_SMALL);
        titleLabel.setForeground(TEXT_GRAY);
        titleRow.add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setName("kpiValue");
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(TEXT_DARK);

        texts.add(titleRow);
        texts.add(valueLabel);
        outer.add(texts, BorderLayout.CENTER);
        return outer;
    }

    public static JPanel createKPICard(String title, String value, Color accent) {
        return createKPICard(title, value, accent, null);
    }

    public static void updateKPICard(JPanel card, String newValue) {
        JLabel label = findLabelByName(card, "kpiValue");
        if (label != null) label.setText(newValue);
    }

    private static JLabel findLabelByName(Container c, String name) {
        for (Component child : c.getComponents()) {
            if (child instanceof JLabel && name.equals(child.getName())) return (JLabel) child;
            if (child instanceof Container) {
                JLabel found = findLabelByName((Container) child, name);
                if (found != null) return found;
            }
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════════
    // KOMPONEN DASAR
    // ═══════════════════════════════════════════════════════════
    public static class Option {
        public int id;
        public String label;
        public Option(int id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    public static String rupiah(double v) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        return nf.format(v).replace("Rp", "Rp ").replace(",", ".").trim();
    }

    public static JLabel formLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_PLAIN);
        label.setForeground(TEXT_DARK);
        return label;
    }

    public static JTextField textField(int cols) {
        JTextField field = new JTextField(cols);
        styleText(field);
        return field;
    }

    public static JPasswordField passwordField(int cols) {
        JPasswordField field = new JPasswordField(cols);
        styleText(field);
        return field;
    }

    private static void styleText(JTextField field) {
        field.setFont(FONT_PLAIN);
        field.setForeground(TEXT_DARK);
        field.setBackground(WHITE);
        field.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                field.setBorder(new CompoundBorder(new LineBorder(BLUE, 1, true), new EmptyBorder(8, 12, 8, 12)));
            }
            @Override public void focusLost(FocusEvent e) {
                field.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(8, 12, 8, 12)));
            }
        });
    }

    public static JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(FONT_BOLD);
        b.setForeground(WHITE);
        b.setBackground(bg);
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(9, 16, 9, 16));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(darken(bg)); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    public static JButton buttonOutline(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(FONT_BOLD);
        b.setForeground(color);
        b.setBackground(WHITE);
        b.setOpaque(true);
        b.setBorder(new CompoundBorder(new LineBorder(color, 1, true), new EmptyBorder(8, 15, 8, 15)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                b.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 24));
            }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(WHITE); }
        });
        return b;
    }

    public static JPanel page(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(BACKGROUND);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel t = new JLabel(title);
        t.setFont(FONT_TITLE);
        t.setForeground(TEXT_DARK);
        p.add(t, BorderLayout.NORTH);
        return p;
    }

    public static JPanel card() {
        JPanel p = new RoundedPanel(16);
        p.setLayout(new BorderLayout(10, 10));
        p.setBackground(CARD_BG);
        p.setBorder(new CompoundBorder(new ShadowBorder(), new EmptyBorder(16, 16, 16, 16)));
        return p;
    }

    public static JScrollPane tableScroll(JTable table) {
        table.setFont(FONT_PLAIN);
        table.setRowHeight(32);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_DARK);
        table.setGridColor(BORDER);
        table.setShowVerticalLines(false);
        JTableHeader header = table.getTableHeader();
        header.setBackground(NAVY);
        header.setForeground(WHITE);
        header.setFont(FONT_BOLD);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setBackground(r % 2 == 0 ? WHITE : new Color(248, 250, 252));
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return comp;
            }
        });
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new LineBorder(BORDER, 1, true));
        sp.getViewport().setBackground(WHITE);
        return sp;
    }

    public static JPanel toolbar(JComponent... components) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        for (JComponent c : components) p.add(c);
        return p;
    }

    public static void bindSearch(JTextField field, TableRowSorter<?> sorter) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            private void apply() {
                String q = field.getText().trim();
                sorter.setRowFilter(q.isEmpty() ? null :
                    RowFilter.regexFilter("(?i)" + Pattern.quote(q)));
            }
            @Override public void insertUpdate(DocumentEvent e)  { apply(); }
            @Override public void removeUpdate(DocumentEvent e)  { apply(); }
            @Override public void changedUpdate(DocumentEvent e) { apply(); }
        });
    }

    public static JLabel schoolLogo(int size) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(BLUE);
        g.fillRoundRect(4, size / 3, size - 8, size / 2, 12, 12);
        g.setColor(WHITE);
        g.fillRect(size / 2 - size / 12, size / 2, size / 6, size / 3);
        g.setColor(CYAN);
        g.fillOval(size / 2 - size / 5, size / 8, size * 2 / 5, size * 2 / 5);
        g.setColor(NAVY);
        int[] xs = {size / 2, size / 8, size * 7 / 8};
        int[] ys = {size / 5, size / 2, size / 2};
        g.fillPolygon(xs, ys, 3);
        g.dispose();
        return new JLabel(new ImageIcon(img));
    }

    public static JComboBox<Option> comboBox(Option[] options) {
        JComboBox<Option> cb = new JComboBox<>(options);
        cb.setFont(FONT_PLAIN);
        cb.setBackground(WHITE);
        cb.setBorder(new LineBorder(BORDER, 1, true));
        return cb;
    }

    public static JPanel sectionTitle(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(title);
        l.setFont(FONT_H2);
        l.setForeground(TEXT_DARK);
        p.add(l, BorderLayout.NORTH);
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        p.add(sep, BorderLayout.SOUTH);
        p.setBorder(new EmptyBorder(8, 0, 8, 0));
        return p;
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    public static boolean showConfirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Konfirmasi",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static double parseNumber(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        return Double.parseDouble(
            text.replace("Rp", "").replace(".", "").replace(",", ".").trim());
    }

    public static String todayString() { return java.time.LocalDate.now().toString(); }

    public static Color darken(Color c) {
        return new Color(Math.max(0, c.getRed() - 24),
                         Math.max(0, c.getGreen() - 24),
                         Math.max(0, c.getBlue() - 24));
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        RoundedPanel(int radius) { this.radius = radius; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class ShadowBorder extends AbstractBorder {
        @Override public void paintBorder(Component c, Graphics g,
                int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(203, 213, 225));
            g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, 16, 16);
            g2.setColor(new Color(15, 23, 42, 14));
            g2.drawRoundRect(x + 3, y + 3, width - 7, height - 7, 16, 16);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(4, 4, 6, 6); }
        @Override public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(4, 4, 6, 6); return insets;
        }
    }
}