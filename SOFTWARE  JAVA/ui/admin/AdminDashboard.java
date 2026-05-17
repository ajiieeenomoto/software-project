package ui.admin;

import database.EventDAO;
import database.RegistrationDAO;
import database.StudentDAO;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.Admin;
import models.Event;
import models.Registration;
import utils.SessionManager;
import utils.UIConstants;

/**
 * Admin Dashboard — main window after admin login.
 * Sidebar + CardLayout navigation.
 */
public class AdminDashboard extends JFrame {

    private final Admin admin = SessionManager.getInstance().getCurrentAdmin();

    private static final String CARD_HOME       = "home";
    private static final String CARD_EVENTS     = "events";
    private static final String CARD_STUDENTS   = "students";
    private static final String CARD_ATTENDANCE = "attendance";

    private CardLayout cardLayout;
    private JPanel     contentPanel;

    public AdminDashboard() {
        setTitle("RTU Event Registration — Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 550));
        initComponents();
        showCard(CARD_HOME);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildTopBar(),  BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIConstants.GRAY_100);
        contentPanel.add(buildHomeCard(),       CARD_HOME);
        contentPanel.add(buildEventsCard(),     CARD_EVENTS);
        contentPanel.add(buildStudentsCard(),   CARD_STUDENTS);
        contentPanel.add(buildAttendanceCard(), CARD_ATTENDANCE);
        root.add(contentPanel, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ─────────────────── TOP BAR ───────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(0x1A237E));
        bar.setPreferredSize(new Dimension(0, 48));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        JLabel title = new JLabel("RTU Event Registration System — Admin Panel");
        title.setFont(UIConstants.FONT_H3);
        title.setForeground(Color.WHITE);
        JLabel adminLabel = new JLabel("🔐  " + admin.getFullName());
        adminLabel.setFont(UIConstants.FONT_SMALL);
        adminLabel.setForeground(new Color(0xB3C3FF));
        bar.add(title,      BorderLayout.WEST);
        bar.add(adminLabel, BorderLayout.EAST);
        return bar;
    }

    // ─────────────────── SIDEBAR ───────────────────────────────

    private JPanel buildSidebar() {
        JPanel sb = new JPanel();
        sb.setBackground(new Color(0x0D1B5C));
        sb.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        sb.add(sBtn("📊  Dashboard",       CARD_HOME));
        sb.add(sBtn("📅  Event Management",CARD_EVENTS));
        sb.add(sBtn("👥  Students",         CARD_STUDENTS));
        sb.add(sBtn("✅  Attendance",       CARD_ATTENDANCE));
        sb.add(Box.createVerticalGlue());

        JButton logout = sBtn("🚪  Logout", null);
        logout.addActionListener(e -> {
            if (UIConstants.confirm(this, "Logout from admin panel?")) {
                SessionManager.getInstance().logout();
                new ui.shared.LandingWindow().setVisible(true);
                this.dispose();
            }
        });
        sb.add(logout);
        return sb;
    }

    private JButton sBtn(String label, String card) {
        JButton b = new JButton(label);
        b.setFont(UIConstants.FONT_SIDEBAR);
        b.setForeground(new Color(0xC5CAE9));
        b.setBackground(new Color(0x0D1B5C));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (card != null) b.addActionListener(e -> showCard(card));
        return b;
    }

    private void showCard(String name) { cardLayout.show(contentPanel, name); }

    // ─────────────────── HOME CARD ─────────────────────────────

