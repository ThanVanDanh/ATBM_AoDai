package controller.admin.contact;

import dao.ContactDao; // Nhớ import dòng này
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.EmailService;

import java.io.IOException;

@WebServlet(name = "ContactReplyController", value = "/admin/contact-reply")
public class ReplyContactController extends HttpServlet {

    private EmailService emailService;
    private ContactDao contactDao;

    @Override
    public void init() throws ServletException {
        super.init();
        this.emailService = new EmailService();
        this.contactDao = new ContactDao();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String idStr = request.getParameter("id");
        String recipientEmail = request.getParameter("email");
        String subject = request.getParameter("subject");
        String content = request.getParameter("content");

        if (recipientEmail == null || content == null || content.trim().isEmpty()) {
            setMessage(request, "error", "Nội dung phản hồi không được để trống.");
            response.sendRedirect(request.getContextPath() + "/admin/contact-list");
            return;
        }

        boolean isSent = emailService.sendContactReply(recipientEmail, subject, content);

        if (isSent) {
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    contactDao.updateStatus(id, "đã gửi");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            setMessage(request, "success", "Đã gửi email phản hồi thành công đến " + recipientEmail);
        } else {
            setMessage(request, "error", "Gửi email thất bại. Vui lòng kiểm tra lại hệ thống.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/contact-list");
    }

    private void setMessage(HttpServletRequest req, String type, String message) {
        HttpSession session = req.getSession();
        session.setAttribute("messageType", type);
        session.setAttribute("message", message);
    }
}