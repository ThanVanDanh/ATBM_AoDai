<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Việt Sắc Đỏ - Đăng ký</title>
        <link rel="icon" href="image/logoaodai.jpg" type="image/jpeg">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css"
            integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw=="
            crossorigin="anonymous" referrerpolicy="no-referrer" />
        <link rel="stylesheet" href="style/auth.css?v=1">
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
                    <li class="breadcrumb-item"><a href="index.jsp">Trang Chủ</a></li>
                    <li class="breadcrumb-item"><a href="login.jsp">Tài khoản</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Đăng ký</li>
                </ol>
            </nav>
        </div>
        <main>
            <section class="section">
                <div class="container">
                    <div class="wrap_background">
                        <div class="heading-bar">
                            <h1>Đặt lại mật khẩu</h1>
                            <p>Nhập mật khẩu mới cho tài khoản của bạn</p>
                        </div>
                        <div class="row">
                            <div class="col">
                                <form class="page_auth" id="reset-password-form" action="forgot-password" method="POST">
                                    <input type="hidden" name="action" value="reset">

                                    <fieldset class="form-group">
                                        <label>Mật khẩu mới <span class="req">*</span></label>
                                        <div class="password-wrapper">
                                            <input type="password" name="new_password" id="new_password" required>
                                            <i class="fa-regular fa-eye toggle-password" toggle="new_password"></i>
                                        </div>
                                    </fieldset>

                                    <fieldset class="form-group">
                                        <label>Xác nhận mật khẩu <span class="req">*</span></label>
                                        <div class="password-wrapper">
                                            <input type="password" name="confirm_password" id="confirm_password"
                                                required>
                                            <i class="fa-regular fa-eye toggle-password" toggle="confirm_password"></i>
                                        </div>
                                    </fieldset>

                                    <p style="color: red; font-style: italic;">${error}</p>
                                    <div id="js-reset-error"
                                        style="color: red; margin-bottom: 10px; font-weight: bold; display: none;">
                                    </div>

                                    <div>
                                        <button type="submit">Đổi mật khẩu</button>
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