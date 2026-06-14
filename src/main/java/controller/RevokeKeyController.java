package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.user.User;
import services.KeyService;

import java.io.IOException;

@WebServlet("/revoke-key")
public class RevokeKeyController extends HttpServlet {
    private KeyService keyService;

    @Override
    public void init() throws ServletException {
        this.keyService = new KeyService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("account") == null) {
            response.getWriter().write("{\"success\": false, \"message\": \"Vui lòng đăng nhập.\"}");
            return;
        }

        User user = (User) session.getAttribute("account");
        boolean isRevoked = keyService.revokeUserKey(user.getId());

        if (isRevoked) {
            response.getWriter().write("{\"success\": true}");
        } else {
            response.getWriter().write("{\"success\": false, \"message\": \"Không tìm thấy khóa hoạt động để hủy.\"}");
        }
    }
}
