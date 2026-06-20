package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.user.User;
import services.KeyService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@WebServlet("/register-key")
public class KeyRegistrationController extends HttpServlet {

    private KeyService keyService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.keyService = new KeyService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("account") == null) {
                out.write("{\"success\": false, \"message\": \"Vui lòng đăng nhập trước khi tạo khóa.\"}");
                return;
            }

            User user = (User) session.getAttribute("account");
            int userId = user.getId();

            InputStream inputStream = request.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            int ch;
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            String requestBody = sb.toString();

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(requestBody, JsonObject.class);

            if (jsonObject == null || !jsonObject.has("publicKey")) {
                out.write("{\"success\": false, \"message\": \"Dữ liệu khóa không hợp lệ hoặc bị thiếu.\"}");
                return;
            }

            String publicKeyPem = jsonObject.get("publicKey").getAsString();

            String validationError = KeyService.validatePublicKeyPem(publicKeyPem);
            if (validationError != null) {
                out.write("{\"success\": false, \"message\": \"Khóa không hợp lệ: " + escapeJson(validationError) + "\"}");
                return;
            }

            boolean isSaved = keyService.registerUserKey(userId, publicKeyPem);

            if (isSaved) {
                out.write("{\"success\": true}");
            } else {
                out.write("{\"success\": false, \"message\": \"Lỗi khi lưu khóa vào cơ sở dữ liệu.\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.write("{\"success\": false, \"message\": \"Lỗi hệ thống: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}