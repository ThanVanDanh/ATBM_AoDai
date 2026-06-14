package dao;

import model.contact.ContactUs;

import java.util.List;

public class ContactDao extends BaseDao {
    public int insert(ContactUs contact) {
        String sql = "INSERT INTO Contact_messages (full_name, email, message_body) VALUES (:fullName, :email, :messageBody)";

            return get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bindBean(contact)
                            .execute()
            );
        }
    public List<ContactUs> getAllMessages() {
        String sql = "SELECT * FROM Contact_messages order by received_at desc";
        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .mapToBean(ContactUs.class)
                        .list()
        );

    }
    public ContactUs getById(int id) {
        String sql = "SELECT * FROM Contact_messages WHERE id = :id";
        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("id", id)
                        .mapToBean(ContactUs.class)
                        .findFirst()
                        .orElse(null)
        );
    }
    public void delete(int id) {
        String sql = "DELETE FROM Contact_messages WHERE id = :id";
        get().withHandle(handle ->
                handle.createUpdate(sql)
                        .bind("id", id)
                        .execute()
        );
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE Contact_messages SET status_message = :status WHERE id = :id";

        return get().withHandle(handle ->
                handle.createUpdate(sql)
                        .bind("status", status)
                        .bind("id", id)
                        .execute() > 0
        );
    }

}

