package controller;

import dao.UserDao;
import dao.AddressDao;
import model.user.User;
import model.user.Address;
import model.order.Order;
import util.OrderSignatureVerifier;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.List;

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
        List<Address> addresses = addressDao.findByUserId(user.getId());
        req.setAttribute("addresses", addresses);

        dao.OrderDao orderDao = new dao.OrderDao();
        List<Order> orders = orderDao.getOrdersByUserId(user.getId());

        OrderSignatureVerifier verifier = new OrderSignatureVerifier();

        for (Order order : orders) {
            if ("valid".equalsIgnoreCase(order.getSignatureStatus())) {
                try {
                    verifier.verifyAndUpdateStatus(order.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    orderDao.updateSignatureStatus(order.getId(), "invalid");
                }
            }
        }

        orders = orderDao.getOrdersByUserId(user.getId());
        req.setAttribute("orders", orders);

        dao.KeyDao keyDao = new dao.KeyDao();
        Map<String, Object> activeKey = keyDao.getActiveKeyInfo(user.getId());

        if (activeKey != null) {
            req.setAttribute("currentKeyId", activeKey.get("id"));
            req.setAttribute("currentPublicKey", activeKey.get("public_key"));
            req.setAttribute("currentKeyCreatedAt", activeKey.get("created_at"));
        }

        req.getRequestDispatcher("account.jsp").forward(req, resp);
    }
}