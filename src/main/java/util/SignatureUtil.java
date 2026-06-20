package util;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SignatureUtil {

    private SignatureUtil() {}


    //tính SHA-256 của chuỗi đầu vào
    public static String sha256Hex(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    //verify chữ ký RSA
    public static boolean verifySignature(String signatureBase64, String orderHash, String publicKeyPem) throws Exception {
        PublicKey publicKey = parsePemPublicKey(publicKeyPem);

        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64.trim());

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(orderHash.getBytes(StandardCharsets.UTF_8));

        return sig.verify(signatureBytes);
    }

    public static PublicKey parsePemPublicKey(String pem) throws Exception {
        String stripped = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(stripped);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}
