package controller.admin.order;

import com.google.gson.Gson;
import dao.OrderDao;
import model.order.Order;
import util.OrderSignatureVerifier;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/admin/update-order-status")
public class UpdateOrderStatusController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();

        try {
            String orderIdStr = req.getParameter("orderId");
            String newStatus = req.getParameter("status");

            if (isBlank(orderIdStr)) {
                response.put("success", false);
                response.put("message", "Thiếu mã đơn hàng");
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            if (isBlank(newStatus)) {
                response.put("success", false);
                response.put("message", "Thiếu trạng thái mới");
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            int orderId = Integer.parseInt(orderIdStr.trim());
            newStatus = newStatus.trim();

            OrderDao orderDao = new OrderDao();
            Order order = orderDao.getOrderById(orderId);

            if (order == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy đơn hàng");
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            try {
                OrderSignatureVerifier verifier = new OrderSignatureVerifier();
                verifier.verifyAndUpdateStatus(orderId);
                order = orderDao.getOrderById(orderId);
            } catch (Exception e) {
                e.printStackTrace();
                orderDao.updateSignatureStatus(orderId, "invalid");
                orderDao.updateOrderStatus(orderId, "Cần xác minh");
                order = orderDao.getOrderById(orderId);
            }

            if ("invalid".equalsIgnoreCase(order.getSignatureStatus())
                    && !isAllowedStatusForInvalidSignature(newStatus)) {
                response.put("success", false);
                response.put("message", "Đơn hàng có chữ ký giả mạo, chỉ có thể chuyển sang Cần xác minh hoặc Đã hủy.");
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            if ("unsigned".equalsIgnoreCase(order.getSignatureStatus())
                    && !isCancelStatus(newStatus)) {
                response.put("success", false);
                response.put("message", "Đơn hàng chưa được khách hàng ký số, chỉ có thể Hủy đơn.");
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            boolean updated;

            if (isCancelStatus(newStatus)) {
                String cancelReason = req.getParameter("cancelReason");

                if (isBlank(cancelReason)) {
                    if ("invalid".equalsIgnoreCase(order.getSignatureStatus())) {
                        cancelReason = "Đơn hàng có chữ ký giả mạo";
                    } else {
                        cancelReason = "Admin hủy đơn";
                    }
                }

                updated = orderDao.cancelOrderWithReason(orderId, "Đã hủy", cancelReason.trim());
            } else {
                updated = orderDao.updateOrderStatus(orderId, newStatus);
            }
            if (updated) {
                response.put("success", true);
                response.put("message", "Cập nhật trạng thái thành công");
                response.put("newStatus", newStatus);
            } else {
                response.put("success", false);
                response.put("message", "Không thể cập nhật trạng thái đơn hàng");
            }

        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "ID đơn hàng không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
        }

        resp.getWriter().write(gson.toJson(response));
    }

    private boolean isAllowedStatusForInvalidSignature(String status) {
        return isNeedVerifyStatus(status) || isCancelStatus(status);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
    private boolean isCancelStatus(String status) {
        if (status == null) {
            return false;
        }

        String normalizedStatus = status.trim().toLowerCase();

        return "đã hủy".equals(normalizedStatus)
                || "đã huỷ".equals(normalizedStatus);
    }

    private boolean isNeedVerifyStatus(String status) {
        if (status == null) {
            return false;
        }

        String normalizedStatus = status.trim().toLowerCase();

        return "cần xác minh".equals(normalizedStatus);
    }
}
