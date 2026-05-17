package main;

import javax.swing.*;

/**
 * RTU Event Registration System — Application Entry Point.
 *
 * HOW TO RUN:
 * 1. Set up MySQL and run database_schema.sql
 * 2. Update DatabaseConnection.java with your MySQL credentials
 * 3. Add to classpath:
 *      - mysql-connector-j-8.x.x.jar  (JDBC driver)
 *      - jbcrypt-0.4.jar               (password hashing)
 *      - (optional) core-3.5.2.jar     (ZXing QR code)
 *      - (optional) javase-3.5.2.jar   (ZXing)
 * 4. Compile: javac -cp ".:lib/*" -d out -sourcepath src src/main/Main.java
 * 5. Run:     java  -cp ".:lib/*:out" main.Main
 */
public class Main {

    public static void main(String[] args) {
        // Use system look-and-feel for native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
            // Fall back to default Swing L&F
        }

        // Launch on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            Object landing = new ui.shared.LandingWindow();
            if (landing instanceof java.awt.Component component) {
                JFrame frame = new JFrame("RTU Event Registration System");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(component);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } else if (landing instanceof java.awt.Window window) {
                window.setVisible(true);
            } else {
                try {
                    landing.getClass().getMethod("setVisible", boolean.class).invoke(landing, true);
                } catch (ReflectiveOperationException ignored) {
                    // Unable to display landing window
                }
            }
        });
    }
}