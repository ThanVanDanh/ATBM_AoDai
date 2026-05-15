package dao;

import model.user.Address;
import org.jdbi.v3.core.Handle;
import java.util.List;

public class AddressDao extends BaseDao {
    public List<Address> findByUserId(int userId) {
        return get().withHandle(handle ->
                handle.createQuery("SELECT * FROM Addresses WHERE user_id = :userId")
                        .bind("userId", userId)
                        .mapToBean(Address.class)
                        .list()
        );
    }
    public boolean insertAddress(Address address) {
        return get().inTransaction(handle -> {
            if (address.getIsDefault()) {
                handle.createUpdate("UPDATE Addresses SET is_default = 0 WHERE user_id = :userId")
                        .bind("userId", address.getUserId())
                        .execute();
            }

            int rows = handle.createUpdate("INSERT INTO Addresses (user_id, recipient_name, recipient_phone, address_line, city_province, country, is_default) " +
                            "VALUES (:userId, :recipientName, :recipientPhone, :addressLine, :cityProvince, :country, :isDefault)")
                    .bindBean(address)
                    .execute();

            return rows > 0;
        });
    }
    public boolean updateAddress(Address address) {
        return get().inTransaction(handle -> {
            if (address.getIsDefault()) {
                handle.createUpdate("UPDATE Addresses SET is_default = 0 WHERE user_id = :userId")
                        .bind("userId", address.getUserId())
                        .execute();
            }

            int rows = handle.createUpdate("UPDATE Addresses SET recipient_name = :name, recipient_phone = :phone, " +
                            "address_line = :addr, city_province = :city, country = :country, is_default = :isDefault " +
                            "WHERE id = :id AND user_id = :userId")
                    .bind("name", address.getRecipientName())
                    .bind("phone", address.getRecipientPhone())
                    .bind("addr", address.getAddressLine())
                    .bind("city", address.getCityProvince())
                    .bind("country", address.getCountry())
                    .bind("isDefault", address.getIsDefault())
                    .bind("id", address.getId())
                    .bind("userId", address.getUserId())
                    .execute();

            return rows > 0;
        });
    }
    public boolean deleteAddress(int id, int userId) {
        return get().withHandle(handle ->
                handle.createUpdate("DELETE FROM Addresses WHERE id = :id AND user_id = :userId")
                        .bind("id", id)
                        .bind("userId", userId)
                        .execute() > 0
        );
    }
}
