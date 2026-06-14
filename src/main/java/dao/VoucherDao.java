package dao;

import model.voucher.Voucher;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class VoucherDao extends BaseDao {
    private final Jdbi jdbi;

    public VoucherDao() {
        this.jdbi = get();
    }

    public List<Voucher> getAll() {
        deactivateExpiredVouchers();

        String sql = "SELECT * FROM Vouchers ORDER BY created_at DESC";
        return jdbi.withHandle(handle -> handle.createQuery(sql)
                .mapToBean(Voucher.class)
                .list());
    }

    public List<Voucher> getActiveVouchers() {
        String sql = "SELECT * FROM Vouchers " +
                "WHERE is_active = 1 " +
                "AND valid_from <= NOW() " +
                "AND valid_to >= NOW() " +
                "AND current_usage < max_usage " +
                "ORDER BY created_at DESC";

        return jdbi.withHandle(handle -> handle.createQuery(sql)
                .mapToBean(Voucher.class)
                .list());
    }

    public Voucher getById(int id) {
        String sql = "SELECT * FROM Vouchers WHERE id = :id";
        return jdbi.withHandle(handle -> handle.createQuery(sql)
                .bind("id", id)
                .mapToBean(Voucher.class)
                .findFirst()
                .orElse(null));
    }

    public Voucher getByCode(String code) {
        String sql = "SELECT * FROM Vouchers WHERE voucher_code = :code";
        return jdbi.withHandle(handle -> handle.createQuery(sql)
                .bind("code", code)
                .mapToBean(Voucher.class)
                .findFirst()
                .orElse(null));
    }

    public int insert(Voucher voucher) {
        return jdbi.withHandle(handle -> insert(handle, voucher));
    }

    public int insert(Handle handle, Voucher voucher) {
        String sql = "INSERT INTO Vouchers " +
                "(voucher_code, discount_type, discount_value, min_order_amount, " +
                "max_usage, valid_from, valid_to, is_active) " +
                "VALUES (:code, :type, :value, :minAmount, :maxUsage, :from, :to, :active)";

        return handle.createUpdate(sql)
                .bind("code", voucher.getVoucherCode())
                .bind("type", voucher.getDiscountType())
                .bind("value", voucher.getDiscountValue())
                .bind("minAmount", voucher.getMinOrderAmount())
                .bind("maxUsage", voucher.getMaxUsage())
                .bind("from", voucher.getValidFrom())
                .bind("to", voucher.getValidTo())
                .bind("active", voucher.isActive())
                .executeAndReturnGeneratedKeys("id")
                .mapTo(int.class)
                .one();
    }

    public boolean update(Voucher voucher) {
        return jdbi.withHandle(handle -> update(handle, voucher));
    }

    public boolean update(Handle handle, Voucher voucher) {
        String sql = "UPDATE Vouchers SET " +
                "voucher_code = :code, " +
                "discount_type = :type, " +
                "discount_value = :value, " +
                "min_order_amount = :minAmount, " +
                "max_usage = :maxUsage, " +
                "current_usage = :currentUsage, " +
                "valid_from = :from, " +
                "valid_to = :to, " +
                "is_active = :active " +
                "WHERE id = :id";

        int rows = handle.createUpdate(sql)
                .bind("id", voucher.getId())
                .bind("code", voucher.getVoucherCode())
                .bind("type", voucher.getDiscountType())
                .bind("value", voucher.getDiscountValue())
                .bind("minAmount", voucher.getMinOrderAmount())
                .bind("maxUsage", voucher.getMaxUsage())
                .bind("currentUsage", voucher.getCurrentUsage())
                .bind("from", voucher.getValidFrom())
                .bind("to", voucher.getValidTo())
                .bind("active", voucher.isActive())
                .execute();

        return rows > 0;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Vouchers WHERE id = :id";
        return jdbi.withHandle(handle -> handle.createUpdate(sql)
                .bind("id", id)
                .execute() > 0);
    }

    public boolean incrementUsage(int voucherId) {
        String sql = "UPDATE Vouchers SET " +
                "current_usage = current_usage + 1, " +
                "is_active = CASE WHEN current_usage + 1 >= max_usage THEN 0 ELSE is_active END " +
                "WHERE id = :id";
        return jdbi.withHandle(handle -> handle.createUpdate(sql)
                .bind("id", voucherId)
                .execute() > 0);
    }

    public int deactivateExpiredVouchers() {
        String sql = "UPDATE Vouchers SET is_active = 0 " +
                "WHERE is_active = 1 AND (valid_to < NOW() OR current_usage >= max_usage)";

        return jdbi.withHandle(handle -> handle.createUpdate(sql).execute());
    }

    public boolean existsByCode(String code) {
        String sql = "SELECT COUNT(*) FROM Vouchers WHERE voucher_code = :code";
        Integer count = jdbi.withHandle(handle -> handle.createQuery(sql)
                .bind("code", code)
                .mapTo(Integer.class)
                .one());
        return count > 0;
    }

    public boolean existsByCodeExcept(String code, int excludeId) {
        String sql = "SELECT COUNT(*) FROM Vouchers WHERE voucher_code = :code AND id != :id";
        Integer count = jdbi.withHandle(handle -> handle.createQuery(sql)
                .bind("code", code)
                .bind("id", excludeId)
                .mapTo(Integer.class)
                .one());
        return count > 0;
    }
}