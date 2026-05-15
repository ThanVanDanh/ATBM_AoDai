package model.product;

import model.AId;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.io.Serializable;

public class ProductVariant extends AId implements Serializable {
    @ColumnName("product_id")
    private int productId;
    private String sku;
    private String size;
    private String color;
    @ColumnName("current_price")
    private double currentPrice;
    @ColumnName("discounted_price")
    private double discountedPrice;
    @ColumnName("stock_quantity")
    private int stockQuantity;



    public ProductVariant(int id) {
        super(id);
    }

    public ProductVariant() {
    }

    public ProductVariant(int id, int productId, String sku, String size, String color, double currentPrice, double discountedPrice, int stockQuantity) {
        super(id);
        this.productId = productId;
        this.sku = sku;
        this.size = size;
        this.color = color;
        this.currentPrice = currentPrice;
        this.discountedPrice = discountedPrice;
        this.stockQuantity = stockQuantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
}
