package controller.admin.user;

import java.io.IOException;
import java.util.List;

import dao.AddressDao;
import dao.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.user.Address;
import model.user.User;

@WebServlet("/admin/users")
public class AdminUserController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDao userDao = new UserDao();
        List<User> users = userDao.findAll();
        AddressDao addressDao = new AddressDao();
        String search = req.getParameter("search");
        String status = req.getParameter("status");
        int page = 1;
        int pageSize = 10;
        if (req.getParameter("page") != null) {
            try {
                page = Integer.parseInt(req.getParameter("page"));
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int totalFilteredUsers = userDao.countUsersByFilter(search, status);
        int totalPages = (int) Math.ceil((double) totalFilteredUsers / pageSize);
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;

        int offset = (page - 1) * pageSize;
        users = userDao.getUsersPaginationAndFilter(pageSize, offset, search, status);


        for (User user : users) {
            List<Address> addresses = addressDao.findByUserId(user.getId());
            String defaultAddress = "";
            for (Address addr : addresses) {
                if (addr.getIsDefault()) {
                    defaultAddress = addr.getAddressLine();
                    break;
                }
            }
            user.setAuthProvider(defaultAddress);
        }
        int totalCustomers = userDao.countAll();
        int newCustomersThisWeek = userDao.countNewThisWeek();
        req.setAttribute("users", users);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("searchKeyword", search);
        req.setAttribute("statusFilter", status);
        req.setAttribute("totalCustomers", totalCustomers);
        req.setAttribute("newCustomersThisWeek", newCustomersThisWeek);
        req.setAttribute("totalFilteredUsers", totalFilteredUsers);
        req.getRequestDispatcher("/admin/users.jsp").forward(req, resp);
    }
}