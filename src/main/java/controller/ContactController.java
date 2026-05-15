package controller;

import dao.ContactDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.contact.ContactUs;
import java.io.IOException;

@WebServlet(name = "ContactController", value = "/contact_us")
public class ContactController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("contactus.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String name = request.getParameter("fullName");
        String email = request.getParameter("email");
        String messageBody = request.getParameter("messageBody");

        ContactUs contactUs = new ContactUs(name, email, messageBody);
        ContactDao contactDao = new ContactDao();

        try {
            contactDao.insert(contactUs);
            request.setAttribute("success", "Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi sớm");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error","Có lỗi xảy ra, vui lòng thử lại sau.");
        }

        request.getRequestDispatcher("contactus.jsp").forward(request, response);
    }
}