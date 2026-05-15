package dao;

import model.policy.Policy;
import org.jdbi.v3.core.Handle;

import java.util.List;

public class PolicyDao extends BaseDao {

    public List<Policy> getAll() {
        String sql = "SELECT * FROM policy ORDER BY id DESC";
        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .mapToBean(Policy.class)
                        .list()
        );
    }

    public Policy getById(int id) {
        String sql = "SELECT * FROM policy WHERE id = :id";
        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("id", id)
                        .mapToBean(Policy.class)
                        .findFirst()
                        .orElse(null)
        );
    }

    public Policy getByCategoryId(int categoryId) {
        String sql = "SELECT * FROM policy WHERE category_id = :categoryId";
        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("categoryId", categoryId)
                        .mapToBean(Policy.class)
                        .findFirst()
                        .orElse(null)
        );
    }

    public boolean exists(int id) {
        String sql = "SELECT COUNT(*) FROM policy WHERE id = :id";
        return get().withHandle(handle -> {
            Integer count = handle.createQuery(sql)
                    .bind("id", id)
                    .mapTo(Integer.class)
                    .one();
            return count > 0;
        });
    }

    public boolean existsByCategoryId(int categoryId) {
        String sql = "SELECT COUNT(*) FROM policy WHERE category_id = :categoryId";
        return get().withHandle(handle -> {
            Integer count = handle.createQuery(sql)
                    .bind("categoryId", categoryId)
                    .mapTo(Integer.class)
                    .one();
            return count > 0;
        });
    }

    public int insert(Policy policy) {
        return get().withHandle(handle -> insert(handle, policy));
    }

    public int insert(Handle handle, Policy policy) {
        String sql = "INSERT INTO policy (category_id, policy_text) VALUES (:categoryId, :policyText)";
        return handle.createUpdate(sql)
                .bind("categoryId", policy.getCategoryId())
                .bind("policyText", policy.getPolicyText())
                .executeAndReturnGeneratedKeys("id")
                .mapTo(int.class)
                .one();
    }

    public boolean update(Policy policy) {
        return get().withHandle(handle -> update(handle, policy));
    }

    public boolean update(Handle handle, Policy policy) {
        String sql = "UPDATE policy SET category_id = :categoryId, policy_text = :policyText WHERE id = :id";
        int rows = handle.createUpdate(sql)
                .bind("id", policy.getId())
                .bind("categoryId", policy.getCategoryId())
                .bind("policyText", policy.getPolicyText())
                .execute();
        return rows > 0;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM policy WHERE id = :id";
        return get().withHandle(handle -> {
            int rows = handle.createUpdate(sql)
                    .bind("id", id)
                    .execute();
            return rows > 0;
        });
    }

    public boolean deleteByCategoryId(int categoryId) {
        String sql = "DELETE FROM policy WHERE category_id = :categoryId";
        return get().withHandle(handle -> {
            int rows = handle.createUpdate(sql)
                    .bind("categoryId", categoryId)
                    .execute();
            return rows > 0;
        });
    }
}
