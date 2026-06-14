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

@WebServlet("/admin/delete-order")
public class DeleteOrderController extends HttpServlet {
    private final OrderDao orderDao = new OrderDao();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            String orderIdStr = request.getParameter("orderId");

            if (orderIdStr == null || orderIdStr.isEmpty()) {
                result.put("success", false);
                result.put("message", "Thiếu mã đơn hàng");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            int orderId = Integer.parseInt(orderIdStr);
            boolean deleted = orderDao.deleteOrder(orderId);

            if (deleted) {
                result.put("success", true);
                result.put("message", "Xóa đơn hàng thành công");
            } else {
                result.put("success", false);
                result.put("message", "Không tìm thấy đơn hàng hoặc xóa thất bại");
            }
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Mã đơn hàng không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Lỗi server: " + e.getMessage());
        }

        response.getWriter().write(gson.toJson(result));
    }
}
