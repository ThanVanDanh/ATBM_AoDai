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

@WebServlet("/add-address")
public class AddAddressController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("account") : null;

        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String ho = req.getParameter("ho");
        String ten = req.getParameter("ten");
        String sdt = req.getParameter("sdt");
        String diachi = req.getParameter("diachi");
        String quocgia = req.getParameter("quocgia");
        String tinhthanh = req.getParameter("tinhthanh");
        boolean isDefault = "true".equals(req.getParameter("macdinh"));

        Address newAddr = new Address();
        newAddr.setUserId(user.getId());
        newAddr.setRecipientName(ho + " " + ten);
        newAddr.setRecipientPhone(sdt);
        newAddr.setAddressLine(diachi);
        newAddr.setCityProvince(tinhthanh);
        newAddr.setCountry(quocgia);
        newAddr.setDefault(isDefault);

        AddressDao dao = new AddressDao();
        dao.insertAddress(newAddr);

        resp.sendRedirect("account");
    }
}