package ui.admin;

import controllers.AuthController;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import models.Admin;
import utils.SessionManager;
import utils.UIConstants;

/**
 * Admin Login Form.
 */
public class AdminLoginForm extends JFrame {

    private final JFrame parent;
    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JLabel         lblError;

    public AdminLoginForm(JFrame parent) {
        this.parent = parent;
        setTitle("Admin Login — RTU Event Registration");
        setSize(420, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new WindowAdapter() { @Override 
            public void windowClosing(WindowEvent e) { goBack(); }
        });
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        header.setBackground(new Color(0x1A237E));  // deep blue for admin
        header.setPreferredSize(new Dimension(0, 80));
        JLabel title = new JLabel("Admin Login");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        txtUsername = UIConstants.styledTextField(20);
        txtPassword = UIConstants.styledPasswordField(20);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        lblError = new JLabel(" ");
        lblError.setFont(UIConstants.FONT_SMALL);
        lblError.setForeground(UIConstants.DANGER_RED);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnLogin = UIConstants.primaryButton("Login");
        btnLogin.setBackground(new Color(0x1A237E));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.addActionListener(e -> doLogin());

        JButton btnBack = UIConstants.secondaryButton("← Back");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(e -> goBack());

        JLabel note = new JLabel("Admin accounts are managed by the system administrator.");
        note.setFont(UIConstants.FONT_SMALL);
        note.setForeground(UIConstants.GRAY_500);
        note.setAlignmentX(Component.CENTER_ALIGNMENT);

        form.add(fieldLabel("Username"));
        form.add(Box.createVerticalStrut(4));
        form.add(txtUsername);
        form.add(Box.createVerticalStrut(16));
        form.add(fieldLabel("Password"));
        form.add(Box.createVerticalStrut(4));
        form.add(txtPassword);
        form.add(Box.createVerticalStrut(8));
        form.add(lblError);
        form.add(Box.createVerticalStrut(12));
        form.add(btnLogin);
        form.add(Box.createVerticalStrut(16));
        form.add(note);
        form.add(Box.createVerticalStrut(12));
        form.add(btnBack);

        root.add(header, BorderLayout.NORTH);
        root.add(form,   BorderLayout.CENTER);
        setContentPane(root);
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please fill in all fields.");
            return;
        }
        Admin admin = new AuthController().loginAdmin(username, password);
        if (admin != null) {
            SessionManager.getInstance().loginAdmin(admin);
            new AdminDashboard().setVisible(true);
            this.dispose();
            parent.dispose();
        } else {
            lblError.setText("Invalid username or password.");
        }
    }

    private void goBack() {
        parent.setVisible(true);
        this.dispose();
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.FONT_BODY);
        lbl.setForeground(UIConstants.GRAY_700);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}