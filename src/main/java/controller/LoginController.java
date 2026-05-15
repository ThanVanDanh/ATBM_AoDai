package controller;

import dao.UserDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.user.User;
import services.EmailService;
import services.UserService;

import java.io.IOException;

@WebServlet(name = "Login", value = "/Login")
public class LoginController extends HttpServlet {
    private UserService userService = new UserService(new UserDao(), new EmailService());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getSession().getAttribute("account") != null) {
            response.sendRedirect("account.jsp");
        } else {
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("google".equals(action) || "facebook".equals(action)) {
            String email = request.getParameter("email");
            String name = request.getParameter("name");
            String uid = request.getParameter("uid");
            User user = userService.processSocialLogin(email, name, uid, action);
            createSession(request, response, user);
        } else {
            String loginKey = request.getParameter("username");
            String pass = request.getParameter("password");
            User user = userService.login(loginKey, pass);
            if (user != null) {
                createSession(request, response, user);
            } else {
                request.setAttribute("error", "Sai số điện thoại hoặc mật khẩu!");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        }
    }

    private void createSession(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        HttpSession session = req.getSession();
        session.setAttribute("account", user);
        if ("admin".equals(user.getRole())) {
            resp.sendRedirect("admin/dashboard");
        } else {
            resp.sendRedirect("account");
        }
    }
}