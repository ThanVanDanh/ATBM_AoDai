package controller;

import dao.UserDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "VerifyAccount", value = "/verify-account")
public class VerifyAccountController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");

        if (token != null && !token.isEmpty()) {
            UserDao userDao = new UserDao();
            boolean isActivated = userDao.activateUser(token);

            if (isActivated) {
                request.setAttribute("successMessage", "Kích hoạt tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.");
            } else {
                request.setAttribute("errorMessage", "Link kích hoạt không hợp lệ hoặc đã hết hạn!");
            }
        } else {
            request.setAttribute("errorMessage", "Đường dẫn không hợp lệ.");
        }

        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}