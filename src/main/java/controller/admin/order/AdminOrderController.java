package controller.admin.order;

import dao.OrderDao;
import model.order.Order;
import util.OrderSignatureVerifier;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/orders")
public class AdminOrderController extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        try {
            OrderDao orderDao = new OrderDao();

            int page = getPage(req);
            String statusFilter = req.getParameter("status");
            String searchKeyword = req.getParameter("search");

            int totalOrders = getTotalOrders(orderDao, searchKeyword, statusFilter);
            int totalPages = (int) Math.ceil((double) totalOrders / PAGE_SIZE);

            if (page < 1) {
                page = 1;
            }

            if (page > totalPages && totalPages > 0) {
                page = totalPages;
            }

            int offset = (page - 1) * PAGE_SIZE;

            List<Order> orders = getOrders(orderDao, searchKeyword, statusFilter, PAGE_SIZE, offset);

            checkSignatureBeforeDisplay(orderDao, orders);

            totalOrders = getTotalOrders(orderDao, searchKeyword, statusFilter);
            totalPages = (int) Math.ceil((double) totalOrders / PAGE_SIZE);

            if (page > totalPages && totalPages > 0) {
                page = totalPages;
            }

            offset = (page - 1) * PAGE_SIZE;
            orders = getOrders(orderDao, searchKeyword, statusFilter, PAGE_SIZE, offset);

            req.setAttribute("orders", orders);
            req.setAttribute("statusFilter", statusFilter);
            req.setAttribute("searchKeyword", searchKeyword == null ? "" : searchKeyword.trim());
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

    private int getPage(HttpServletRequest req) {
        String pageParam = req.getParameter("page");

        if (pageParam == null || pageParam.trim().isEmpty()) {
            return 1;
        }

        try {
            return Integer.parseInt(pageParam);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private int getTotalOrders(OrderDao orderDao, String searchKeyword, String statusFilter) {
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            return orderDao.countOrdersBySearch(searchKeyword.trim(), statusFilter);
        }

        return orderDao.countOrdersByStatus(statusFilter);
    }

    private List<Order> getOrders(OrderDao orderDao, String searchKeyword, String statusFilter, int limit, int offset) {
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            return orderDao.searchOrders(searchKeyword.trim(), statusFilter, limit, offset);
        }

        return orderDao.getOrdersPaginationAndFilter(limit, offset, statusFilter);
    }

    private void checkSignatureBeforeDisplay(OrderDao orderDao, List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return;
        }

        OrderSignatureVerifier verifier = new OrderSignatureVerifier();

        for (Order order : orders) {
            if (order == null) {
                continue;
            }

            if (!"valid".equalsIgnoreCase(order.getSignatureStatus())) {
                continue;
            }

            try {
                verifier.verifyAndUpdateStatus(order.getId());
            } catch (Exception e) {
                e.printStackTrace();
                orderDao.updateSignatureStatus(order.getId(), "invalid");
                orderDao.updateOrderStatus(order.getId(), "Cần xác minh");
            }
        }
    }
}