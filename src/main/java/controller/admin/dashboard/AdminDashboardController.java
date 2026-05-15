package controller.admin.dashboard;

import dao.OrderDao;
import dao.ProductDao;
import dao.UserDao;
import model.order.Order;
import model.product.Product;
import model.user.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import dao.VoucherDao;
import model.voucher.Voucher;

@WebServlet(name = "AdminDashboardController", urlPatterns = { "/admin/dashboard" })
public class AdminDashboardController extends HttpServlet {
    private OrderDao orderDao;
    private UserDao userDao;
    private ProductDao productDao;
    private VoucherDao voucherDao;

    @Override
    public void init() throws ServletException {
        this.orderDao = new OrderDao();
        this.userDao = new UserDao();
        this.productDao = new ProductDao();
        this.voucherDao = new VoucherDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        double totalRevenue = orderDao.getTotalRevenue();
        int totalOrders = orderDao.countTotalOrders();
        int newCustomersWeek = userDao.countNewThisWeek();
        Product bestSellingProduct = productDao.getBestSellingProduct();

        List<Order> recentOrders = orderDao.getRecentOrders(5);
        List<User> newCustomers = userDao.getNewCustomers(5);
        List<model.order.MonthlyRevenue> monthlyRevenue = orderDao.getRevenueByMonth(6);
        List<Voucher> activeVouchers = voucherDao.getActiveVouchers();

        req.setAttribute("totalRevenue", totalRevenue);
        req.setAttribute("totalOrders", totalOrders);
        req.setAttribute("newCustomersWeek", newCustomersWeek);
        req.setAttribute("bestSellingProduct", bestSellingProduct);
        req.setAttribute("recentOrders", recentOrders);
        req.setAttribute("newCustomers", newCustomers);
        req.setAttribute("monthlyRevenue", monthlyRevenue);
        req.setAttribute("activeVouchers", activeVouchers);

        req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
    }
}
