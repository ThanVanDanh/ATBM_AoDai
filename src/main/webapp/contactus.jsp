<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=">
    <title>Liên hệ</title>
    <link rel="icon" href="image/logoaodai.jpg" type="image/jpeg">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css" integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="style/contactus.css">
    <link rel="stylesheet" href="style/style-header.css">
    <link rel="stylesheet" href="style/footer.css">
    <link rel="stylesheet" href="style/breadcrumb.css">
    <script src="scripts/home.js"></script>
    <link rel="stylesheet" href="style/contactus.css">
</head>
<body>
<jsp:include page="header.jsp" />
<div class="breadcrumb-container">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Trang Chủ</a></li>
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/contact_us">Liên hệ</a></li> <li class="breadcrumb-item active" aria-current="page">Gửi</li>
        </ol>
    </nav>
</div>
<section >
    <div class="section-header">
        <div class="container">
            <h2>Liên hệ</h2>
        </div>
    </div>

    <div class="container">
        <div class="row">

            <div class="contact-info">
                <div class="contact-info-item">
                    <div class="contact-info-icon">
                        <i class="fas fa-home"></i>
                    </div>

                    <div class="contact-info-content">
                        <h4>Địa chỉ</h4>
                        <p>16 tổ 3,Linh Trung, Tp.Thủ Đức,Tp.Hồ Chí Minh</p>
                    </div>
                </div>

                <div class="contact-info-item">
                    <div class="contact-info-icon">
                        <i class="fas fa-phone"></i>
                    </div>

                    <div class="contact-info-content">
                        <h4>Số điện thoại</h4>
                        <p>0901.234.567</p>
                    </div>
                </div>

                <div class="contact-info-item">
                    <div class="contact-info-icon">
                        <i class="fas fa-envelope"></i>
                    </div>

                    <div class="contact-info-content">
                        <h4>Email</h4>
                        <p>info@vietsacdo.com</p>
                    </div>
                </div>
            </div>

            <div class="contact-form">
                <form action="" id="contact-form" method="post">
                    <h2>Gửi tin nhắn</h2>
                    <c:if test="${not empty success}">
                        <p style="color: green; margin-bottom: 10px;">${success}</p>
                    </c:if>
                    <c:if test="${not empty error}">
                        <p style="color: red; margin-bottom: 10px;">${error}</p>
                    </c:if>
                    <div class="input-box">
                        <input type="text" name="fullName" placeholder="Họ và Tên" required>
                    </div>

                    <div class="input-box">
                        <input type="email" name="email" placeholder="Địa chỉ Email" required>
                    </div>

                    <div class="input-box">
                        <textarea name="messageBody" placeholder="Lời nhắn..." required></textarea >

                    </div>

                    <div class="input-box">
                        <input type="submit" value="Gửi"  name="">
                    </div>
                </form>
            </div>

        </div>
    </div>
</section>
<jsp:include page="footer.jsp" />
</body>
</html>