package model.article;

import model.AId;
import model.voucher.Voucher;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Article extends AId implements Serializable {
    @ColumnName("voucher_id")
    private Integer voucherId;
    @ColumnName("title")
    private String title;
    @ColumnName("content")
    private String content;
    @ColumnName("banner_image_url")
    private String bannerImageUrl;
    @ColumnName("status_articles")
    private String statusArticles;
    @ColumnName("start_date")
    private LocalDateTime startDate;
    @ColumnName("end_date")
    private LocalDateTime endDate;
    @ColumnName("created_at")
    private LocalDateTime createdAt;

    private Voucher voucher;

    public Article() {
    }

    public Article(int id) {
        super(id);
    }

    public Article(int id, Integer voucherId, String title, String content, String bannerImageUrl, String statusArticles, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime createdAt, Voucher voucher) {
        super(id);
        this.voucherId = voucherId;
        this.title = title;
        this.content = content;
        this.bannerImageUrl = bannerImageUrl;
        this.statusArticles = statusArticles;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.voucher = voucher;
    }

    public Integer getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Integer voucherId) {
        this.voucherId = voucherId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBannerImageUrl() {
        return bannerImageUrl;
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        this.bannerImageUrl = bannerImageUrl;
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

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", voucherId=" + voucherId +
                ", title='" + title + '\'' +
                ", statusArticles='" + statusArticles + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}