package controller.admin.order;

import com.google.gson.Gson;
import dao.OrderDao;

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
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();

        try {
            String orderIdStr = req.getParameter("orderId");
            String newStatus = req.getParameter("status");

            if (orderIdStr == null || orderIdStr.isEmpty()) {
                response.put("success", false);
                response.put("message", "Thiếu mã đơn hàng");
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            if (newStatus == null || newStatus.isEmpty()) {
                response.put("success", false);
                response.put("message", "Thiếu trạng thái mới");
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            int orderId = Integer.parseInt(orderIdStr);
            OrderDao orderDao = new OrderDao();

            boolean updated;

            if (newStatus.equalsIgnoreCase("đã hủy")) {
                String cancelReason = req.getParameter("cancelReason");
                if (cancelReason == null || cancelReason.trim().isEmpty()) {
                    cancelReason = "Admin hủy đơn";
                }
                updated = orderDao.cancelOrderWithReason(orderId, newStatus, cancelReason);
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
}
