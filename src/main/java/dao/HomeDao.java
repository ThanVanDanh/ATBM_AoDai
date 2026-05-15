package dao;

import model.home.Home;
import org.jdbi.v3.core.Jdbi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeDao extends BaseDao {

    private final Jdbi jdbi;

    public HomeDao() {
        this.jdbi = get();
        ensureTable();
    }

    private void ensureTable() {
        try {
            jdbi.useHandle(handle -> {
                handle.execute(
                    "CREATE TABLE IF NOT EXISTS Home (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "section_type VARCHAR(50) NOT NULL," +
                    "position INT NOT NULL," +
                    "category_id INT NOT NULL," +
                    "section_title VARCHAR(255) NULL," +
                    "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE KEY uk_home_section_pos (section_type, position)" +
                    ")"
                );
            });
        } catch (Exception e) {
           return ;
        }
    }

    public String getSectionTitle(String sectionType) {
        try {
            String sql = "SELECT section_title FROM Home " +
                        "WHERE section_type = :type " +
                        "ORDER BY position ASC LIMIT 1";
            
            return jdbi.withHandle(handle -> 
                handle.createQuery(sql)
                    .bind("type", sectionType)
                    .mapTo(String.class)
                    .findOne()
                    .orElse(null)
            );
        } catch (Exception e) {
            return null;
        }
    }

    public List<Home> getSectionTabs(String sectionType) {
        try {
            String sql = "SELECT id, section_type, position, category_id, section_title, updated_at " +
                        "FROM Home " +
                        "WHERE section_type = :type " +
                        "ORDER BY position ASC";
            
            List<Home> tabs = jdbi.withHandle(handle -> 
                handle.createQuery(sql)
                    .bind("type", sectionType)
                    .map((rs, ctx) -> {
                        Home h = new Home();
                        h.setId(rs.getInt("id"));
                        h.setSectionType(rs.getString("section_type"));
                        h.setPosition(rs.getInt("position"));
                        h.setCategoryId(rs.getInt("category_id"));
                        h.setSectionTitle(rs.getString("section_title"));
                        return h;
                    })
                    .list()
            );

            return tabs;

        } catch (Exception e) {
            System.err.println("Error getting section tabs for " + sectionType + ": " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public boolean saveSection(String sectionType, String title, List<Home> tabs, int maxTabs) {
        try {
            String normalizedTitle = (title == null) ? "" : title.trim();
            List<Home> normalizedTabs = normalizeTabs(tabs, maxTabs);

            if (normalizedTabs.isEmpty()) {
                System.err.println(" No valid tabs to save for section: " + sectionType);
                return false;
            }

            return jdbi.withHandle(handle -> handle.inTransaction(h -> {
                // 1. Xóa dữ liệu cũ
                int deleted = h.createUpdate("DELETE FROM Home WHERE section_type = :type")
                    .bind("type", sectionType)
                    .execute();
                
                System.out.println("  Deleted " + deleted + " old records");

                for (Home tab : normalizedTabs) {
                    h.createUpdate(
                        "INSERT INTO Home " +
                        "(section_type, position, category_id, section_title) " +
                        "VALUES (:type, :pos, :cid, :title)"
                    )
                    .bind("type", sectionType)
                    .bind("pos", tab.getPosition())
                    .bind("cid", tab.getCategoryId())
                    .bind("title", normalizedTitle)
                    .execute();
                    
                    System.out.println("  Inserted: pos=" + tab.getPosition() + ", categoryId=" + tab.getCategoryId());
                }

                return true;
            }));

        } catch (Exception e) {
            System.err.println(" Error saving section " + sectionType + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private List<Home> normalizeTabs(List<Home> tabs, int maxTabs) {
        if (tabs == null) {
            return Collections.emptyList();
        }

        List<Home> result = new ArrayList<>();
        
        for (Home tab : tabs) {
            if (tab == null) continue;
            
            int position = tab.getPosition();
            int categoryId = tab.getCategoryId();

            if (position < 1 || position > maxTabs) {
                System.err.println("  ⚠ Skipped tab with invalid position: " + position);
                continue;
            }

            Home h = new Home();
            h.setPosition(position);
            h.setCategoryId(categoryId);
            result.add(h);
        }

        result.sort(Comparator.comparing(Home::getPosition));
        
        return result;
    }

    public boolean deleteSection(String sectionType) {
        try {
            int deleted = jdbi.withHandle(handle -> 
                handle.createUpdate("DELETE FROM Home WHERE section_type = :type")
                    .bind("type", sectionType)
                    .execute()
            );
            
            return deleted > 0;

        } catch (Exception e) {
            System.err.println(" Error deleting section " + sectionType + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getAllSectionKeys() {
        try {
            return jdbi.withHandle(handle -> 
                handle.createQuery("SELECT DISTINCT section_type FROM Home ORDER BY section_type")
                    .mapTo(String.class)
                    .list()
            );
        } catch (Exception e) {
            System.err.println(" Error getting all section keys: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
