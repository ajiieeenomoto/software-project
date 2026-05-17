package utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for BCrypt password hashing.
 * Requires jbcrypt-0.4.jar on the classpath.
 *
 * Maven dependency:
 *   <dependency>
 *     <groupId>org.mindrot</groupId>
 *     <artifactId>jbcrypt</artifactId>
 *     <version>0.4</version>
 *   </dependency>
 */
public class PasswordUtil {

    private static final int WORK_FACTOR = 12;

    /** Hashes a plain-text password using BCrypt. */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    /**
     * Verifies a plain-text password against a stored BCrypt hash.
     * Returns true if they match.
     */
    public static boolean verify(String plainPassword, String storedHash) {
        if (storedHash == null || !storedHash.startsWith("$2")) return false;
        return BCrypt.checkpw(plainPassword, storedHash);
    }

    // Prevent instantiation
    private PasswordUtil() {}
}