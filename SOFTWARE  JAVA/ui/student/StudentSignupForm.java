package ui.student;

import java.awt.*;
import javax.swing.*;

import database.StudentDAO;
import models.Student;
import utils.InputValidator;
import utils.UIConstants;

/**
 * Student Signup / Registration Form.
 */
public class StudentSignupForm extends JFrame {

    private final JFrame parent;
    private JTextField     txtStudentNumber, txtFirstName, txtLastName,
                           txtEmail, txtCourse, txtContact;
    private JPasswordField txtPassword, txtConfirm;
    private JComboBox<String> cmbYear;

    public StudentSignupForm(JFrame parent) {
        this.parent = parent;
        setTitle("Create Student Account — RTU");
        setSize(520, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 18));
        header.setBackground(UIConstants.RTU_BLUE);
        header.setPreferredSize(new Dimension(0, 70));
        JLabel title = new JLabel("Student Registration");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 4, 5, 4);

        txtStudentNumber = UIConstants.styledTextField(20);
        txtFirstName     = UIConstants.styledTextField(15);
        txtLastName      = UIConstants.styledTextField(15);
        txtEmail         = UIConstants.styledTextField(20);
        txtCourse        = UIConstants.styledTextField(20);
        txtContact       = UIConstants.styledTextField(15);
        txtPassword      = UIConstants.styledPasswordField(15);
        txtConfirm       = UIConstants.styledPasswordField(15);
        cmbYear          = new JComboBox<>(new String[]{"1st Year","2nd Year","3rd Year","4th Year","5th Year"});
        cmbYear.setFont(UIConstants.FONT_INPUT);

        addRow(form, gbc, "Student Number *",   txtStudentNumber, 0);
        addRow(form, gbc, "First Name *",        txtFirstName,    1);
        addRow(form, gbc, "Last Name *",         txtLastName,     2);
        addRow(form, gbc, "Email Address *",     txtEmail,        3);
        addRow(form, gbc, "Course *",            txtCourse,       4);
        addRow(form, gbc, "Year Level",          cmbYear,         5);
        addRow(form, gbc, "Contact Number",      txtContact,      6);
        addRow(form, gbc, "Password * (min 8)", txtPassword,     7);
        addRow(form, gbc, "Confirm Password *", txtConfirm,      8);

        JButton btnRegister = UIConstants.primaryButton("Create Account");
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnRegister.addActionListener(e -> doRegister());

        JButton btnBack = UIConstants.secondaryButton("← Back to Login");
        btnBack.addActionListener(e -> { parent.setVisible(true); this.dispose(); });

        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2; gbc.insets = new Insets(16, 4, 4, 4);
        form.add(btnRegister, gbc);
        gbc.gridy = 10; gbc.insets = new Insets(4, 4, 4, 4);
        form.add(btnBack, gbc);

        root.add(header, BorderLayout.NORTH);
        root.add(new JScrollPane(form), BorderLayout.CENTER);
        setContentPane(root);
    }

    private void addRow(JPanel form, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridwidth = 1; gbc.weightx = 0.35;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_BODY);
        form.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        form.add(field, gbc);
    }

    private void doRegister() {
        String sn    = txtStudentNumber.getText().trim();
        String first = txtFirstName.getText().trim();
        String last  = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String course= txtCourse.getText().trim();
        String phone = txtContact.getText().trim();
        String pwd   = new String(txtPassword.getPassword());
        String conf  = new String(txtConfirm.getPassword());
        int    year  = cmbYear.getSelectedIndex() + 1;

        if (!InputValidator.isValidStudentNumber(sn)) {
            UIConstants.showError(this, "Invalid student number format. Use: 2021-00001"); return;
        }
        if (InputValidator.isBlank(first) || InputValidator.isBlank(last)) {
            UIConstants.showError(this, "First and Last name are required."); return;
        }
        if (!InputValidator.isValidEmail(email)) {
            UIConstants.showError(this, "Invalid email address."); return;
        }
        if (!InputValidator.isValidPassword(pwd)) {
            UIConstants.showError(this, "Password must be at least 8 characters with letters and digits."); return;
        }
        if (!pwd.equals(conf)) {
            UIConstants.showError(this, "Passwords do not match."); return;
        }
        if (InputValidator.isBlank(course)) {
            UIConstants.showError(this, "Course is required."); return;
        }

        StudentDAO dao = new StudentDAO();
        if (dao.isStudentNumberTaken(sn)) {
            UIConstants.showError(this, "Student number already registered."); return;
        }
        if (dao.isEmailTaken(email)) {
            UIConstants.showError(this, "Email already registered."); return;
        }

        Student s = new Student();
        s.setStudentNumber(sn);
        s.setFirstName(first);
        s.setLastName(last);
        s.setEmail(email);
        s.setCourse(course);
        s.setYearLevel(year);
        s.setContactNumber(phone);

        int id = dao.insert(s, pwd);
        if (id > 0) {
            UIConstants.showSuccess(this, "Account created successfully! You may now login.");
            parent.setVisible(true);
            this.dispose();
        } else {
            UIConstants.showError(this, "Registration failed. Please try again.");
        }
    }
}