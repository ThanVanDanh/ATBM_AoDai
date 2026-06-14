package dao;

import model.banner.Banner;
import org.jdbi.v3.core.Jdbi;

import java.util.Collections;
import java.util.List;

public class BannerDao extends BaseDao {

    private final Jdbi jdbi;

    public BannerDao() {
        this.jdbi = get();
        ensureTable();
    }

    private void ensureTable() {
        try {
            jdbi.useHandle(handle -> {
                handle.execute(
                    "CREATE TABLE IF NOT EXISTS banners (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "image_url VARCHAR(500) NOT NULL," +
                    "alt_text VARCHAR(255) NULL," +
                    "sort_order INT NOT NULL DEFAULT 0," +
                    "is_active TINYINT(1) NOT NULL DEFAULT 1," +
                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                    ")"
                );

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<Banner> getActiveBanners() {
        try {
            String sql = "SELECT id, image_url, alt_text, sort_order, is_active, created_at " +
                        "FROM banners WHERE is_active = 1 ORDER BY sort_order ASC";
            
            return jdbi.withHandle(handle -> 
                handle.createQuery(sql)
                    .map((rs, ctx) -> {
                        Banner b = new Banner();
                        b.setId(rs.getInt("id"));
                        b.setImageUrl(rs.getString("image_url"));
                        b.setAltText(rs.getString("alt_text"));
                        b.setSortOrder(rs.getInt("sort_order"));
                        b.setActive(rs.getBoolean("is_active"));
                        b.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        return b;
                    })
                    .list()
            );
        } catch (Exception e) {
            System.err.println(" Error getting active banners: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<BannerDTO> getAllBanners() {
        try {
            String sql = "SELECT id, image_url, alt_text, sort_order, is_active, created_at " +
                        "FROM banners ORDER BY sort_order ASC, id ASC";
            
            return jdbi.withHandle(handle -> 
                handle.createQuery(sql)
                    .map((rs, ctx) -> {
                        BannerDTO b = new BannerDTO();
                        b.id = rs.getInt("id");
                        b.imageUrl = rs.getString("image_url");
                        b.altText = rs.getString("alt_text");
                        b.sortOrder = rs.getInt("sort_order");
                        b.isActive = rs.getBoolean("is_active");
                        return b;
                    })
                    .list()
            );
        } catch (Exception e) {
            System.err.println(" Error getting all banners: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public BannerDTO getById(int id) {
        try {
            String sql = "SELECT id, image_url, alt_text, sort_order, is_active " +
                        "FROM banners WHERE id = :id";
            
            return jdbi.withHandle(handle -> 
                handle.createQuery(sql)
                    .bind("id", id)
                    .map((rs, ctx) -> {
                        BannerDTO b = new BannerDTO();
                        b.id = rs.getInt("id");
                        b.imageUrl = rs.getString("image_url");
                        b.altText = rs.getString("alt_text");
                        b.sortOrder = rs.getInt("sort_order");
                        b.isActive = rs.getBoolean("is_active");
                        return b;
                    })
                    .findOne()
                    .orElse(null)
            );
        } catch (Exception e) {
            System.err.println(" Error getting banner by id: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public int insert(String imageUrl, String altText, int sortOrder, boolean isActive) {
        try {
            String sql = "INSERT INTO banners (image_url, alt_text, sort_order, is_active) " +
                        "VALUES (:imageUrl, :altText, :sortOrder, :isActive)";
            
            return jdbi.withHandle(handle -> 
                handle.createUpdate(sql)
                    .bind("imageUrl", imageUrl)
                    .bind("altText", altText)
                    .bind("sortOrder", sortOrder)
                    .bind("isActive", isActive)
                    .executeAndReturnGeneratedKeys("id")
                    .mapTo(Integer.class)
                    .one()
            );
        } catch (Exception e) {
            System.err.println(" Error inserting banner: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public boolean update(int id, String imageUrl, String altText, int sortOrder, boolean isActive) {
        try {
            String sql = "UPDATE banners SET image_url = :imageUrl, " +
                        "alt_text = :altText, sort_order = :sortOrder, is_active = :isActive " +
                        "WHERE id = :id";
            
            int updated = jdbi.withHandle(handle -> 
                handle.createUpdate(sql)
                    .bind("id", id)
                    .bind("imageUrl", imageUrl)
                    .bind("altText", altText)
                    .bind("sortOrder", sortOrder)
                    .bind("isActive", isActive)
                    .execute()
            );
            return updated > 0;
        } catch (Exception e) {
            System.err.println(" Error updating banner: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateActive(int id, boolean isActive) {
        try {
            String sql = "UPDATE banners SET is_active = :isActive WHERE id = :id";
            
            int updated = jdbi.withHandle(handle -> 
                handle.createUpdate(sql)
                    .bind("id", id)
                    .bind("isActive", isActive)
                    .execute()
            );
            return updated > 0;
        } catch (Exception e) {
            System.err.println(" Error updating banner active status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        try {
            String sql = "DELETE FROM banners WHERE id = :id";
            
            int deleted = jdbi.withHandle(handle -> 
                handle.createUpdate(sql)
                    .bind("id", id)
                    .execute()
            );
            return deleted > 0;
        } catch (Exception e) {
            System.err.println(" Error deleting banner: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int getNextSortOrder() {
        try {
            String sql = "SELECT COALESCE(MAX(sort_order), 0) + 1 FROM banners";
            return jdbi.withHandle(handle -> 
                handle.createQuery(sql)
                    .mapTo(Integer.class)
                    .one()
            );
        } catch (Exception e) {
            return 1;
        }
    }

    public static class BannerDTO {
        public int id;
        public String imageUrl;
        public String altText;
        public int sortOrder;
        public boolean isActive;
    }
}
