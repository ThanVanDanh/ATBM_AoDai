package dao;

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
}