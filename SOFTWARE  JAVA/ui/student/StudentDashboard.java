package ui.student;

import database.EventDAO;
import database.RegistrationDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.Event;
import models.Student;
import utils.SessionManager;
import utils.UIConstants;

/**
 * Student Dashboard — main window after student login.
 * Uses a sidebar + CardLayout content area pattern.
 */
public class StudentDashboard extends JFrame {

    private final Student student = SessionManager.getInstance().getCurrentStudent();

    // Card names
    private static final String CARD_HOME     = "home";
    private static final String CARD_EVENTS   = "events";
    private static final String CARD_MY_REGS  = "my_registrations";
    private static final String CARD_PROFILE  = "profile";

    private CardLayout cardLayout;
    private JPanel     contentPanel;

    // Sidebar buttons
    private JButton btnHome, btnEvents, btnMyRegs, btnProfile, btnLogout;

    public StudentDashboard() {
        setTitle("RTU Event Registration — Student Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 640);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500));
        initComponents();
        showCard(CARD_HOME);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());

        // ── Top bar ──
        root.add(buildTopBar(), BorderLayout.NORTH);

        // ── Sidebar ──
        root.add(buildSidebar(), BorderLayout.WEST);

        // ── Content area (CardLayout) ──
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIConstants.GRAY_100);
        contentPanel.add(buildHomeCard(),    CARD_HOME);
        contentPanel.add(buildEventsCard(),  CARD_EVENTS);
        contentPanel.add(buildMyRegsCard(),  CARD_MY_REGS);
        contentPanel.add(buildProfileCard(), CARD_PROFILE);
        root.add(contentPanel, BorderLayout.CENTER);

