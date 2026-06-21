package dao;

import java.util.Map;

public class KeyDao extends BaseDao {

    public boolean registerNewKey(int userId, String publicKey) {
        return get().inTransaction(handle -> {
            handle.createUpdate("UPDATE user_keys SET status = 'revoked', revoked_at = NOW() WHERE user_id = :userId AND status = 'active'")
                    .bind("userId", userId)
                    .execute();

            int rowsInserted = handle.createUpdate("INSERT INTO user_keys (user_id, public_key, status) VALUES (:userId, :publicKey, 'active')")
                    .bind("userId", userId)
                    .bind("publicKey", publicKey)
                    .execute();

            return rowsInserted > 0;
        });
    }

    public java.util.Map<String, Object> getActiveKeyInfo(int userId) {
        return get().withHandle(handle ->
                handle.createQuery("SELECT id, public_key, created_at FROM user_keys WHERE user_id = :userId AND status = 'active' ORDER BY created_at DESC LIMIT 1")
                        .bind("userId", userId)
                        .mapToMap()
                        .findFirst()
                        .orElse(null)
        );
    }

    public boolean revokeKey(int userId) {
        return get().withHandle(handle ->
                handle.createUpdate("UPDATE user_keys SET status = 'revoked', revoked_at = NOW() WHERE user_id = :userId AND status = 'active'")
                        .bind("userId", userId)
                        .execute() > 0
        );
    }

    public Integer getActiveKeyId(int userId) {
        return get().withHandle(handle ->
                handle.createQuery("SELECT id FROM user_keys WHERE user_id = :userId AND status = 'active' ORDER BY created_at DESC LIMIT 1")
                        .bind("userId", userId)
                        .mapTo(Integer.class)
                        .findFirst()
                        .orElse(null)
        );
    }

    public String getActivePublicKey(int userId) {
        return get().withHandle(handle ->
                handle.createQuery("SELECT public_key FROM user_keys WHERE user_id = :userId AND status = 'active' ORDER BY created_at DESC LIMIT 1")
                        .bind("userId", userId)
                        .mapTo(String.class)
                        .findFirst()
                        .orElse(null)
        );
    }

    public String getPublicKeyById(int keyId) {
        return get().withHandle(handle ->
                handle.createQuery("SELECT public_key FROM user_keys WHERE id = :keyId")
                        .bind("keyId", keyId)
                        .mapTo(String.class)
                        .findFirst()
                        .orElse(null)
        );
    }

    public Integer getUserIdByKeyId(int keyId) {
        return get().withHandle(handle ->
                handle.createQuery("SELECT user_id FROM user_keys WHERE id = :keyId")
                        .bind("keyId", keyId)
                        .mapTo(Integer.class)
                        .findFirst()
                        .orElse(null)
        );
    }

    public boolean isKeyActive(int keyId) {
        return get().withHandle(handle ->
                handle.createQuery("SELECT status FROM user_keys WHERE id = :keyId")
                        .bind("keyId", keyId)
                        .mapTo(String.class)
                        .findFirst()
                        .map(status -> "active".equalsIgnoreCase(status))
                        .orElse(false)
        );
    }

    public Map<String, Object> getKeyInfoById(int keyId) {
        return get().withHandle(handle ->
                handle.createQuery("SELECT public_key, status FROM user_keys WHERE id = :keyId")
                        .bind("keyId", keyId)
                        .mapToMap()
                        .findFirst()
                        .orElse(null)
        );
    }
}