package utils;

import java.util.regex.Pattern;

/**
 * Input validation helpers.
 */
public class InputValidator {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern STUDENT_NUM_PATTERN =
        Pattern.compile("^\\d{4}-\\d{5}$");   // 2021-00001

    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^(09|\\+639)\\d{9}$");

    /** Returns true if the string is null or blank. */
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidStudentNumber(String sn) {
        return sn != null && STUDENT_NUM_PATTERN.matcher(sn.trim()).matches();
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone == null || phone.isBlank()
            || PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Password must be ≥8 chars, with at least one letter and one digit.
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasLetter = false, hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit  = true;
        }
        return hasLetter && hasDigit;
    }

    public static boolean isPositiveInt(String s) {
        try { return Integer.parseInt(s) > 0; }
        catch (NumberFormatException e) { return false; }
    }

    // Prevent instantiation
    private InputValidator() {}
}