package model.home;

import model.AId;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.time.LocalDateTime;

public class Home extends AId {
    @ColumnName("section_type")
    private String sectionType;
    private int position;
    @ColumnName("category_id")
    private int categoryId;
    @ColumnName("section_title")
    private String sectionTitle;
    @ColumnName("updated_at")
    private LocalDateTime updatedAt;

    public Home(int id) {
        super(id);
    }

    public Home() {
    }

    public Home(int id, String sectionType, int position, int categoryId, String sectionTitle, LocalDateTime updatedAt) {
        super(id);
        this.sectionType = sectionType;
        this.position = position;
        this.categoryId = categoryId;
        this.sectionTitle = sectionTitle;
        this.updatedAt = updatedAt;
    }

    public String getSectionType() {
        return sectionType;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
