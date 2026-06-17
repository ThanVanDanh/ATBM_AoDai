package model.order;

import model.AId;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.io.Serializable;

public class OrderItem extends AId implements Serializable {
    @ColumnName("order_id")
    private int orderId;
    @ColumnName("variant_id")
    private int variantId;
    @ColumnName("quantity")
    private int quantity;
    @ColumnName("price_at_purchase")
    private double priceAtPurchase;
    @ColumnName("product_name_at_purchase")
    private String productNameAtPurchase;
    @ColumnName("product_code_at_purchase")
    private String productCodeAtPurchase;
    @ColumnName("variant_sku_at_purchase")
    private String variantSkuAtPurchase;
    @ColumnName("size_at_purchase")
    private String sizeAtPurchase;
    @ColumnName("color_at_purchase")
    private String colorAtPurchase;
    @ColumnName("line_total_at_purchase")
    private double lineTotalAtPurchase;

    public OrderItem() {
    }

    public OrderItem(int orderId, int variantId, int quantity, double priceAtPurchase) {
        this.orderId = orderId;
        this.variantId = variantId;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(double priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    public String getProductNameAtPurchase() {
        return productNameAtPurchase;
    }

    public void setProductNameAtPurchase(String productNameAtPurchase) {
        this.productNameAtPurchase = productNameAtPurchase;
    }

    public String getProductCodeAtPurchase() {
        return productCodeAtPurchase;
    }

    public void setProductCodeAtPurchase(String productCodeAtPurchase) {
        this.productCodeAtPurchase = productCodeAtPurchase;
    }

    public String getVariantSkuAtPurchase() {
        return variantSkuAtPurchase;
    }

    public void setVariantSkuAtPurchase(String variantSkuAtPurchase) {
        this.variantSkuAtPurchase = variantSkuAtPurchase;
    }

    public String getSizeAtPurchase() {
        return sizeAtPurchase;
    }

    public void setSizeAtPurchase(String sizeAtPurchase) {
        this.sizeAtPurchase = sizeAtPurchase;
    }

    public String getColorAtPurchase() {
        return colorAtPurchase;
    }

    public void setColorAtPurchase(String colorAtPurchase) {
        this.colorAtPurchase = colorAtPurchase;
    }

    public double getLineTotalAtPurchase() {
        return lineTotalAtPurchase;
    }

    public void setLineTotalAtPurchase(double lineTotalAtPurchase) {
        this.lineTotalAtPurchase = lineTotalAtPurchase;
    }

    private String productName;
    private String productImage;
    private String size;
    private String sku;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
