package utils;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * Centralized UI constants for the RTU Event Registration System.
 * Import this in every UI class to keep styling consistent.
 */
public class UIConstants {

    // ── RTU Color Palette ──────────────────────────────────────
    public static final Color RTU_BLUE        = new Color(0x0D3B73);   // primary
    public static final Color RTU_BLUE_LIGHT  = new Color(0x1565C0);   // hover
    public static final Color RTU_BLUE_PALE   = new Color(0xE3F0FF);   // backgrounds
    public static final Color WHITE            = Color.WHITE;
    public static final Color GRAY_100         = new Color(0xF5F5F5);
    public static final Color GRAY_200         = new Color(0xE0E0E0);
    public static final Color GRAY_500         = new Color(0x9E9E9E);
    public static final Color GRAY_700         = new Color(0x616161);
    public static final Color GRAY_900         = new Color(0x212121);
    public static final Color SUCCESS_GREEN    = new Color(0x2E7D32);
    public static final Color WARNING_AMBER    = new Color(0xE65100);
    public static final Color DANGER_RED       = new Color(0xC62828);
    public static final Color SIDEBAR_BG       = new Color(0x0A2D5A);  // darker sidebar

    // ── Typography ─────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_H2      = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_H3      = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_SIDEBAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_INPUT   = new Font("Segoe UI", Font.PLAIN, 13);

    // ── Borders ────────────────────────────────────────────────
    public static final Border BORDER_INPUT = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(GRAY_200, 1),
        BorderFactory.createEmptyBorder(6, 10, 6, 10)
    );
    public static final Border BORDER_CARD = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(GRAY_200, 1),
        BorderFactory.createEmptyBorder(16, 16, 16, 16)
    );
    public static final Border PADDING_PANEL = BorderFactory.createEmptyBorder(20, 20, 20, 20);

    // ── Dimensions ─────────────────────────────────────────────
    public static final int SIDEBAR_WIDTH  = 220;
    public static final int BUTTON_HEIGHT  = 36;
    public static final Dimension BTN_SIZE_PRIMARY   = new Dimension(160, BUTTON_HEIGHT);
    public static final Dimension BTN_SIZE_SECONDARY = new Dimension(120, BUTTON_HEIGHT);

    // ── Factory Methods ────────────────────────────────────────

    /** Creates a styled primary (blue) button. */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(RTU_BLUE);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(BTN_SIZE_PRIMARY);
        return btn;
    }

    /** Creates a styled secondary (outlined) button. */
    public static JButton secondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(WHITE);
        btn.setForeground(RTU_BLUE);
        btn.setBorder(BorderFactory.createLineBorder(RTU_BLUE, 1));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(BTN_SIZE_SECONDARY);
        return btn;
    }

    /** Creates a styled danger (red) button. */
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(DANGER_RED);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Creates a styled text field with standard padding. */
    public static JTextField styledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FONT_INPUT);
        field.setBorder(BORDER_INPUT);
        return field;
    }

    /** Creates a styled password field. */
    public static JPasswordField styledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(FONT_INPUT);
        field.setBorder(BORDER_INPUT);
        return field;
    }

    /** Creates a styled JTable with RTU header colors. */
    public static JTable styledTable(Object[][] data, String[] columns) {
        JTable table = new JTable(data, columns);
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setGridColor(GRAY_200);
        table.setSelectionBackground(RTU_BLUE_PALE);
        table.setSelectionForeground(GRAY_900);
        table.getTableHeader().setFont(FONT_H3);
        table.getTableHeader().setBackground(RTU_BLUE);
        table.getTableHeader().setForeground(WHITE);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        return table;
    }

    /** Creates a label styled as a section header. */
    public static JLabel sectionHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_H2);
        lbl.setForeground(RTU_BLUE);
        return lbl;
    }

    /** Shows a standard error dialog. */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Shows a standard success dialog. */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Confirms an action with Yes/No dialog. Returns true if user chose Yes. */
    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(
            parent, message, "Confirm", JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }

    // Prevent instantiation
    private UIConstants() {}
}