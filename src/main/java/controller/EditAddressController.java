package controller;

import dao.AddressDao;
import model.user.Address;
import model.user.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/edit-address")
public class EditAddressController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("account") : null;
        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        try {
            int id = Integer.parseInt(req.getParameter("id"));
            String ho = req.getParameter("ho");
            String ten = req.getParameter("ten");
            String sdt = req.getParameter("sdt");
            String diachi = req.getParameter("diachi");
            String tinhthanh = req.getParameter("tinhthanh");
            String quocgia = req.getParameter("quocgia");
            boolean isDefault = "true".equals(req.getParameter("macdinh"));

            Address addr = new Address();
            addr.setId(id);
            addr.setUserId(user.getId());
            addr.setRecipientName(ho + " " + ten);
            addr.setRecipientPhone(sdt);
            addr.setAddressLine(diachi);
            addr.setCityProvince(tinhthanh);
            addr.setCountry(quocgia);
            addr.setDefault(isDefault);

            AddressDao dao = new AddressDao();
            dao.updateAddress(addr);

            resp.sendRedirect("account");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            resp.sendRedirect("account?error=invalid_id");
        }
    }
}