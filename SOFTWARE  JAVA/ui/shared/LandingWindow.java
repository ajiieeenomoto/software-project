package ui.shared;

import utils.UIConstants;
import ui.admin.AdminLoginForm;
import ui.student.StudentLoginForm;

import javax.swing.*;
import java.awt.*;

/**
 * Landing Window — the first screen shown on application startup.
 * Provides navigation to Student Login or Admin Login.
 */
public class LandingWindow extends JFrame {

    public LandingWindow() {
        setTitle("RTU Event Registration System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        // ── Root layout: left blue panel + right white panel ──
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildLeftPanel(),  BorderLayout.CENTER);
        root.add(buildRightPanel(), BorderLayout.EAST);
        setContentPane(root);
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIConstants.RTU_BLUE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 40, 40, 40));

        // RTU Seal placeholder label
        JLabel seal = new JLabel("🎓", SwingConstants.CENTER);
        seal.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        seal.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("RTU");
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Event Registration System");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(0xB3D1FF));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel university = new JLabel("<html><center>Rizal Technological<br>University</center></html>");
        university.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        university.setForeground(new Color(0x8BB8F0));
        university.setHorizontalAlignment(SwingConstants.CENTER);
        university.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(seal);
        panel.add(Box.createVerticalStrut(12));
        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(16));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(16));
        panel.add(university);
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 40, 40, 40));

        JLabel welcome = new JLabel("Welcome");
        welcome.setFont(UIConstants.FONT_TITLE);
        welcome.setForeground(UIConstants.RTU_BLUE);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instruction = new JLabel("Please select your role to continue");
        instruction.setFont(UIConstants.FONT_BODY);
        instruction.setForeground(UIConstants.GRAY_500);
        instruction.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Student login button
        JButton btnStudent = UIConstants.primaryButton("Login as Student");
        btnStudent.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnStudent.setMaximumSize(new Dimension(220, 42));
        btnStudent.setPreferredSize(new Dimension(220, 42));
        btnStudent.addActionListener(e -> openStudentLogin());

        // Admin login button
        JButton btnAdmin = UIConstants.secondaryButton("Login as Admin");
        btnAdmin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdmin.setMaximumSize(new Dimension(220, 42));
        btnAdmin.setPreferredSize(new Dimension(220, 42));
        btnAdmin.addActionListener(e -> openAdminLogin());

        // Version label
        JLabel version = new JLabel("v1.0  |  AY 2024–2025");
        version.setFont(UIConstants.FONT_SMALL);
        version.setForeground(UIConstants.GRAY_500);
        version.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(welcome);
        panel.add(Box.createVerticalStrut(8));
        panel.add(instruction);
        panel.add(Box.createVerticalStrut(40));
        panel.add(btnStudent);
        panel.add(Box.createVerticalStrut(16));
        panel.add(btnAdmin);
        panel.add(Box.createVerticalGlue());
        panel.add(version);
        return panel;
    }

    private void openStudentLogin() {
        StudentLoginForm form = new StudentLoginForm(this);
        form.setVisible(true);
        this.setVisible(false);
    }

    private void openAdminLogin() {
        AdminLoginForm form = new AdminLoginForm(this);
        form.setVisible(true);
        this.setVisible(false);
    }
}