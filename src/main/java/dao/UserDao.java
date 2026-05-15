package dao;

import java.util.List;

import model.user.User;
import org.jdbi.v3.core.Jdbi;

public class UserDao extends BaseDao {
    private final Jdbi jdbi;
    public UserDao() {
        this.jdbi = get();
    }
    public User findByEmailOrPhone(String key) {
        return get().withHandle(handle -> handle
                .createQuery("SELECT * FROM Users WHERE email = :key OR phone_number = :key").bind("key", key)
                .mapToBean(User.class).stream().findFirst().orElse(null));
    }

    public User findByEmail(String email) {
        return get().withHandle(handle -> handle.createQuery("SELECT * FROM Users WHERE email = :email")
                .bind("email", email)
                .mapToBean(User.class)
                .findFirst()
                .orElse(null));
    }

    public User findByPhone(String phone) {
        return get().withHandle(handle -> handle.createQuery("SELECT * FROM Users WHERE phone_number = :phone")
                .bind("phone", phone)
                .mapToBean(User.class)
                .findFirst()
                .orElse(null));
    }

    public int insert(User user) {
        String sql = "INSERT INTO Users (full_name, phone_number, email, password_hash, role_user, auth_provider, firebase_uid, account_status, verify_token) "
                +
                "VALUES (:fullName, :phone, :email, :password, :role, :authProvider, :firebaseUID, :status, :verifyToken)";
        return get().withHandle(handle -> handle.createUpdate(sql)
                .bindBean(user)
                .executeAndReturnGeneratedKeys("id")
                .mapTo(int.class)
                .one());
    }

    public boolean updatePassword(String email, String newPasswordHash) {
        return get().withHandle(
                handle -> handle.createUpdate("UPDATE Users SET password_hash = :password WHERE email = :email")
                        .bind("password", newPasswordHash)
                        .bind("email", email)
                        .execute() > 0);
    }

    public boolean activateUser(String token) {
        return get().withHandle(handle -> handle
                .createUpdate(
                        "UPDATE Users SET account_status = 'active', verify_token = NULL WHERE verify_token = :token")
                .bind("token", token)
                .execute() > 0);
    }

    public List<User> findAll() {
        return get().withHandle(handle -> handle.createQuery("SELECT * FROM Users")
                .mapToBean(User.class)
                .list());
    }

    public void updateRole(int userId, String role) {
        get().useHandle(handle -> {
            handle.createUpdate("UPDATE Users SET role_user = :role WHERE id = :id")
                    .bind("role", role)
                    .bind("id", userId)
                    .execute();
        });
    }

    public int countAll() {
        return get().withHandle(handle -> handle.createQuery("SELECT COUNT(*) FROM Users")
                .mapTo(int.class)
                .one());
    }

    public int countNewThisWeek() {
        return get().withHandle(handle -> handle
                .createQuery("SELECT COUNT(*) FROM Users WHERE YEARWEEK(created_at, 1) = YEARWEEK(CURDATE(), 1)")
                .mapTo(int.class)
                .one());
    }

    public boolean updateStatus(int userId, String status) {
        return get().withHandle(handle -> {
            return handle.createUpdate("UPDATE Users SET account_status = :status WHERE id = :id")
                    .bind("status", status)
                    .bind("id", userId)
                    .execute() > 0;
        });
    }

    public boolean delete(int userId) {
        return get().withHandle(handle -> handle.createUpdate("DELETE FROM Users WHERE id = :id")
                .bind("id", userId)
                .execute() > 0);
    }

    public List<User> getNewCustomers(int limit) {
        return get()
                .withHandle(handle -> handle.createQuery("SELECT * FROM Users ORDER BY created_at DESC LIMIT :limit")
                        .bind("limit", limit)
                        .mapToBean(User.class)
                        .list());
    }
    public int countTotalUsers() {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT COUNT(*) FROM Users")
                        .mapTo(Integer.class)
                        .one()
        );
    }
    public List<User> getUsersPagination(int limit, int offset) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM Users ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
                        .bind("limit", limit)
                        .bind("offset", offset)
                        .mapToBean(User.class)
                        .list()
        );
    }
    public int countUsersByFilter(String search, String status) {
        String sql = "SELECT COUNT(*) FROM Users WHERE 1=1";
        if (search != null && !search.isEmpty()) {
            sql += " AND (full_name LIKE :search OR email LIKE :search)";
        }
        if (status != null && !status.isEmpty()) {
            sql += " AND account_status = :status";
        }

        String finalSql = sql;
        return jdbi.withHandle(handle -> {
            var query = handle.createQuery(finalSql);
            if (search != null && !search.isEmpty()) query.bind("search", "%" + search + "%");
            if (status != null && !status.isEmpty()) query.bind("status", status);
            return query.mapTo(Integer.class).one();
        });
    }
    public List<User> getUsersPaginationAndFilter(int limit, int offset, String search, String status) {
        String sql = "SELECT * FROM Users WHERE 1=1";

        if (search != null && !search.isEmpty()) {
            sql += " AND (full_name LIKE :search OR email LIKE :search)";
        }
        if (status != null && !status.isEmpty()) {
            sql += " AND account_status = :status";
        }

        sql += " ORDER BY created_at DESC LIMIT :limit OFFSET :offset";

        String finalSql = sql;
        return jdbi.withHandle(handle -> {
            var query = handle.createQuery(finalSql)
                    .bind("limit", limit)
                    .bind("offset", offset);

            if (search != null && !search.isEmpty()) query.bind("search", "%" + search + "%");
            if (status != null && !status.isEmpty()) query.bind("status", status);

            return query.mapToBean(User.class).list();
        });
    }
}
