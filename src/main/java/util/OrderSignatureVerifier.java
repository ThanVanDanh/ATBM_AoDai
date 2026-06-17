package util;

import dao.KeyDao;
import dao.OrderDao;
import model.order.Order;
import model.order.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderSignatureVerifier {
    private final OrderDao orderDao;
    private final KeyDao keyDao;
    public OrderSignatureVerifier() {
        this.orderDao = new OrderDao();
        this.keyDao = new KeyDao();
    }
    public boolean verifyAndUpdateStatus(int orderId) throws Exception {
        Order order = orderDao.getOrderById(orderId);
        if (order == null) return false;

        if (order.getOrderHash() == null || order.getOrderHash().isBlank()
                || order.getOrderSignature() == null || order.getOrderSignature().isBlank()
                || order.getKeyId() == null) {
            return false;
        }
        List<OrderItem> items = orderDao.getOrderItems(orderId);
        List<OrderSignatureDataBuilder.SignableItem> signableItems = new ArrayList<>();
        for (OrderItem item : items) {
            signableItems.add(
                    new OrderSignatureDataBuilder.SignableItem(
                            item.getSku(),
                            item.getQuantity(),
                            item.getPriceAtPurchase()
                    )
            );
        }
        String currentSignedData = OrderSignatureDataBuilder.build(order, signableItems);
        String currentHash = SignatureUtil.sha256Hex(currentSignedData);

        if (!currentHash.equals(order.getOrderHash())) {
            orderDao.updateSignatureStatus(orderId, "invalid");
            return false;
        }
        String publicKey = keyDao.getPublicKeyById(order.getKeyId());
        if (publicKey == null || publicKey.isBlank()) {
            orderDao.updateSignatureStatus(orderId, "invalid");
            return false;
        }
        boolean valid = SignatureUtil.verifySignature(
                order.getOrderSignature(),
                order.getOrderHash(),
                publicKey
        );
        orderDao.updateSignatureStatus(orderId, valid ? "valid" : "invalid");
        return valid;



    }


}
