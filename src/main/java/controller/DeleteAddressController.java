package controller;

import dao.AddressDao;
import model.user.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/delete-address")
public class DeleteAddressController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("account") : null;

        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        try {
            String idStr = req.getParameter("id");
            if (idStr != null) {
                int addressId = Integer.parseInt(idStr);
                int userId = user.getId();

                AddressDao dao = new AddressDao();
                dao.deleteAddress(addressId, userId);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        resp.sendRedirect("account");
    }
}