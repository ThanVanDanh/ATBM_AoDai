package controller.admin.user;

import dao.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/block-user")
public class BlockUserController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(req.getParameter("id"));
            String action = req.getParameter("action");
            String status = "block".equals(action) ? "banned" : "active";

            UserDao dao = new UserDao();
            boolean success = dao.updateStatus(userId, status);

            resp.setStatus(success ? 200 : 400);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
        }
    }
}