package model.cart;

import model.product.Product;

import java.io.Serializable;

public class CartItem implements Serializable {
    private int quantity;
    private Product product;
    private double price;
    private String sku;
    private String size;

    public CartItem(int quantity, Product product, double price,String sku, String size) {
        this.quantity = quantity;
        this.product = product;
        this.price = price;
        this.sku = sku;
        this.size = size;
    }
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public void upQuantity(int quantity) {
        this.quantity += quantity;
    }
    public double getTotalPrice() {
        return this.quantity * this.price;
    }
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


}
