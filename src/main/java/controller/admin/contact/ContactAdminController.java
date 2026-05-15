package controller.admin.contact;

import dao.ContactDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.contact.ContactUs;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ContactUsAdmin", value = "/admin/contact-list")
public class ContactAdminController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ContactDao contactDao = new ContactDao();

        List<ContactUs> list = contactDao.getAllMessages();
        request.setAttribute("contactList", list);
        request.getRequestDispatcher("/admin/contact-admin.jsp").forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}