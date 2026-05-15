package controller.admin.contact;

import dao.ContactDao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "DeleteContactController", value = "/delete_message")
public class DeleteContactController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            ContactDao dao = new ContactDao();
            dao.delete(id);

            request.getSession().setAttribute("message", "Đã xóa thành công!");
            request.getSession().setAttribute("messageType", "success");

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("message", "Xóa thất bại!");
            request.getSession().setAttribute("messageType", "error");
        }

        response.sendRedirect(request.getContextPath() + "/contactus-admin");

    }
}