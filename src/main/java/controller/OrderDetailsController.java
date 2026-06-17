package controller;

import com.google.gson.Gson;
import dao.OrderDao;
import model.order.Order;
import model.order.OrderItem;
import model.user.User;
import util.GsonUtil;

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

@WebServlet("/order-details")
public class OrderDetailsController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Gson gson = GsonUtil.getGson();
        Map<String, Object> response = new HashMap<>();

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("account");
        if (user == null) {
            response.put("success", false);
            response.put("message", "Bạn cần đăng nhập để xem chi tiết.");
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        String orderIdStr = req.getParameter("id");
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
            boolean isOwner = false;
            Order targetOrder = null;
            for (Order o : userOrders) {
                if (o.getId() == orderId) {
                    isOwner = true;
                    targetOrder = o;
                    break;
                }
            }

            if (!isOwner) {
                response.put("success", false);
                response.put("message", "Bạn không có quyền xem đơn hàng này.");
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            List<OrderItem> items = orderDao.getOrderItems(orderId);

            boolean signatureValid = false;

            try {
                util.OrderSignatureVerifier verifier = new util.OrderSignatureVerifier();
                signatureValid = verifier.verifyAndUpdateStatus(orderId);
                
                targetOrder = orderDao.getOrderById(orderId);

            } catch (Exception ex) {
                ex.printStackTrace();
                orderDao.updateSignatureStatus(orderId, "invalid");
                targetOrder = orderDao.getOrderById(orderId);
            }

            targetOrder.setFormattedCreatedAt(targetOrder.getFormattedCreatedAt());

            response.put("success", true);
            response.put("order", targetOrder);
            response.put("items", items);
            response.put("signatureValid", signatureValid);

        } catch (NumberFormatException e) {
            response.put("success", false);
            response.put("message", "ID đơn hàng không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Lỗi máy chủ: " + e.getMessage());
        }

        resp.getWriter().write(gson.toJson(response));
    }
}