package model.product;

import model.AId;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.io.Serializable;

public class Category extends AId implements Serializable {
    @ColumnName("name_category")
    private String nameCategory;
    private String slug;
    private String description;
    @ColumnName("parent_category_id")
    private Integer parentId;

    public Category(int id) {
        super(id);
    }
    public Category() {}

    public Category(int id, String nameCategory, String slug, String description, Integer parentId) {
        super(id);
        this.nameCategory = nameCategory;
        this.slug = slug;
        this.description = description;
        this.parentId = parentId;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
