package controller.admin.order;

import dao.OrderDao;
import model.order.Order;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/orders")
public class AdminOrderController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        try {
            OrderDao orderDao = new OrderDao();
            int page = 1;
            int pageSize = 10;
            if (req.getParameter("page") != null) {
                try {
                    page = Integer.parseInt(req.getParameter("page"));
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }
            int totalOrders;
            int totalPages;
            List<Order> orders;
            String statusFilter = req.getParameter("status");
            String searchKeyword = req.getParameter("search");
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                totalOrders = orderDao.countOrdersBySearch(searchKeyword.trim(), statusFilter);
                totalPages = (int) Math.ceil((double) totalOrders / pageSize);
                if (page < 1) page = 1;
                if (page > totalPages && totalPages > 0) page = totalPages;
                int offset = (page - 1) * pageSize;

                orders = orderDao.searchOrders(searchKeyword.trim(), statusFilter, pageSize, offset);
            } else {
                totalOrders = orderDao.countOrdersByStatus(statusFilter);
                totalPages = (int) Math.ceil((double) totalOrders / pageSize);
                if (page < 1) page = 1;
                if (page > totalPages && totalPages > 0) page = totalPages;
                int offset = (page - 1) * pageSize;
                orders = orderDao.getOrdersPaginationAndFilter(pageSize, offset, statusFilter);
            }
            req.setAttribute("orders", orders);
            req.setAttribute("statusFilter", statusFilter);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("totalOrders", totalOrders);

            req.getRequestDispatcher("/admin/orders.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Lỗi khi tải danh sách đơn hàng: " + e.getMessage());
            req.getRequestDispatcher("/admin/orders.jsp").forward(req, resp);
        }
    }
}
