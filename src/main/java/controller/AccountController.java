package controller;

import dao.UserDao;
import dao.AddressDao;
import model.user.User;
import model.user.Address;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/account")
public class AccountController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("account") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("account");
        req.setAttribute("user", user);

        AddressDao addressDao = new AddressDao();
        java.util.List<Address> addresses = addressDao.findByUserId(user.getId());

        req.setAttribute("addresses", addresses);

        dao.OrderDao orderDao = new dao.OrderDao();
        java.util.List<model.order.Order> orders = orderDao.getOrdersByUserId(user.getId());
        req.setAttribute("orders", orders);

        req.getRequestDispatcher("account.jsp").forward(req, resp);
    }
}
