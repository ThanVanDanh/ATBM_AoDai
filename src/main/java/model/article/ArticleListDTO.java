package model.article;

import model.AId;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ArticleListDTO extends AId implements Serializable {
    @ColumnName("title")
    private String title;
    @ColumnName("status_articles")
    private String statusArticles;
    @ColumnName("start_date")
    private LocalDateTime startDate;
    @ColumnName("end_date")
    private LocalDateTime endDate;
    @ColumnName("created_at")
    private LocalDateTime createdAt;
    @ColumnName("banner_image_url")
    private String bannerImageUrl;

    @ColumnName("voucher_id")
    private Integer voucherId;
    @ColumnName("voucher_code")
    private String voucherCode;
    @ColumnName("discount_type")
    private String discountType;
    @ColumnName("discount_value")
    private String discountValue;

    public ArticleListDTO() {
        super();
    }

    public ArticleListDTO(int id) {
        super(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatusArticles() {
        return statusArticles;
    }

    public void setStatusArticles(String statusArticles) {
        this.statusArticles = statusArticles;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getBannerImageUrl() {
        return bannerImageUrl;
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        this.bannerImageUrl = bannerImageUrl;
    }

    public Integer getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Integer voucherId) {
        this.voucherId = voucherId;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }
}