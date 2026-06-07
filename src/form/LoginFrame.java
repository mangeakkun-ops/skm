package form;

import controller.AuthController;
import java.awt.*;
import javax.swing.*;
import model.User;
import util.UIUtils;

public class LoginFrame extends JFrame {
    private final JTextField username = UIUtils.textField(22);
    private final JPasswordField password = UIUtils.passwordField(22);
    private final JLabel status = new JLabel(" ");
    private final JButton masuk = UIUtils.button("MASUK", UIUtils.BLUE);
    private final AuthController auth = new AuthController();

    public LoginFrame() {
        setTitle("SKM - Sistem Keuangan Madrasah");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, branding(), formPanel());
        split.setDividerLocation(390);
        split.setEnabled(false);
        split.setBorder(null);
        setContentPane(split);
        getRootPane().setDefaultButton(masuk);
        masuk.addActionListener(e -> doLogin());
    }

    private JPanel branding() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UIUtils.NAVY);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0; gc.insets = new Insets(10,10,18,10);
        p.add(UIUtils.schoolLogo(120), gc);
        gc.gridy++;
        JLabel title = new JLabel("SKM"); title.setFont(new Font("Segoe UI", Font.BOLD, 42)); title.setForeground(UIUtils.WHITE); p.add(title, gc);
        gc.gridy++;
        JLabel sub = new JLabel("Sistem Keuangan Madrasah"); sub.setFont(UIUtils.FONT_H2); sub.setForeground(new Color(203,213,225)); p.add(sub, gc);
        gc.gridy++;
        JLabel tag = new JLabel("Modern • Aman • Terintegrasi"); tag.setFont(UIUtils.FONT_PLAIN); tag.setForeground(new Color(148,163,184)); p.add(tag, gc);
        return p;
    }

    private JPanel formPanel() {
        JPanel outer = new JPanel(new GridBagLayout()); outer.setBackground(UIUtils.BACKGROUND);
        JPanel card = UIUtils.card(); card.setLayout(new GridBagLayout()); card.setPreferredSize(new Dimension(380, 360));
        GridBagConstraints gc = new GridBagConstraints(); gc.gridx=0; gc.fill=GridBagConstraints.HORIZONTAL; gc.insets=new Insets(7,8,7,8);
        JLabel title = new JLabel("Masuk ke Aplikasi"); title.setFont(UIUtils.FONT_TITLE); title.setForeground(UIUtils.TEXT_DARK); gc.gridy=0; card.add(title,gc);
        gc.gridy++; card.add(UIUtils.formLabel("Username"),gc); gc.gridy++; card.add(username,gc);
        gc.gridy++; card.add(UIUtils.formLabel("Password"),gc); gc.gridy++; card.add(password,gc);
        gc.gridy++; status.setFont(UIUtils.FONT_SMALL); status.setForeground(UIUtils.RED); card.add(status,gc);
        gc.gridy++; card.add(masuk,gc);
        outer.add(card);
        return outer;
    }

    private void doLogin() {
        masuk.setEnabled(false); status.setForeground(UIUtils.MUTED); status.setText("Memproses login...");
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override protected User doInBackground() { return auth.login(username.getText().trim(), new String(password.getPassword())); }
            @Override protected void done() {
                try {
                    User u = get();
                    if (u != null) { new MainFrame(u).setVisible(true); dispose(); }
                    else { status.setForeground(UIUtils.RED); status.setText("Username atau password salah."); shake(); }
                } catch (Exception ex) { UIUtils.showError(LoginFrame.this, ex.getMessage()); }
                masuk.setEnabled(true);
            }
        };
        worker.execute();
    }

    private void shake() {
        Point p = getLocation();
        Timer timer = new Timer(25, null);
        final int[] i = {0};
        timer.addActionListener(e -> { setLocation(p.x + (i[0] % 2 == 0 ? 8 : -8), p.y); i[0]++; if (i[0] > 10) { timer.stop(); setLocation(p); } });
        timer.start();
    }
}
