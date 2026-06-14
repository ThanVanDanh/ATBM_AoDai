package model.product;

import model.AId;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.io.Serializable;

public class ProductImage extends AId implements Serializable {
    @ColumnName("product_id")
    private int productId;
    @ColumnName("image_url")
    private String imageUrl;
    @ColumnName("alt_text")
    private String altText;
    @ColumnName("is_thumbnail")
    private boolean isThumbnail;

    public ProductImage(int id) {
        super(id);
    }
    public ProductImage() {
    }
    public ProductImage( int id, int productId, String imageUrl, String altText, boolean isThumbnail) {
        super(id);
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.altText = altText;
        this.isThumbnail = isThumbnail;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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

    public boolean isThumbnail() {
        return isThumbnail;
    }

    public boolean getIsThumbnail() {
        return isThumbnail;
    }

    public void setThumbnail(boolean thumbnail) {
        isThumbnail = thumbnail;
    }

    public void setIsThumbnail(boolean thumbnail) {
        isThumbnail = thumbnail;
    }
}
