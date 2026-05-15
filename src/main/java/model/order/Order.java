package model.order;

import model.AId;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Order extends AId implements Serializable {
    @ColumnName("user_id")
    private Integer userId;
    @ColumnName("order_code")
    private String orderCode;
    @ColumnName("customer_fullname")
    private String customerFullname;
    @ColumnName("customer_email")
    private String customerEmail;
    @ColumnName("customer_phone")
    private String customerPhone;
    @ColumnName("shipping_address")
    private String shippingAddress;
    @ColumnName("customer_note")
    private String customerNote;
    @ColumnName("cancel_reason")
    private String cancelReason;
    @ColumnName("subtotal_amount")
    private double subtotalAmount;
    @ColumnName("shipping_fee")
    private double shippingFee;
    @ColumnName("discount_amount")
    private double discountAmount;
    @ColumnName("total_amount")
    private double totalAmount;
    @ColumnName("voucher_id")
    private Integer voucherId;
    @ColumnName("order_status")
    private String orderStatus;
    @ColumnName("payment_method")
    private String paymentMethod;
    @ColumnName("payment_status")
    private String paymentStatus;
    @ColumnName("created_at")
    private LocalDateTime createdAt;
    @ColumnName("updated_at")
    private LocalDateTime updatedAt;

    private String formattedCreatedAt;

    public Order() {
    }

    public Integer getUserId() {
        return userId;
    }

    @ColumnName("user_id")
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    @ColumnName("order_code")
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getCustomerFullname() {
        return customerFullname;
    }

    @ColumnName("customer_fullname")
    public void setCustomerFullname(String customerFullname) {
        this.customerFullname = customerFullname;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    @ColumnName("customer_email")
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    @ColumnName("customer_phone")
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    @ColumnName("shipping_address")
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCustomerNote() {
        return customerNote;
    }

    @ColumnName("customer_note")
    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    @ColumnName("cancel_reason")
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public double getSubtotalAmount() {
        return subtotalAmount;
    }

    @ColumnName("subtotal_amount")
    public void setSubtotalAmount(double subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    @ColumnName("shipping_fee")
    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    @ColumnName("discount_amount")
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    @ColumnName("total_amount")
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getVoucherId() {
        return voucherId;
    }

    @ColumnName("voucher_id")
    public void setVoucherId(Integer voucherId) {
        this.voucherId = voucherId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    @ColumnName("order_status")
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    @ColumnName("payment_method")
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    @ColumnName("payment_status")
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @ColumnName("created_at")
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @ColumnName("updated_at")
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFormattedCreatedAt() {
        if (createdAt == null)
            return "Không rõ";
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                .ofPattern("dd-MM-yyyy HH:mm:ss");
        return createdAt.format(formatter);
    }

    public void setFormattedCreatedAt(String formattedCreatedAt) {
        this.formattedCreatedAt = formattedCreatedAt;
    }

    public String getFormattedUpdatedAt() {
        if (updatedAt == null)
            return "Không rõ";
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                .ofPattern("dd-MM-yyyy HH:mm:ss");
        return updatedAt.format(formatter);
    }
}
