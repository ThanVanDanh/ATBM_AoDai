package controller;

import com.google.gson.Gson;
import dao.OrderDao;
import model.order.Order;
import model.user.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/cancel-order")
public class CancelOrderController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("account");
        if (user == null) {
            response.put("success", false);
            response.put("message", "Bạn cần đăng nhập để thực hiện thao tác này.");
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        String orderIdStr = req.getParameter("orderId");
        if (orderIdStr == null || orderIdStr.isEmpty()) {
            response.put("success", false);
            response.put("message", "Mã đơn hàng không hợp lệ.");
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);
            OrderDao orderDao = new OrderDao();

            List<Order> userOrders = orderDao.getOrdersByUserId(user.getId());
            Order targetOrder = null;
            for (Order o : userOrders) {
                if (o.getId() == orderId) {
                    targetOrder = o;
                    break;
                }
            }

            if (targetOrder == null) {
                response.put("success", false);
                response.put("message", "Đơn hàng không tồn tại hoặc không thuộc về bạn.");
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            String currentStatus = targetOrder.getOrderStatus() != null
                    ? targetOrder.getOrderStatus().trim().toLowerCase()
                    : "";

            boolean canCancel = currentStatus.contains("chờ") || currentStatus.contains("đang xử lý") ||
                    currentStatus.equals("pending") || currentStatus.equals("processing");

            if (!canCancel) {
                response.put("success", false);
                response.put("message", "Không thể hủy đơn hàng này do đã được vận chuyển hoặc hoàn thành.");
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            String cancelReason = req.getParameter("cancelReason");
            if (cancelReason == null || cancelReason.trim().isEmpty()) {
                cancelReason = "Không có lý do";
            }
            
            boolean updated = orderDao.cancelOrderWithReason(orderId, "Đã hủy", cancelReason);

            if (updated) {
                response.put("success", true);
                response.put("message", "Hủy đơn hàng thành công.");
            } else {
                response.put("success", false);
                response.put("message", "Lỗi định dạng hoặc lỗi máy chủ khi cập nhật.");
            }

        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "ID đơn hàng không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
        }

        resp.getWriter().write(gson.toJson(response));
    }
}
