package bo.edu.uagrm.ugram.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Cryptographic hash utilities for EMR content integrity.
 * Used to generate SHA-256 hashes that are stored in PostgreSQL
 * and verified against the Blockchain ledger.
 */
public final class HashUtil {

    private HashUtil() {
        // Utility class
    }

    /**
     * Generates a SHA-256 hash of the given content.
     *
     * @param content the raw clinical record content (JSON string)
     * @return lowercase hex-encoded hash string
     */
    public static String sha256(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies that a content string matches an expected hash.
     */
    public static boolean verify(String content, String expectedHash) {
        return sha256(content).equals(expectedHash);
    }
}
