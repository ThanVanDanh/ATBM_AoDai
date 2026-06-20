package services;

import dao.KeyDao;

import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyService {
    private final KeyDao keyDao;

    public KeyService() {
        this.keyDao = new KeyDao();
    }

    public boolean registerUserKey(int userId, String publicKey) {
        if (publicKey == null || publicKey.trim().isEmpty()) {
            return false;
        }
        String trimmedKey = publicKey.trim();
        String validationError = validatePublicKeyPem(trimmedKey);
        if (validationError != null) {
            return false;
        }
        return keyDao.registerNewKey(userId, trimmedKey);
    }

    public static String validatePublicKeyPem(String pem) {
        if (pem == null || pem.trim().isEmpty()) {
            return "Khóa trống.";
        }

        String trimmed = pem.trim();

        if (!trimmed.contains("-----BEGIN PUBLIC KEY-----")) {
            return "Thiếu BEGIN PUBLIC KEY.";
        }
        if (!trimmed.contains("-----END PUBLIC KEY-----")) {
            return "Thiếu END PUBLIC KEY.";
        }
        if (trimmed.contains("-----BEGIN PRIVATE KEY-----")
                || trimmed.contains("-----BEGIN RSA PRIVATE KEY-----")) {
            return "Đây là Private Key, không phải Public Key.";
        }

        int startIdx = trimmed.indexOf("-----BEGIN PUBLIC KEY-----") + "-----BEGIN PUBLIC KEY-----".length();
        int endIdx = trimmed.indexOf("-----END PUBLIC KEY-----");
        String base64Body = trimmed.substring(startIdx, endIdx).replaceAll("\\s+", "");

        if (base64Body.isEmpty()) {
            return "Nội dung khóa trống.";
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(base64Body);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (IllegalArgumentException e) {
            return "Base64 không hợp lệ.";
        } catch (Exception e) {
            return "Định dạng RSA không hợp lệ.";
        }

        return null;
    }

    public boolean revokeUserKey(int userId) {
        return keyDao.revokeKey(userId);
    }
}