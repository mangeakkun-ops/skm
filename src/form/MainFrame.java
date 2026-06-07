package form;

import controller.AuthController;
import controller.PengaturanController;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import model.Pengaturan;
import model.User;
import util.UIUtils;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final Map<String, JButton> buttons = new LinkedHashMap<>();
    private final User user;
    private final JLabel clock = new JLabel();

    public MainFrame(User user) {
        this.user = user;
        Pengaturan p = new PengaturanController().getPengaturan();
        setTitle("SKM - Sistem Keuangan Madrasah");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 600));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        add(sidebar(p), BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        add(statusBar(), BorderLayout.SOUTH);
        addPages();
        showPage("Dashboard");
        new Timer(1000, e -> clock.setText(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))).start();
    }

    private JPanel sidebar(Pengaturan p) {
        JPanel side = new JPanel(new BorderLayout());
        side.setPreferredSize(new Dimension(220, 1));
        side.setBackground(UIUtils.SIDEBAR_BG);

        // Header
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(22, 12, 20, 12));
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        header.add(UIUtils.schoolLogo(56), gc);
        gc.gridy++;
        JLabel name = new JLabel("<html><center>" + p.getNamaSekolah() + "</center></html>");
        name.setForeground(UIUtils.WHITE);
        name.setFont(UIUtils.FONT_BOLD);
        header.add(name, gc);
        side.add(header, BorderLayout.NORTH);

        // Menu
        JPanel menu = new JPanel(new GridLayout(0, 1, 0, 4));
        menu.setOpaque(false);
        menu.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        addMenu(menu, "Dashboard",   "Dashboard",   "dashboard");
        addMenu(menu, "Siswa",       "Siswa",       "students");
        addMenu(menu, "Kelas",       "Kelas",       "class");
        addMenu(menu, "Pemasukan",   "Pemasukan",   "income");
        addMenu(menu, "Pengeluaran", "Pengeluaran", "expense");
        addMenu(menu, "Tabungan",    "Tabungan",    "savings");
        addMenu(menu, "Laporan",     "Laporan",     "report");
        addMenu(menu, "Pengaturan",  "Pengaturan",  "settings");

        side.add(menu, BorderLayout.CENTER);

        // Logout
        JButton logout = new JButton("LOGOUT");
        logout.setFont(UIUtils.FONT_BOLD);
        logout.setForeground(UIUtils.WHITE);
        logout.setBackground(UIUtils.RED);
        logout.setOpaque(true);
        logout.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        logout.setFocusPainted(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.setIcon(UIUtils.makeIcon("logout", 16, UIUtils.WHITE));
        logout.setIconTextGap(8);
        logout.setHorizontalAlignment(SwingConstants.LEFT);
        logout.addActionListener(e -> {
            new AuthController().logout();
            new LoginFrame().setVisible(true);
            dispose();
        });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(12, 12, 20, 12));
        bottom.add(logout);
        side.add(bottom, BorderLayout.SOUTH);

        return side;
    }

    private void addMenu(JPanel menu, String key, String text, String iconType) {
        JButton b = new JButton(text);
        b.setFont(UIUtils.FONT_BOLD);
        b.setForeground(UIUtils.WHITE);
        b.setBackground(UIUtils.SIDEBAR_BG);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setIcon(UIUtils.makeIcon(iconType, 18, UIUtils.WHITE));
        b.setIconTextGap(10);
        b.addActionListener(e -> showPage(key));
        buttons.put(key, b);
        menu.add(b);
    }

    private void addPages() {
        content.add(new DashboardForm(),   "Dashboard");
        content.add(new SiswaForm(),       "Siswa");
        content.add(new KelasForm(),       "Kelas");
        content.add(new PemasukanForm(),   "Pemasukan");
        content.add(new PengeluaranForm(), "Pengeluaran");
        content.add(new TabunganForm(),    "Tabungan");
        content.add(new LaporanForm(),     "Laporan");
        content.add(new PengaturanForm(),  "Pengaturan");
    }

    private void showPage(String key) {
        buttons.forEach((k, b) -> {
            boolean active = k.equals(key);
            b.setBackground(active ? UIUtils.BLUE : UIUtils.SIDEBAR_BG);
            b.setIcon(UIUtils.makeIcon(iconForKey(k), 18,
                active ? UIUtils.WHITE : new Color(148, 163, 184)));
        });
        cardLayout.show(content, key);
    }

    private String iconForKey(String key) {
        return switch (key) {
            case "Dashboard"   -> "dashboard";
            case "Siswa"       -> "students";
            case "Kelas"       -> "class";
            case "Pemasukan"   -> "income";
            case "Pengeluaran" -> "expense";
            case "Tabungan"    -> "savings";
            case "Laporan"     -> "report";
            case "Pengaturan"  -> "settings";
            default            -> "dashboard";
        };
    }

    private JPanel statusBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        p.setBackground(UIUtils.WHITE);
        JLabel u = new JLabel("User: " + user.getNama() + " (" + user.getRole() + ")");
        u.setFont(UIUtils.FONT_SMALL);
        clock.setFont(UIUtils.FONT_SMALL);
        p.add(u, BorderLayout.WEST);
        p.add(clock, BorderLayout.EAST);
        return p;
    }
}