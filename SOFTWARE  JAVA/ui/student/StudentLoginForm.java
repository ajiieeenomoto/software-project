package ui.student;

import controllers.AuthController;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import models.Student;
import utils.SessionManager;
import utils.UIConstants;

/**
 * Student Login Form.
 */
public class StudentLoginForm extends JFrame {

    private final JFrame parent;
    private JTextField     txtStudentNumber;
    private JPasswordField txtPassword;
    private JButton        btnLogin;
    private JLabel         lblError;

    public StudentLoginForm(JFrame parent) {
        this.parent = parent;
        setTitle("Student Login — RTU Event Registration");
        setSize(460, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { goBack(); }
        });
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Header ──
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        header.setBackground(UIConstants.RTU_BLUE);
        JLabel title = new JLabel("Student Login");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 80));
        header.add(title);

        // ── Form ──
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(30, 50, 20, 50));

        txtStudentNumber = UIConstants.styledTextField(20);
        txtPassword      = UIConstants.styledPasswordField(20);
        txtStudentNumber.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        // Allow Enter key to trigger login
        KeyAdapter enterLogin = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        };
        txtStudentNumber.addKeyListener(enterLogin);
        txtPassword.addKeyListener(enterLogin);

        lblError = new JLabel(" ");
        lblError.setFont(UIConstants.FONT_SMALL);
        lblError.setForeground(UIConstants.DANGER_RED);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin = UIConstants.primaryButton("Login");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnLogin.addActionListener(e -> doLogin());

        JLabel lblSignup = new JLabel("<html><center>Don't have an account? "
            + "<a href=''>Sign up here</a></center></html>");
        lblSignup.setFont(UIConstants.FONT_SMALL);
        lblSignup.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblSignup.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSignup.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { openSignup(); }
        });

        JLabel lblForgot = new JLabel("<html><center><a href=''>Forgot password?</a></center></html>");
        lblForgot.setFont(UIConstants.FONT_SMALL);
        lblForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblForgot.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblForgot.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { openForgotPassword(); }
        });

        JButton btnBack = UIConstants.secondaryButton("← Back");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.addActionListener(e -> goBack());

        form.add(fieldLabel("Student Number (e.g. 2021-00001)"));
        form.add(Box.createVerticalStrut(4));
        form.add(txtStudentNumber);
        form.add(Box.createVerticalStrut(16));
        form.add(fieldLabel("Password"));
        form.add(Box.createVerticalStrut(4));
        form.add(txtPassword);
        form.add(Box.createVerticalStrut(8));
        form.add(lblError);
        form.add(Box.createVerticalStrut(12));
        form.add(btnLogin);
        form.add(Box.createVerticalStrut(16));
        form.add(lblSignup);
        form.add(Box.createVerticalStrut(8));
        form.add(lblForgot);
        form.add(Box.createVerticalStrut(12));
        form.add(btnBack);

        root.add(header, BorderLayout.NORTH);
        root.add(form,   BorderLayout.CENTER);
        setContentPane(root);
    }

    private void doLogin() {
        String sn  = txtStudentNumber.getText().trim();
        String pwd = new String(txtPassword.getPassword());

        if (sn.isEmpty() || pwd.isEmpty()) {
            lblError.setText("Please fill in all fields.");
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in…");

        SwingWorker<Student, Void> worker = new SwingWorker<>() {
            @Override protected Student doInBackground() {
                return new AuthController().loginStudent(sn, pwd);
            }
            @Override protected void done() {
                try {
                    Student student = get();
                    if (student != null) {
                        SessionManager.getInstance().loginStudent(student);
                        openStudentDashboard();
                    } else {
                        lblError.setText("Invalid student number or password.");
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Login");
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    lblError.setText("Login interrupted.");
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");
                } catch (java.util.concurrent.ExecutionException ex) {
                    lblError.setText("Error: " + ex.getCause().getMessage());
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");
                }
            }
        };
        worker.execute();
    }

    private void openStudentDashboard() {
        StudentDashboard dashboard = new StudentDashboard();
        dashboard.setVisible(true);
        this.dispose();
        parent.dispose();
    }

    private void openSignup() {
        StudentSignupForm signup = new StudentSignupForm(this);
        signup.setVisible(true);
        this.setVisible(false);
    }

    private void openForgotPassword() {
        JOptionPane.showMessageDialog(this,
            """
            Please contact your Department Secretary or the Registrar's Office
            with your Student ID to reset your password.
            """,
            "Forgot Password", JOptionPane.INFORMATION_MESSAGE);
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