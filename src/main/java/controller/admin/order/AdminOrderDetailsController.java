package controller.admin.order;

import com.google.gson.Gson;
import dao.OrderDao;
import model.order.Order;
import model.order.OrderItem;
import util.GsonUtil;
import util.OrderSignatureVerifier;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/order-details")
public class AdminOrderDetailsController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        Gson gson = GsonUtil.getGson();
        Map<String, Object> response = new HashMap<>();

        try {
            String orderIdStr = req.getParameter("orderId");

            if (orderIdStr == null || orderIdStr.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Thiếu mã đơn hàng");
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            int orderId = Integer.parseInt(orderIdStr);
            OrderDao orderDao = new OrderDao();

            Order order = orderDao.getOrderById(orderId);

            if (order == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy đơn hàng");
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            if ("valid".equalsIgnoreCase(order.getSignatureStatus())) {
                try {
                    OrderSignatureVerifier verifier = new OrderSignatureVerifier();
                    verifier.verifyAndUpdateStatus(orderId);
                } catch (Exception e) {
                    e.printStackTrace();
                    orderDao.updateSignatureStatus(orderId, "invalid");
                    orderDao.updateOrderStatus(orderId, "Cần xác minh");
                }
            }

            order = orderDao.getOrderById(orderId);
            List<OrderItem> items = orderDao.getOrderItems(orderId);

            order.setFormattedCreatedAt(order.getFormattedCreatedAt());

            response.put("success", true);
            response.put("order", order);
            response.put("items", items);

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