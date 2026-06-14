package model.home;

import model.product.ProductListDTO;

import java.util.ArrayList;
import java.util.List;


public class TabDTO {
    public int index;
    public String title;
    public List<ProductListDTO> products;

    public TabDTO() {
        this.products = new ArrayList<>();
    }

    public TabDTO(int index, String title) {
        this.index = index;
        this.title = title;
        this.products = new ArrayList<>();
    }

    public int getIndex() { return index; }
    public String getTitle() { return title; }
    public List<ProductListDTO> getProducts() { return products; }

    public void setIndex(int index) { this.index = index; }
    public void setTitle(String title) { this.title = title; }
    public void setProducts(List<ProductListDTO> products) { this.products = products; }

    @Override
    public String toString() {
        return "TabDTO{" +
                "index=" + index +
                ", title='" + title + '\'' +
                ", products=" + products.size() +
                '}';
    }
}
