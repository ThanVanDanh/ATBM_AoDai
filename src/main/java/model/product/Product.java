package model.product;

import model.AId;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Product extends AId implements Serializable {
    @ColumnName("name_product")
    private String nameProduct;
    @ColumnName("product_code")
    private String productCode;
    @ColumnName("description")
    private String description;
    @ColumnName("created_at")
    private LocalDateTime createdAt;
    @ColumnName("status_product")
    private String statusProduct;
    @ColumnName("category_id")
    private int categoryId;
    private List<ProductVariant> variants;
    private List<ProductImage> images;

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public Product(int id) {
        super(id);
    }

    public Product() {
    }

    public Product(int id, String nameProduct, String productCode, String description, LocalDateTime createdAt,
            String statusProduct, int categoryId) {
        super(id);
        this.nameProduct = nameProduct;
        this.productCode = productCode;
        this.description = description;
        this.createdAt = createdAt;
        this.statusProduct = statusProduct;
        this.categoryId = categoryId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getStatusProduct() {
        return statusProduct;
    }

    public void setStatusProduct(String statusProduct) {
        this.statusProduct = statusProduct;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }
}
