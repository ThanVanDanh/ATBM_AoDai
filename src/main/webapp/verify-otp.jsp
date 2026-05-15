<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Việt Sắc Đỏ - Đăng ký</title>
    <link rel="icon" href="image/logoaodai.jpg" type="image/jpeg">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css" integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="style/auth.css">
    <script src="scripts/home.js"></script>
    <link rel="stylesheet" href="style/style-header.css">
    <script type="module" src="scripts/auth.js"></script>
    <link rel="stylesheet" href="style/footer.css">
    <link rel="stylesheet" href="style/breadcrumb.css">
</head>
<body>
<jsp:include page="header.jsp" />
<div class="breadcrumb-container">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Trang Chủ</a></li>
            <li class="breadcrumb-item"><a href="login.jsp">Tài khoản</a></li> <li class="breadcrumb-item active" aria-current="page">Đăng ký</li>
        </ol>
    </nav>
</div>
<main>
    <section class="section">
        <div class="container">
            <div class="wrap_background">
                <div class="heading-bar">
                    <h1>Nhập mã xác thực</h1>
                    <p>Mã OTP đã được gửi đến email của bạn</p>
                </div>
                <div class="row">
                    <div class="col">
                        <form class="page_auth" action="forgot-password" method="POST">
                            <input type="hidden" name="action" value="verify">

                            <fieldset class="form-group">
                                <label>Mã OTP <span class="req">*</span></label>
                                <input type="text" name="otp" placeholder="Nhập 6 số" required>
                            </fieldset>

                            <p style="color: green; font-style: italic;">${message}</p>
                            <p style="color: red; font-style: italic;">${error}</p>

                            <div>
                                <button type="submit">Xác nhận</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </section>
</main>
<jsp:include page="footer.jsp" />
</body>
</html>