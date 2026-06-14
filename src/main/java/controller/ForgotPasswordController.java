package controller;

import dao.UserDao;
import model.user.User;
import services.EmailService;
import services.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;

@WebServlet(name = "ForgotPassword", value = "/forgot-password")
public class ForgotPasswordController extends HttpServlet {
    private UserDao userDao = new UserDao();
    private EmailService emailService = new EmailService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("request".equals(action)) {
            String email = request.getParameter("email");
            User user = userDao.findByEmail(email);

            if (user != null) {
                String otp = emailService.sendEmail(user);
                if (otp != null) {
                    HttpSession session = request.getSession();
                    session.setAttribute("otp", otp);
                    session.setAttribute("email", email);
                    session.setAttribute("otp_time", System.currentTimeMillis());

                    request.setAttribute("message", "Mã OTP đã được gửi!");
                    request.getRequestDispatcher("verify-otp.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Lỗi gửi email, vui lòng thử lại!");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("error", "Email không tồn tại trong hệ thống!");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }

        } else if ("verify".equals(action)) {
            String inputOtp = request.getParameter("otp");
            HttpSession session = request.getSession();
            String sessionOtp = (String) session.getAttribute("otp");
            Long otpTime = (Long) session.getAttribute("otp_time");

            if (otpTime == null || (System.currentTimeMillis() - otpTime > 5 * 60 * 1000)) {
                request.setAttribute("error", "Mã OTP đã hết hạn! Vui lòng lấy lại mã mới.");
                request.getRequestDispatcher("verify-otp.jsp").forward(request, response);
                return;
            }

            if (sessionOtp != null && sessionOtp.equals(inputOtp)) {
                request.getRequestDispatcher("reset-pw.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Mã OTP không chính xác!");
                request.getRequestDispatcher("verify-otp.jsp").forward(request, response);
            }

        } else if ("reset".equals(action)) {
            String newPass = request.getParameter("new_password");
            String confirmPass = request.getParameter("confirm_password");
            HttpSession session = request.getSession();
            String email = (String) session.getAttribute("email");

            String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$";
            if (!newPass.matches(passwordRegex)) {
                request.setAttribute("error",
                        "Mật khẩu yếu: Phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và ký tự đặc biệt!");
                request.getRequestDispatcher("reset-pw.jsp").forward(request, response);
                return;
            }

            if (newPass.equals(confirmPass)) {
                String hashPass = BCrypt.hashpw(newPass, BCrypt.gensalt(12));
                boolean isUpdated = userDao.updatePassword(email, hashPass);

                if (isUpdated) {
                    session.invalidate();
                    response.sendRedirect("login.jsp?message=DoiMatKhauThanhCong");
                } else {
                    request.setAttribute("error", "Lỗi hệ thống!");
                    request.getRequestDispatcher("reset-pw.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
                request.getRequestDispatcher("reset-pw.jsp").forward(request, response);
            }
        }
    }
}