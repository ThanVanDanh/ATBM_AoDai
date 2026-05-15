package controller.admin.user;

import java.io.IOException;

import dao.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin/update-role")
public class UpdateUserRoleController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userIdStr = req.getParameter("userId");
        String role = req.getParameter("role");
        if (userIdStr != null && role != null) {
            try {
                int userId = Integer.parseInt(userIdStr);
                UserDao userDao = new UserDao();
                userDao.updateRole(userId, role);
            } catch (NumberFormatException ignored) {}
        }
        resp.sendRedirect("users");
    }
}
