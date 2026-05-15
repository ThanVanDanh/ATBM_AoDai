package model.cart;

import model.product.Product;
import model.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart {
    Map<String, CartItem> cartItemMap;
    private User user;

    public Cart() {
        cartItemMap = new HashMap<>();
    }

    private static String normalizeKeyPart(String value) {
        return value == null ? "" : value.trim();
    }

    private static String buildKey(int productId, String sku) {
        return productId + "-" + normalizeKeyPart(sku);
    }

    public void addItem(Product product, int quantity, double price, String sku, String size) {
        if (quantity <= 0)
            quantity = 1;
        sku = normalizeKeyPart(sku);
        size = normalizeKeyPart(size);

        String key = buildKey(product.getId(), sku);

        CartItem item = cartItemMap.get(key);
        if (item != null) {
            item.upQuantity(quantity);
        } else {
            cartItemMap.put(key, new CartItem(quantity, product, price, sku, size));
        }
    }

    public void updateQuantity(int productId, String sku, int quantity) {
        if (sku == null)
            sku = "";
        sku = sku.trim();
        String key = productId + "-" + sku;

        CartItem item = cartItemMap.get(key);
        if (item != null) {
            if (quantity <= 0)
                cartItemMap.remove(key);
            else
                item.setQuantity(quantity);
        }
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItemMap.values()) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public int getTotalQuantity() {
        int total = 0;
        for (CartItem item : cartItemMap.values()) {
            total += item.getQuantity();
        }
        return total;
    }

    public void clear() {
        cartItemMap.clear();
    }

    public void remove(int productId, String sku) {

        String normalizedSku = normalizeKeyPart(sku);
        String keyToRemove = buildKey(productId, normalizedSku);

        if (cartItemMap.containsKey(keyToRemove)) {
            cartItemMap.remove(keyToRemove);
            System.out.println("Successfully removed!");
            return;
        }

        String prefix = productId + "-";
        String candidateKey = null;
        for (String key : cartItemMap.keySet()) {
            if (key.startsWith(prefix)) {
                if (candidateKey != null) {
                    candidateKey = null;
                    break;
                }
                candidateKey = key;
            }
        }

        if (candidateKey != null) {
            cartItemMap.remove(candidateKey);
            System.out.println("Removed by fallback key: [" + candidateKey + "]");
        } else {
            System.out.println("Key NOT FOUND in cart!");
        }
    }

    public CartItem get(String key) {
        return cartItemMap.get(key);
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(cartItemMap.values());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
