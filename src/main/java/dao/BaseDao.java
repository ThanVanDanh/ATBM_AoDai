package dao;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.jdbi.v3.core.Jdbi;

import java.sql.SQLException;

public abstract class BaseDao {
    private static Jdbi jdbi;

    public Jdbi get() {
        if (jdbi == null) {
            synchronized (BaseDao.class) {
                if (jdbi == null) {
                    connect();
                }
            }
        }
        return jdbi;
    }

    private void connect() {
        try {
            System.out.println("=== Creating Database Connection ===");

            MysqlDataSource ds = new MysqlDataSource();

            String url = String.format(
                    "jdbc:mysql://%s:%s/%s?" +
                            "useSSL=false&" +
                            "serverTimezone=Asia/Ho_Chi_Minh&" +
                            "allowPublicKeyRetrieval=true&" +
                            "autoReconnect=true&" +
                            "maxReconnects=3&" +
                            "initialTimeout=10&" +
                            "connectTimeout=30000",
                    DBProperties.host,
                    DBProperties.port,
                    DBProperties.dbname);

            System.out.println("Connection URL: " + url);

            ds.setURL(url);
            ds.setUser(DBProperties.username);
            ds.setPassword(DBProperties.password);

            ds.setUseCompression(true);
            jdbi = Jdbi.create(ds);


            jdbi.useHandle(handle -> {
                Integer result = handle.createQuery("SELECT 1 as test")
                        .mapTo(Integer.class)
                        .one();
            });


        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database connection failed: " + e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot initialize database: " + e.getMessage(), e);
        }
    }
}