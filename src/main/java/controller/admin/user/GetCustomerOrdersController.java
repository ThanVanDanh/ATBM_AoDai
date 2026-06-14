package controller.admin.user;

import com.google.gson.Gson;
import dao.OrderDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.order.Order;
import model.user.User;
import util.GsonUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/admin/get-customer-orders")
public class GetCustomerOrdersController extends HttpServlet {
    private OrderDao orderDao;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        orderDao = new OrderDao();
        gson = GsonUtil.getGson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        User user = (User) req.getSession().getAttribute("account");
        if (user == null || !"admin".equals(user.getRole())) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String userIdStr = req.getParameter("userId");
        if (userIdStr != null) {
            try {
                int userId = Integer.parseInt(userIdStr);
                List<Order> orders = orderDao.getOrdersByUserId(userId);

                PrintWriter out = resp.getWriter();
                out.print(gson.toJson(orders));
                out.flush();

            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid User ID");
            } catch (Exception e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error: " + e.getMessage());
            }
        }
    }
}
