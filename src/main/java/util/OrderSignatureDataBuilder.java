package util;

import model.cart.CartItem;
import model.order.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class OrderSignatureDataBuilder {

    private OrderSignatureDataBuilder() {}


    public static class SignableItem {
        private String sku;
        private int quantity;
        private double price;

        public SignableItem(String sku, int quantity, double price) {
            this.sku = sku;
            this.quantity = quantity;
            this.price = price;
        }

        public String getSku() { return sku; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
    }

     //build chuỗi canonical để hash và hiển thị cho user ký
    public static String build(Order order, List<SignableItem> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("ORDER_CODE=").append(order.getOrderCode()).append("\n");
        sb.append("NAME=").append(nullSafe(order.getCustomerFullname())).append("\n");
        sb.append("PHONE=").append(nullSafe(order.getCustomerPhone())).append("\n");
        sb.append("EMAIL=").append(nullSafe(order.getCustomerEmail())).append("\n");
        sb.append("ADDRESS=").append(nullSafe(order.getShippingAddress())).append("\n");
        sb.append("SUBTOTAL=").append(formatAmount(order.getSubtotalAmount())).append("\n");
        sb.append("SHIPPING=").append(formatAmount(order.getShippingFee())).append("\n");
        sb.append("DISCOUNT=").append(formatAmount(order.getDiscountAmount())).append("\n");
        sb.append("TOTAL=").append(formatAmount(order.getTotalAmount())).append("\n");
        sb.append("PAYMENT=").append(nullSafe(order.getPaymentMethod())).append("\n");
        sb.append("ITEMS=");

        //mỗi item ngăn cách bằng |
        for (int i = 0; i < items.size(); i++) {
            SignableItem item = items.get(i);
            sb.append(nullSafe(item.getSku()))
              .append(":")
              .append(item.getQuantity())
              .append(":")
              .append(formatAmount(item.getPrice()));
            if (i < items.size() - 1) sb.append("|");
        }

        return sb.toString();
    }

    private static String nullSafe(String value) {
        return value != null ? value.trim() : "";
    }

    private static String formatAmount(double amount) {
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
