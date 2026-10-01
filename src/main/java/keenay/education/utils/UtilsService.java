package keenay.education.utils;

import keenay.education.exception.errors.InternalException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UtilsService {

    public static <T> boolean in(List<T> array, T elem) {
        for (T elements : array) {
            if (elements.equals(elem)) {
                return true;
            }
        }
        return false;
    }

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new InternalException("SHA-256 not available");
        }
    }

}
