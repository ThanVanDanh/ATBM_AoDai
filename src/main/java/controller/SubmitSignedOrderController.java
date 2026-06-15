package controller;

import dao.KeyDao;
import dao.OrderDao;
import dao.VoucherDao;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import model.cart.CartItem;
import model.order.Order;
import model.user.User;
import util.SignatureUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(name = "SubmitSignedOrderController", urlPatterns = { "/submit-signed-order", "/cancel-pending-order" })
public class SubmitSignedOrderController extends HttpServlet {
    private OrderDao orderDao;
    private KeyDao keyDao;
    private VoucherDao voucherDao;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.orderDao = new OrderDao();
            this.keyDao = new KeyDao();
            this.voucherDao = new VoucherDao();
        } catch (Exception ex) {
            throw new ServletException("Failed to initialize SubmitSignedOrderController: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        if ("/cancel-pending-order".equals(req.getServletPath())) {
            clearPendingSession(req.getSession());
            resp.getWriter().write("{\"success\":true}");
            return;
        }

        handleSubmit(req, resp);
    }

    @SuppressWarnings("unchecked")
    private void handleSubmit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("account");

        if (user == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Phiên đăng nhập đã hết hạn.\"}");
            return;
        }

        Order pendingOrder = (Order) session.getAttribute("pendingOrder");
        List<CartItem> pendingItems = (List<CartItem>) session.getAttribute("pendingItems");
        String orderHash = (String) session.getAttribute("pendingOrderHash");
        String canonicalData = (String) session.getAttribute("pendingCanonicalData");
        Integer keyId = (Integer) session.getAttribute("pendingKeyId");

        if (pendingOrder == null || pendingItems == null || orderHash == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Không tìm thấy đơn hàng đang chờ. Vui lòng thử lại.\"}");
            return;
        }

        String signature = req.getParameter("signature");
        if (signature == null || signature.trim().isEmpty()) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Vui lòng nhập chữ ký.\"}");
            return;
        }

        String publicKey = keyDao.getActivePublicKey(user.getId());
        if (publicKey == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Không tìm thấy khóa công khai. Vui lòng tạo lại khóa.\"}");
            return;
        }

        try {
            boolean isValid = SignatureUtil.verifySignature(signature.trim(), orderHash, publicKey);

            if (!isValid) {
                resp.getWriter().write("{\"success\":false,\"message\":\"Chữ ký không hợp lệ. Vui lòng kiểm tra lại private key và hash đã dùng.\"}");
                return;
            }

            pendingOrder.setKeyId(keyId);
            pendingOrder.setOrderHash(orderHash);
            pendingOrder.setSignedOrderData(canonicalData);
            pendingOrder.setOrderSignature(signature.trim());
            pendingOrder.setSignatureStatus("valid");
            pendingOrder.setSignedAt(LocalDateTime.now());

            orderDao.createOrder(pendingOrder, pendingItems);

            Integer voucherId = pendingOrder.getVoucherId();
            if (voucherId != null) {
                voucherDao.incrementUsage(voucherId);
            }

            clearPendingSession(session);
            session.removeAttribute("cart");
            session.removeAttribute("appliedVoucher");
            session.removeAttribute("voucherError");

            resp.getWriter().write("{\"success\":true,\"redirect\":\"account\"}");

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false,\"message\":\"Lỗi lưu đơn hàng: " + e.getMessage() + "\"}");
        }
    }

    private void clearPendingSession(HttpSession session) {
        session.removeAttribute("pendingOrder");
        session.removeAttribute("pendingItems");
        session.removeAttribute("pendingOrderHash");
        session.removeAttribute("pendingCanonicalData");
        session.removeAttribute("pendingKeyId");
    }
}
