package controller;

import dao.KeyDao;
import dao.OrderDao;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import model.order.Order;
import model.order.OrderItem;
import model.user.User;
import util.SignatureUtil;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "SubmitSignedOrderController", urlPatterns = {"/submit-signed-order"})
public class SubmitSignedOrderController extends HttpServlet {
    private OrderDao orderDao;
    private KeyDao keyDao;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.orderDao = new OrderDao();
            this.keyDao = new KeyDao();
        } catch (Exception ex) {
            throw new ServletException("Failed to initialize SubmitSignedOrderController: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("account");

        if (user == null) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Phiên đăng nhập đã hết hạn.\"}");
            return;
        }

        String orderIdStr = req.getParameter("orderId");
        String signature = req.getParameter("signature");

        if (orderIdStr == null || orderIdStr.isBlank()) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Thiếu mã đơn hàng.\"}");
            return;
        }
        if (signature == null || signature.isBlank()) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Vui lòng nhập chữ ký.\"}");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);
            Order order = orderDao.getOrderById(orderId);

            if (order == null || !order.getUserId().equals(user.getId())) {
                resp.getWriter().write("{\"success\":false,\"message\":\"Không tìm thấy đơn hàng.\"}");
                return;
            }

            if (!"unsigned".equals(order.getSignatureStatus())) {
                resp.getWriter().write("{\"success\":false,\"message\":\"Đơn hàng này đã được xác thực hoặc không hợp lệ.\"}");
                return;
            }

            if (order.getKeyId() == null) {
                resp.getWriter().write("{\"success\":false,\"message\":\"Đơn hàng này không có khóa xác thực đính kèm.\"}");
                return;
            }

            //lấy public key theo key_id đã gắn khi tạo đơn, không dùng active key hiện tại
            String publicKey = keyDao.getPublicKeyById(order.getKeyId());
            if (publicKey == null) {
                resp.getWriter().write("{\"success\":false,\"message\":\"Không tìm thấy khóa công khai của đơn hàng.\"}");
                return;
            }

            //check khóa active
            if (!keyDao.isKeyActive(order.getKeyId())) {
                resp.getWriter().write("{\"success\":false,\"message\":\"Khóa dùng để tạo đơn hàng này đã bị thu hồi. Không thể dùng khóa cũ để ký đơn hàng nữa.\"}");
                return;
            }

            util.OrderSignatureVerifier verifier = new util.OrderSignatureVerifier();
            verifier.verifyAndUpdateStatus(orderId);
            order = orderDao.getOrderById(orderId); // reload

            if ("invalid".equals(order.getSignatureStatus())) {
                resp.getWriter().write("{\"success\":false,\"message\":\"Dữ liệu đơn hàng đã bị thay đổi, chữ ký không còn hợp lệ.\"}");
                return;
            }

            boolean isValid = SignatureUtil.verifySignature(signature.trim(), order.getOrderHash(), publicKey);
            if (!isValid) {
                resp.getWriter().write("{\"success\":false,\"message\":\"Chữ ký không hợp lệ. Vui lòng kiểm tra lại private key và hash đã dùng.\"}");
                return;
            }

            boolean updated = orderDao.updateOrderSignature(orderId, signature.trim(), "Chờ xử lý");
            if (updated) {
                resp.getWriter().write("{\"success\":true}");
            } else {
                resp.getWriter().write("{\"success\":false,\"message\":\"Không thể cập nhật đơn hàng. Vui lòng thử lại.\"}");
            }

        } catch (NumberFormatException e) {
            resp.getWriter().write("{\"success\":false,\"message\":\"Mã đơn hàng không hợp lệ.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false,\"message\":\"Lỗi xử lý: " + e.getMessage() + "\"}");
        }
    }
}