    private JPanel buildHomeCard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIConstants.GRAY_100);
        p.setBorder(UIConstants.PADDING_PANEL);

        JLabel h = UIConstants.sectionHeader("Dashboard Overview");
        h.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));

        StudentDAO      sDao = new StudentDAO();
        EventDAO        eDao = new EventDAO();
        RegistrationDAO rDao = new RegistrationDAO();

        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setOpaque(false);
        stats.add(statCard("Total Students",      String.valueOf(sDao.countAll()),             UIConstants.RTU_BLUE));
        stats.add(statCard("Total Events",         String.valueOf(eDao.countAll()),              new Color(0x1A237E)));
        stats.add(statCard("Registrations",        String.valueOf(rDao.countAllRegistrations()), UIConstants.SUCCESS_GREEN));
        stats.add(statCard("Attendance Recorded",  String.valueOf(rDao.countTotalAttendance()),  UIConstants.WARNING_AMBER));

        // Recent registrations table
        JLabel recent = UIConstants.sectionHeader("Recent Registrations");
        recent.setBorder(BorderFactory.createEmptyBorder(20,0,10,0));

        String[] cols = {"Student","Student No.","Event","Status","Date"};
        DefaultTableModel model = new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;} };
        rDao.findByStudent(0); // admin view: load all recent
        // Simplified: show first 10 registrations across all events for first upcoming event
        eDao.findUpcoming().stream().limit(1).forEach(ev ->
            rDao.findByEvent(ev.getEventId()).stream().limit(10).forEach(r ->
                model.addRow(new Object[]{
                    r.getStudentName(), r.getStudentNumber(),
                    r.getEventTitle(), r.getStatus(), r.getRegisteredAt()
                })
            )
        );
        JTable tbl = UIConstants.styledTable(new Object[0][0], cols);
        tbl.setModel(model);
        JScrollPane scroll = new JScrollPane(tbl);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(stats);
        center.add(recent);
        center.add(scroll);

        p.add(h, BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    // ─────────────────── EVENTS CARD ───────────────────────────

    private JPanel buildEventsCard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIConstants.GRAY_100);
        p.setBorder(UIConstants.PADDING_PANEL);

        JLabel h = UIConstants.sectionHeader("Event Management");
        h.setBorder(BorderFactory.createEmptyBorder(0,0,16,0));

        String[] cols = {"ID","Title","Category","Date","Venue","Max","Registered","Open"};
        DefaultTableModel model = new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};

        JTable tbl = new JTable(model);
        tbl.setFont(UIConstants.FONT_BODY);
        tbl.setRowHeight(32);
        tbl.setSelectionBackground(UIConstants.RTU_BLUE_PALE);
        tbl.getTableHeader().setFont(UIConstants.FONT_H3);
        tbl.getTableHeader().setBackground(new Color(0x1A237E));
        tbl.getTableHeader().setForeground(Color.WHITE);

        Runnable reload = () -> {
            model.setRowCount(0);
            new EventDAO().findAll().forEach(ev -> model.addRow(new Object[]{
                ev.getEventId(), ev.getTitle(), ev.getCategoryName(),
                ev.getEventDate(), ev.getVenue(),
                ev.getMaxParticipants(), ev.getRegisteredCount(),
                ev.isRegistrationOpen() ? "Yes" : "No"
            }));
        };
        reload.run();

        JScrollPane scroll = new JScrollPane(tbl);

        // Action buttons
        JButton btnCreate = UIConstants.primaryButton("+ Create Event");
        btnCreate.setBackground(new Color(0x1A237E));
        btnCreate.addActionListener(e -> openCreateEventDialog(reload));

        JButton btnEdit = UIConstants.secondaryButton("Edit");
        btnEdit.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row < 0) { UIConstants.showError(this,"Select an event first."); return; }
            int eventId = (int) model.getValueAt(row, 0);
            Event ev = new EventDAO().findById(eventId);
            if (ev != null) openEditEventDialog(ev, reload);
        });

        JButton btnDelete = UIConstants.dangerButton("Delete");
        btnDelete.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row < 0) { UIConstants.showError(this,"Select an event."); return; }
            int id = (int) model.getValueAt(row, 0);
            if (UIConstants.confirm(this,"Delete this event? This cannot be undone.")) {
                new EventDAO().softDelete(id);
                reload.run();
            }
        });

        JButton btnParticipants = UIConstants.secondaryButton("View Participants");
        btnParticipants.addActionListener(e -> {
            int row = tbl.getSelectedRow();
            if (row < 0) { UIConstants.showError(this,"Select an event."); return; }
            int id = (int) model.getValueAt(row, 0);
            openParticipantsDialog(id);
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnCreate);
        btnRow.add(btnEdit);
        btnRow.add(btnDelete);
        btnRow.add(btnParticipants);

        JPanel center = new JPanel(new BorderLayout(0,8));
        center.setOpaque(false);
        center.add(scroll, BorderLayout.CENTER);
        center.add(btnRow, BorderLayout.SOUTH);

        p.add(h,      BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private void openCreateEventDialog(Runnable reload) {
        JTextField tTitle     = UIConstants.styledTextField(20);
        JTextField tVenue     = UIConstants.styledTextField(20);
        JTextField tOrganizer = UIConstants.styledTextField(20);
        JTextField tDate      = UIConstants.styledTextField(10); tDate.setText("2025-06-01");
        JTextField tTime      = UIConstants.styledTextField(8);  tTime.setText("09:00:00");
        JTextField tMax       = UIConstants.styledTextField(5);  tMax.setText("100");
        JTextArea  tDesc      = new JTextArea(3, 20);
        tDesc.setFont(UIConstants.FONT_INPUT);
        JComboBox<String> catBox = new JComboBox<>(
            new String[]{"Academic","Cultural","Sports","Leadership","Community","Other"});

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Title:"));       form.add(tTitle);
        form.add(new JLabel("Category:"));    form.add(catBox);
        form.add(new JLabel("Venue:"));       form.add(tVenue);
        form.add(new JLabel("Date (yyyy-MM-dd):")); form.add(tDate);
        form.add(new JLabel("Time (HH:mm:ss):")); form.add(tTime);
        form.add(new JLabel("Max participants:"));  form.add(tMax);
        form.add(new JLabel("Organizer:"));   form.add(tOrganizer);
        form.add(new JLabel("Description:")); form.add(new JScrollPane(tDesc));

        int result = JOptionPane.showConfirmDialog(this, form,
            "Create New Event", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Event ev = new Event();
            ev.setTitle(tTitle.getText().trim());
            ev.setVenue(tVenue.getText().trim());
            ev.setOrganizer(tOrganizer.getText().trim());
            ev.setDescription(tDesc.getText().trim());
            ev.setMaxParticipants(Integer.parseInt(tMax.getText().trim()));
            ev.setEventDate(Date.valueOf(tDate.getText().trim()));
            ev.setEventTime(Time.valueOf(tTime.getText().trim()));
            ev.setRegistrationOpen(true);
            ev.setCreatedBy(admin.getAdminId());
            // Resolve category id (simplified: use position+1)
            ev.setCategoryId(catBox.getSelectedIndex() + 1);
            int id = new EventDAO().insert(ev);
            if (id > 0) { UIConstants.showSuccess(this, "Event created!"); reload.run(); }
            else         UIConstants.showError(this, "Failed to create event.");
        }
    }

    private void openEditEventDialog(Event ev, Runnable reload) {
        JTextField tTitle = UIConstants.styledTextField(20); tTitle.setText(ev.getTitle());
        JTextField tVenue = UIConstants.styledTextField(20); tVenue.setText(ev.getVenue());
        JTextField tMax   = UIConstants.styledTextField(5);  tMax.setText(String.valueOf(ev.getMaxParticipants()));
        JCheckBox  chkOpen = new JCheckBox("Registration Open", ev.isRegistrationOpen());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Title:")); form.add(tTitle);
        form.add(new JLabel("Venue:")); form.add(tVenue);
        form.add(new JLabel("Max participants:")); form.add(tMax);
        form.add(new JLabel("Registration:")); form.add(chkOpen);

        int r = JOptionPane.showConfirmDialog(this, form, "Edit Event",
            JOptionPane.OK_CANCEL_OPTION);
        if (r == JOptionPane.OK_OPTION) {
            ev.setTitle(tTitle.getText().trim());
            ev.setVenue(tVenue.getText().trim());
            ev.setMaxParticipants(Integer.parseInt(tMax.getText().trim()));
            ev.setRegistrationOpen(chkOpen.isSelected());
            boolean ok = new EventDAO().update(ev);
            if (ok) { UIConstants.showSuccess(this, "Event updated."); reload.run(); }
            else    UIConstants.showError(this, "Update failed.");
        }
    }

    private void openParticipantsDialog(int eventId) {
        List<Registration> regs = new RegistrationDAO().findByEvent(eventId);
        String[] cols = {"Student","Student No.","Status","Registered At","Attended"};
        Object[][] data = regs.stream().map(r -> new Object[]{
            r.getStudentName(), r.getStudentNumber(), r.getStatus(),
            r.getRegisteredAt(),
            new RegistrationDAO().hasAttended(r.getRegistrationId()) ? "Yes ✓" : "No"
        }).toArray(Object[][]::new);

        JTable tbl = UIConstants.styledTable(data, cols);
        JScrollPane sp = new JScrollPane(tbl);
        sp.setPreferredSize(new Dimension(600, 300));
        JOptionPane.showMessageDialog(this, sp, "Event Participants", JOptionPane.PLAIN_MESSAGE);
    }

    // ─────────────────── STUDENTS CARD ─────────────────────────

    private JPanel buildStudentsCard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIConstants.GRAY_100);
        p.setBorder(UIConstants.PADDING_PANEL);

        JLabel h = UIConstants.sectionHeader("Student Management");
        h.setBorder(BorderFactory.createEmptyBorder(0,0,16,0));

        String[] cols = {"ID","Student No.","Name","Email","Course","Year","Active"};
        DefaultTableModel model = new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        new StudentDAO().findAll().forEach(s -> model.addRow(new Object[]{
            s.getStudentId(), s.getStudentNumber(), s.getFullName(),
            s.getEmail(), s.getCourse(), s.getYearLevel(), s.isActive()
        }));

        JTable tbl = UIConstants.styledTable(new Object[0][0], cols);
        tbl.setModel(model);
        JScrollPane scroll = new JScrollPane(tbl);

        p.add(h,      BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    // ─────────────────── ATTENDANCE CARD ───────────────────────

    private JPanel buildAttendanceCard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIConstants.GRAY_100);
        p.setBorder(UIConstants.PADDING_PANEL);

        JLabel h = UIConstants.sectionHeader("Attendance Management");
        h.setBorder(BorderFactory.createEmptyBorder(0,0,16,0));

        // QR verification panel
        JPanel qrPanel = new JPanel();
        qrPanel.setBackground(Color.WHITE);
        qrPanel.setBorder(UIConstants.BORDER_CARD);
        qrPanel.setLayout(new BoxLayout(qrPanel, BoxLayout.Y_AXIS));

        JLabel qrLabel = new JLabel("Enter QR Code token to verify attendance:");
        qrLabel.setFont(UIConstants.FONT_BODY);
        JTextField qrField = UIConstants.styledTextField(30);
        qrField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        JButton verifyBtn = UIConstants.primaryButton("Verify & Mark Attended");
        verifyBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel resultLabel = new JLabel(" ");
        resultLabel.setFont(UIConstants.FONT_BODY);

        verifyBtn.addActionListener(e -> {
            String token = qrField.getText().trim();
            if (token.isEmpty()) { resultLabel.setText("Please enter a QR token."); return; }
            Registration reg = new RegistrationDAO().findByQrToken(token);
            if (reg == null) {
                resultLabel.setForeground(UIConstants.DANGER_RED);
                resultLabel.setText("❌ Invalid or already used QR token.");
            } else if (new RegistrationDAO().hasAttended(reg.getRegistrationId())) {
                resultLabel.setForeground(UIConstants.WARNING_AMBER);
                resultLabel.setText("⚠ Already marked as attended: " + reg.getStudentName());
            } else {
                boolean ok = new RegistrationDAO().markAttendance(
                    reg.getRegistrationId(), reg.getEventId(),
                    reg.getStudentId(), "qr_scan", admin.getAdminId()
                );
                if (ok) {
                    resultLabel.setForeground(UIConstants.SUCCESS_GREEN);
                    resultLabel.setText("✔ Attendance recorded for: " + reg.getStudentName()
                        + "  |  " + reg.getEventTitle());
                    qrField.setText("");
                } else {
                    resultLabel.setForeground(UIConstants.DANGER_RED);
                    resultLabel.setText("❌ Failed to record attendance.");
                }
            }
        });

        qrPanel.add(qrLabel);
        qrPanel.add(Box.createVerticalStrut(8));
        qrPanel.add(qrField);
        qrPanel.add(Box.createVerticalStrut(10));
        qrPanel.add(verifyBtn);
        qrPanel.add(Box.createVerticalStrut(10));
        qrPanel.add(resultLabel);

        p.add(h,       BorderLayout.NORTH);
        p.add(qrPanel, BorderLayout.CENTER);
        return p;
    }

    // ─────────────────── HELPERS ───────────────────────────────

    private JPanel statCard(String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(0,4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,4,0,0, color),
            BorderFactory.createEmptyBorder(16,16,16,16)
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
}