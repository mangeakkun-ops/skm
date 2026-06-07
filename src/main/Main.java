package main;

import form.LoginFrame;
import java.awt.Font;
import java.lang.reflect.Method;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
            if (!installLaf("com.formdev.flatlaf.FlatLightLaf")) {
                if (!installLaf("com.formdev.flatlaf.FlatLightLaf")) {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
            }
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            System.err.println("Look and Feel gagal: " + ex.getMessage());
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    private static boolean installLaf(String className) {
        try {
            Class<?> laf = Class.forName(className);
            Method setup = laf.getMethod("setup");
            Object result = setup.invoke(null);
            return !(result instanceof Boolean) || (Boolean) result;
        } catch (ReflectiveOperationException ex) {
            return false;
        }
    }
}