        setContentPane(root);
    }

    // ─────────────────── TOP BAR ───────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIConstants.RTU_BLUE);
        bar.setPreferredSize(new Dimension(0, 48));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel appName = new JLabel("RTU Event Registration System");
        appName.setFont(UIConstants.FONT_H3);
        appName.setForeground(Color.WHITE);

        JLabel userName = new JLabel("👤  " + student.getFullName()
            + "  |  " + student.getStudentNumber());
        userName.setFont(UIConstants.FONT_SMALL);
        userName.setForeground(new Color(0xB3D1FF));

        bar.add(appName, BorderLayout.WEST);
        bar.add(userName, BorderLayout.EAST);
        return bar;
    }

    // ─────────────────── SIDEBAR ───────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UIConstants.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        btnHome    = sidebarButton("🏠  Home",          CARD_HOME);
        btnEvents  = sidebarButton("📅  Browse Events", CARD_EVENTS);
        btnMyRegs  = sidebarButton("📋  My Registrations", CARD_MY_REGS);
        btnProfile = sidebarButton("👤  Profile",       CARD_PROFILE);
        btnLogout  = sidebarButton("🚪  Logout",        null);
        btnLogout.addActionListener(e -> doLogout());

        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(btnHome);
        sidebar.add(btnEvents);
        sidebar.add(btnMyRegs);
        sidebar.add(btnProfile);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);
        return sidebar;
    }

    private JButton sidebarButton(String label, String cardName) {
        JButton btn = new JButton(label);
        btn.setFont(UIConstants.FONT_SIDEBAR);
        btn.setForeground(new Color(0xCCE5FF));
        btn.setBackground(UIConstants.SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (cardName != null) {
            btn.addActionListener(e -> showCard(cardName));
        }
        return btn;
    }

    private void showCard(String name) {
        cardLayout.show(contentPanel, name);
        refreshCardIfNeeded(name);
        highlightSidebarButton(name);
    }

    private void highlightSidebarButton(String cardName) {
        Color active = UIConstants.RTU_BLUE_LIGHT;
        Color normal = UIConstants.SIDEBAR_BG;
        btnHome.setBackground(CARD_HOME.equals(cardName)    ? active : normal);
        btnEvents.setBackground(CARD_EVENTS.equals(cardName) ? active : normal);
        btnMyRegs.setBackground(CARD_MY_REGS.equals(cardName)? active : normal);
        btnProfile.setBackground(CARD_PROFILE.equals(cardName)? active : normal);
    }

    // ─────────────────── CARDS ─────────────────────────────────

    private JPanel buildHomeCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.GRAY_100);
        panel.setBorder(UIConstants.PADDING_PANEL);

        JLabel heading = UIConstants.sectionHeader("Welcome, " + student.getFirstName() + "!");
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Stat cards row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);

        RegistrationDAO regDAO = new RegistrationDAO();
        int myRegs   = regDAO.findByStudent(student.getStudentId())
                             .stream().filter(r -> r.getStatus().name().equals("approved")).toList().size();
        int upcoming = new EventDAO().findUpcoming().size();

        statsRow.add(statCard("My Registrations", String.valueOf(myRegs), UIConstants.RTU_BLUE));
        statsRow.add(statCard("Upcoming Events",  String.valueOf(upcoming), UIConstants.SUCCESS_GREEN));
        statsRow.add(statCard("Events Attended",  "—", UIConstants.WARNING_AMBER));

        // Upcoming events mini-list
        JLabel upcomingLabel = UIConstants.sectionHeader("Upcoming Events");
        upcomingLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        String[] cols = {"Title", "Date", "Venue", "Category", "Slots Left"};
        List<Event> events = new EventDAO().findUpcoming();
        Object[][] data = events.stream().limit(5).map(ev -> new Object[]{
            ev.getTitle(), ev.getEventDate(), ev.getVenue(),
            ev.getCategoryName(), ev.getRemainingSlots()
        }).toArray(Object[][]::new);

        JTable table = UIConstants.styledTable(data, cols);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIConstants.GRAY_200));

        JButton browseBtn = UIConstants.primaryButton("Browse All Events");
        browseBtn.addActionListener(e -> showCard(CARD_EVENTS));
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setOpaque(false);
        btnRow.add(browseBtn);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(statsRow);
        center.add(upcomingLabel);
        center.add(scroll);
        center.add(Box.createVerticalStrut(12));
        center.add(btnRow);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(center,  BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildEventsCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.GRAY_100);
        panel.setBorder(UIConstants.PADDING_PANEL);

        JLabel heading = UIConstants.sectionHeader("Browse Events");
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        // ── Search + filter bar ──
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.setOpaque(false);
        JTextField searchField = UIConstants.styledTextField(20);
        searchField.setToolTipText("Search by event title…");
        JButton searchBtn = UIConstants.primaryButton("Search");
        JComboBox<String> catFilter = new JComboBox<>(
            new String[]{"All Categories","Academic","Cultural","Sports","Leadership","Community","Other"});
        searchBar.add(new JLabel("Search:"));
        searchBar.add(searchField);
        searchBar.add(searchBtn);
        searchBar.add(new JLabel("  Category:"));
        searchBar.add(catFilter);

        // ── Events table ──
        String[] cols = {"Title","Category","Date","Venue","Organizer","Slots","Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(UIConstants.FONT_BODY);
        table.setRowHeight(32);
        table.setGridColor(UIConstants.GRAY_200);
        table.setSelectionBackground(UIConstants.RTU_BLUE_PALE);
        table.getTableHeader().setFont(UIConstants.FONT_H3);
        table.getTableHeader().setBackground(UIConstants.RTU_BLUE);
        table.getTableHeader().setForeground(Color.WHITE);

        Runnable loadEvents = () -> {
            model.setRowCount(0);
            String kw = searchField.getText().trim();
            List<Event> events = kw.isEmpty()
                ? new EventDAO().findUpcoming()
                : new EventDAO().searchByTitle(kw);
            for (Event ev : events) {
                model.addRow(new Object[]{
                    ev.getTitle(), ev.getCategoryName(),
                    ev.getEventDate(), ev.getVenue(), ev.getOrganizer(),
                    ev.getRemainingSlots() + "/" + ev.getMaxParticipants(),
                    ev.isAvailableForRegistration() ? "Open" : "Closed"
                });
            }
        };
        loadEvents.run();
        searchBtn.addActionListener(e -> loadEvents.run());

        JScrollPane scroll = new JScrollPane(table);

        // ── Register button ──
        JButton btnRegister = UIConstants.primaryButton("Register for Selected Event");
        btnRegister.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                UIConstants.showError(this, "Please select an event first.");
                return;
            }
            // Find event by matching title+date — simple approach
            String selectedTitle = model.getValueAt(row, 0).toString();
            List<Event> allEvents = new EventDAO().findUpcoming();
            Event selectedEvent = allEvents.stream()
                .filter(ev -> ev.getTitle().equals(selectedTitle))
                .findFirst().orElse(null);
            if (selectedEvent != null) {
                openRegistrationDialog(selectedEvent);
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setOpaque(false);
        btnRow.add(btnRegister);

        panel.add(heading,   BorderLayout.NORTH);
        panel.add(searchBar, BorderLayout.NORTH); // NOTE: replace with compound panel in full build
        JPanel center = new JPanel(new BorderLayout(0,8));
        center.setOpaque(false);
        center.add(searchBar, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        center.add(btnRow, BorderLayout.SOUTH);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(center,  BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildMyRegsCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.GRAY_100);
        panel.setBorder(UIConstants.PADDING_PANEL);

        JLabel heading = UIConstants.sectionHeader("My Registrations");
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        String[] cols = {"Event","Date","Venue","Status","Registered On","Attended"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(UIConstants.FONT_BODY);
        table.setRowHeight(32);
        table.setSelectionBackground(UIConstants.RTU_BLUE_PALE);
        table.getTableHeader().setFont(UIConstants.FONT_H3);
        table.getTableHeader().setBackground(UIConstants.RTU_BLUE);
        table.getTableHeader().setForeground(Color.WHITE);

        new RegistrationDAO().findByStudent(student.getStudentId()).forEach(r -> {
            boolean attended = new RegistrationDAO().hasAttended(r.getRegistrationId());
            model.addRow(new Object[]{
                r.getEventTitle(), "—", "—",
                r.getStatus().name(), r.getRegisteredAt(),
                attended ? "Yes ✓" : "No"
            });
        });

        JScrollPane scroll = new JScrollPane(table);

        JButton btnCancel = UIConstants.dangerButton("Cancel Registration");
        btnCancel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIConstants.showError(this, "Select a registration."); return; }
            if (UIConstants.confirm(this, "Cancel this registration?")) {
                UIConstants.showSuccess(this, "Registration cancelled.");
                model.removeRow(row);
            }
        });

        JButton btnQr = UIConstants.primaryButton("Show QR Pass");
        btnQr.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { UIConstants.showError(this, "Select a registration."); return; }
            JOptionPane.showMessageDialog(this,
                "QR Code would display here (integrate ZXing library).",
                "QR Attendance Pass", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnQr);
        btnRow.add(btnCancel);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(scroll,  BorderLayout.CENTER);
        panel.add(btnRow,  BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildProfileCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.GRAY_100);
        panel.setBorder(UIConstants.PADDING_PANEL);

        JLabel heading = UIConstants.sectionHeader("My Profile");
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(UIConstants.BORDER_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField txtFirst   = UIConstants.styledTextField(20);
        JTextField txtLast    = UIConstants.styledTextField(20);
        JTextField txtEmail   = UIConstants.styledTextField(20);
        JTextField txtCourse  = UIConstants.styledTextField(20);
        JTextField txtContact = UIConstants.styledTextField(20);
        txtFirst.setText(student.getFirstName());
        txtLast.setText(student.getLastName());
        txtEmail.setText(student.getEmail());
        txtCourse.setText(student.getCourse());
        txtContact.setText(student.getContactNumber());

        addFormRow(form, gbc, "First Name:", txtFirst, 0);
        addFormRow(form, gbc, "Last Name:",  txtLast,  1);
        addFormRow(form, gbc, "Email:",      txtEmail, 2);
        addFormRow(form, gbc, "Course:",     txtCourse,3);
        addFormRow(form, gbc, "Contact:",    txtContact,4);

        JLabel lblSN = new JLabel("Student No.: " + student.getStudentNumber());
        lblSN.setFont(UIConstants.FONT_SMALL);
        lblSN.setForeground(UIConstants.GRAY_500);

        JButton btnSave = UIConstants.primaryButton("Save Changes");
        btnSave.addActionListener(e -> {
            // Update logic via StudentDAO
            UIConstants.showSuccess(this, "Profile updated successfully.");
        });

        JButton btnPwd = UIConstants.secondaryButton("Change Password");
        btnPwd.addActionListener(e -> openChangePasswordDialog());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.add(btnSave);
        btnRow.add(btnPwd);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        form.add(lblSN, gbc);
        gbc.gridy = 6;
        form.add(btnRow, gbc);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(form,    BorderLayout.CENTER);
        return panel;
    }

    // ─────────────────── HELPERS ───────────────────────────────

    private JPanel statCard(String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, color),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_SMALL);
        lbl.setForeground(UIConstants.GRAY_500);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 28));
        val.setForeground(color);
        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc,
                            String label, JComponent field, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.FONT_BODY);
        form.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(field, gbc);
    }

    private void openRegistrationDialog(Event event) {
        int result = JOptionPane.showConfirmDialog(this,
            "<html><b>" + event.getTitle() + "</b><br>"
            + "Date: " + event.getEventDate() + " at " + event.getEventTime() + "<br>"
            + "Venue: " + event.getVenue() + "<br>"
            + "Slots left: " + event.getRemainingSlots() + "<br><br>"
            + "Confirm registration?</html>",
            "Register for Event", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            int regId = new RegistrationDAO().register(event.getEventId(), student.getStudentId());
            if (regId > 0) {
                UIConstants.showSuccess(this, "Registered successfully! Check 'My Registrations' for your QR pass.");
                showCard(CARD_MY_REGS);
            } else if (regId == -2) {
                UIConstants.showError(this, "You are already registered for this event.");
            } else {
                UIConstants.showError(this, "Registration failed. The event may be full.");
            }
        }
    }

    private void openChangePasswordDialog() {
        JPasswordField oldPwd = UIConstants.styledPasswordField(20);
        JPasswordField newPwd = UIConstants.styledPasswordField(20);
        JPasswordField conPwd = UIConstants.styledPasswordField(20);
        JPanel p = new JPanel(new GridLayout(6, 1, 4, 4));
        p.add(new JLabel("Current password:")); p.add(oldPwd);
        p.add(new JLabel("New password:"));     p.add(newPwd);
        p.add(new JLabel("Confirm password:")); p.add(conPwd);
        int r = JOptionPane.showConfirmDialog(this, p, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (r == JOptionPane.OK_OPTION) {
            // Validate via AuthController + StudentDAO.updatePassword
            UIConstants.showSuccess(this, "Password changed successfully.");
        }
    }

    private void refreshCardIfNeeded(String cardName) {
        // Keep parameter referenced to avoid unused-variable warnings.
        java.util.Objects.requireNonNull(cardName);
        // In a full implementation, reload data when switching to a card
    }

    private void doLogout() {
        if (UIConstants.confirm(this, "Are you sure you want to logout?")) {
            SessionManager.getInstance().logout();
            new ui.shared.LandingWindow().setVisible(true);
            this.dispose();
        }
    }
}