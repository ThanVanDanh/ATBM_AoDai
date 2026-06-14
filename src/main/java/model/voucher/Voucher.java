package model.voucher;

import model.AId;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Voucher extends AId implements Serializable {
    @ColumnName("voucher_code")
    private String voucherCode;
    @ColumnName("discount_type")
    private String discountType;
    @ColumnName("discount_value")
    private double discountValue;
    @ColumnName("min_order_amount")
    private double minOrderAmount;
    @ColumnName("max_usage")
    private int maxUsage;
    @ColumnName("current_usage")
    private int currentUsage;
    @ColumnName("valid_from")
    private LocalDateTime validFrom;
    @ColumnName("valid_to")
    private LocalDateTime validTo;
    @ColumnName("is_active")
    private boolean isActive;
    @ColumnName("created_at")
    private LocalDateTime createdAt;

    public Voucher() {
    }

    public Voucher(int id) {
        super(id);
    }

    public Voucher(int id, LocalDateTime createdAt, boolean isActive, LocalDateTime validTo, LocalDateTime validFrom,
            int currentUsage, int maxUsage, double minOrderAmount, double discountValue, String discountType,
            String voucherCode) {
        super(id);
        this.createdAt = createdAt;
        this.isActive = isActive;
        this.validTo = validTo;
        this.validFrom = validFrom;
        this.currentUsage = currentUsage;
        this.maxUsage = maxUsage;
        this.minOrderAmount = minOrderAmount;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.voucherCode = voucherCode;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    @ColumnName("voucher_code")
    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getDiscountType() {
        return discountType;
    }

    @ColumnName("discount_type")
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    @ColumnName("discount_value")
    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public double getMinOrderAmount() {
        return minOrderAmount;
    }

    @ColumnName("min_order_amount")
    public void setMinOrderAmount(double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public int getMaxUsage() {
        return maxUsage;
    }

    @ColumnName("max_usage")
    public void setMaxUsage(int maxUsage) {
        this.maxUsage = maxUsage;
    }

    public int getCurrentUsage() {
        return currentUsage;
    }

    @ColumnName("current_usage")
    public void setCurrentUsage(int currentUsage) {
        this.currentUsage = currentUsage;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    @ColumnName("valid_from")
    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    @ColumnName("valid_to")
    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public boolean isActive() {
        return isActive;
    }

    @ColumnName("is_active")
    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @ColumnName("created_at")
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Voucher{" +
                "id=" + id +
                ", voucherCode='" + voucherCode + '\'' +
                ", discountType='" + discountType + '\'' +
                ", discountValue=" + discountValue +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                ", isActive=" + isActive +
                '}';
    }

    public String getFormattedValidFrom() {
        if (validFrom == null)
            return "";
        return validFrom.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    public String getFormattedValidTo() {
        if (validTo == null)
            return "";
        return validTo.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    public String getFormattedCreatedAt() {
        if (createdAt == null)
            return "";
        return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }
    public String getFormattedValidToDate() {
        if (validTo == null)
            return "";
        return validTo.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String getFormattedValidFromDate() {
        if (validFrom == null)
            return "";
        return validFrom.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
