package model.banner;

import model.AId;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.time.LocalDateTime;

public class Banner extends AId {
    @ColumnName("image_url")
    private String imageUrl;
    @ColumnName("alt_text")
    private String altText;
    @ColumnName("sort_order")
    private int sortOrder;
    @ColumnName("is_active")
    private boolean isActive;
    @ColumnName("create_at")
    private LocalDateTime createdAt;

    public Banner(int id) {
        super(id);
    }

    public Banner() {
    }

    public Banner(int id, String imageUrl, String altText, int sortOrder, boolean isActive, LocalDateTime createdAt) {
        super(id);
        this.imageUrl = imageUrl;
        this.altText = altText;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
