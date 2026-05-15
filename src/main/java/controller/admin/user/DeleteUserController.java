package controller.admin.user;

import dao.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/delete-user")
public class DeleteUserController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(req.getParameter("id"));
            UserDao dao = new UserDao();

            boolean success = dao.delete(userId);

            resp.setStatus(success ? 200 : 400);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
        }
    }
}