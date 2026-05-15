package services;

import model.user.User;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {
    public String sendEmail(User user) {
        String to = user.getEmail();
        String from = "minhthu12575@gmail.com";
        String password = "vaqq ffcq regn hgpb";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        String otp = String.format("%06d", number);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "Việt Sắc Đỏ"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Việt Sắc Đỏ - Mã xác nhận khôi phục mật khẩu");

            String htmlContent = "<div style=\"font-family: Arial, sans-serif; border: 1px solid #dadce0; border-radius: 8px; padding: 40px; max-width: 550px; margin: 20px auto; text-align: center;\">"
                    +
                    "<div style=\"margin-bottom: 24px;\">" +
                    "<span style=\"font-size: 24px; font-weight: bold; color: #320000;\">Việt Sắc Đỏ</span>" +
                    "</div>" +

                    "<h1 style=\"font-size: 22px; color: #202124; font-weight: 500; margin-bottom: 15px;\">Yêu cầu khôi phục mật khẩu</h1>"
                    +

                    "<div style=\"margin-bottom: 24px;\">" +
                    "<img src=\"https://www.gstatic.com/images/branding/product/2x/avatar_square_blue_120dp.png\" style=\"border-radius: 50%; width: 40px; height: 40px; vertical-align: middle; margin-right: 10px;\">"
                    +
                    "<span style=\"font-size: 14px; color: #3c4043; font-weight: 500;\">" + to + "</span>" +
                    "</div>" +

                    "<hr style=\"border: none; border-top: 1px solid #dadce0; margin-bottom: 24px;\">" +

                    "<div style=\"text-align: left; color: #3c4043; font-size: 14px; line-height: 1.6; margin-bottom: 30px;\">"
                    +
                    "<p>Chào bạn,</p>" +
                    "<p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Dưới đây là mã OTP xác nhận:</p>"
                    +
                    "</div>" +

                    "<div style=\"background-color: #f1f3f4; border-radius: 8px; padding: 15px; margin-bottom: 30px; display: inline-block; min-width: 200px;\">"
                    +
                    "<span style=\"font-size: 24px; font-weight: bold; letter-spacing: 5px; color: #320000; display: block;\">"
                    + otp + "</span>" +
                    "</div>" +

                    "<div style=\"text-align: left; color: #3c4043; font-size: 14px; line-height: 1.6;\">" +
                    "<p>Mã xác nhận này sẽ hết hạn sau <b>5 phút</b>.</p>" +
                    "<p>Nếu bạn không yêu cầu thay đổi mật khẩu, vui lòng bỏ qua email này.</p>" +
                    "</div>" +

                    "<div style=\"margin-top: 35px; padding-top: 20px; border-top: 1px solid #f1f3f4; font-size: 12px; color: #70757a; line-height: 1.4;\">"
                    +
                    "Đây là email tự động, vui lòng không trả lời.<br><br>" +
                    "<a href=\"#\" style=\"color: #1a73e8; text-decoration: none;\">Truy cập trang web của chúng tôi</a>"
                    +
                    "</div>" +
                    "</div>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            return otp;
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendVerifyLink(String toEmail, String token, String domain) {
        String from = "minhthu12575@gmail.com";
        String password = "vaqq ffcq regn hgpb";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "Việt Sắc Đỏ"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("Việt Sắc Đỏ - Kích hoạt tài khoản");

            String link = domain + "/verify-account?token=" + token;

            String htmlContent = "<div style=\"font-family: Arial, sans-serif; border: 1px solid #dadce0; border-radius: 8px; padding: 40px; max-width: 550px; margin: 20px auto; text-align: center;\">"
                    +
                    // Logo (Bạn có thể thay bằng link logo Việt Sắc Đỏ nếu có)
                    "<div style=\"margin-bottom: 24px;\">" +
                    "<span style=\"font-size: 24px; font-weight: bold; color: #320000;\">Việt Sắc Đỏ</span>" +
                    "</div>" +

                    "<h1 style=\"font-size: 22px; color: #202124; font-weight: 500; margin-bottom: 15px;\">Kích hoạt tài khoản của bạn</h1>"
                    +

                    "<div style=\"margin-bottom: 24px;\">" +
                    "<img src=\"https://www.gstatic.com/images/branding/product/2x/avatar_square_blue_120dp.png\" style=\"border-radius: 50%; width: 40px; height: 40px; vertical-align: middle; margin-right: 10px;\">"
                    +
                    "<span style=\"font-size: 14px; color: #3c4043; font-weight: 500;\">" + toEmail + "</span>" +
                    "</div>" +

                    "<hr style=\"border: none; border-top: 1px solid #dadce0; margin-bottom: 24px;\">" +

                    "<div style=\"text-align: left; color: #3c4043; font-size: 14px; line-height: 1.6; margin-bottom: 30px;\">"
                    +
                    "<p>Chào bạn,</p>" +
                    "<p>Cảm ơn bạn đã đăng ký tài khoản tại <b>Việt Sắc Đỏ</b>. Để bắt đầu sử dụng dịch vụ, vui lòng xác nhận rằng đây là địa chỉ email của bạn bằng cách nhấn vào nút bên dưới.</p>"
                    +
                    "</div>" +

                    // Nút bấm giống hệt Google
                    "<a href=\"" + link
                    + "\" style=\"display: inline-block; background-color: #1a73e8; color: #ffffff; padding: 12px 30px; text-decoration: none; border-radius: 4px; font-weight: 500; font-size: 14px;\">"
                    +
                    "Kích hoạt tài khoản" +
                    "</a>" +

                    "<div style=\"margin-top: 35px; padding-top: 20px; border-top: 1px solid #f1f3f4; font-size: 12px; color: #70757a; line-height: 1.4;\">"
                    +
                    "Bạn nhận được email này vì địa chỉ này đã được dùng để đăng ký tài khoản tại Việt Sắc Đỏ. " +
                    "Nếu không phải là bạn, vui lòng bỏ qua email này.<br><br>" +
                    "<a href=\"" + domain
                    + "\" style=\"color: #1a73e8; text-decoration: none;\">Truy cập trang web của chúng tôi</a>" +
                    "</div>" +
                    "</div>";

            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public boolean sendContactReply(String toEmail, String subject, String messageBody) {
        String from = "minhthu12575@gmail.com";
        String password = "vaqq ffcq regn hgpb";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "Việt Sắc Đỏ Support"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject, "UTF-8");

            String htmlContent = "<h3>Xin chào,</h3>"
                    + "<p>" + messageBody.replace("\n", "<br>") + "</p>"
                    + "<br><hr>"
                    + "<p style='color:gray; font-size:12px;'>Trân trọng,<br><b>Đội ngũ Việt Sắc Đỏ</b></p>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }
}