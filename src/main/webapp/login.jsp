<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Việt Sắc Đỏ - Đăng nhập</title>
        <link rel="icon" href="image/logoaodai.jpg" type="image/jpeg">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css"
            integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw=="
            crossorigin="anonymous" referrerpolicy="no-referrer" />
        <link rel="stylesheet" href="style/auth.css?v=1">
        <script type="module" src="scripts/auth.js"></script>
        <link rel="stylesheet" href="style/style-header.css">
        <link rel="stylesheet" href="style/footer.css">
        <link rel="stylesheet" href="style/breadcrumb.css">
        <script src="scripts/home.js"></script>
    </head>

    <body>
        <jsp:include page="header.jsp" />
        <div class="breadcrumb-container">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Trang Chủ</a></li>
                    <li class="breadcrumb-item"><a href="login.html">Tài khoản</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Đăng nhập</li>
                </ol>
            </nav>
        </div>
        <main>
            <section class="section">
                <div class="container">
                    <div class="wrap_background" id="login_view">
                        <div class="heading-bar">
                            <h1>Đăng nhập tài khoản</h1>
                            <p>Bạn chưa có tài khoản? <a href="signup.jsp">Đăng ký tại đây</a></p>
                        </div>
                        <div class="row">
                            <div class="col">
                                <form class="page_auth" id="login" action="Login" method="POST">
                                    <fieldset class="form-group">
                                        <label>
                                            Số điện thoại hoặc Email
                                            <span class="req">*</span>
                                        </label>
                                        <input type="text" id="username" name="username"
                                            placeholder="Nhập email hoặc số điện thoại...">
                                    </fieldset>
                                    <fieldset class="form-group">
                                        <label>
                                            Mật khẩu
                                            <span class="req">*</span>
                                        </label>
                                        <div class="password-wrapper">
                                            <input type="password" id="customer_password" name="password"
                                                placeholder="Mật khẩu" required autocomplete="current-password">
                                            <i class="fa-regular fa-eye toggle-password" toggle="customer_password"></i>
                                        </div>
                                        <small>Quên mật khẩu? Nhấn vào <a href="#" id="show_forgot_view">đây</a></small>
                                    </fieldset>
                                    <% if (request.getAttribute("error") !=null) { %>
                                        <p style="color: red; font-style: italic;">${error}</p>
                                        <% } %>
                                            <div>
                                                <button type="submit">Đăng nhập</button>
                                            </div>
                                </form>
                                <div class="social-auth">
                                    <p>Hoặc đăng nhập bằng</p>
                                    <div class="wrap-social-auth">
                                        <button type="button" id="btn-google" aria-label="Đăng nhập bằng Google">
                                            <div class="btn-google">
                                                <i class="fa-brands fa-google"></i>
                                            </div>
                                            <div>Đăng nhập Google</div>
                                        </button>
                                        <button type="button" id="btn-facebook" aria-label="Đăng nhập bằng Facebook">
                                            <div class="btn-facebook">
                                                <i class="fab fa-facebook-f"></i>
                                            </div>
                                            <div>Đăng nhập Facebook</div>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="wrap_background" id="forgot_view" style="display: none;">
                        <div class="heading-bar">
                            <h1>Khôi phục mật khẩu</h1>
                            <p>Bạn đã nhớ mật khẩu? <a href="#" id="show_login_view">Quay lại đăng nhập</a></p>
                        </div>
                        <div class="row">
                            <div class="col">
                                <form class="page_auth" id="forgot_password_form" action="forgot-password"
                                    method="POST">
                                    <input type="hidden" name="action" value="request">
                                    <fieldset class="form-group">
                                        <label>
                                            Email
                                            <span class="req">*</span>
                                        </label>
                                        <input type="email" name="email" id="email_reset" placeholder="Email" required
                                            autocomplete="email">
                                        <small>Chúng tôi sẽ gửi mã otp đến email để khôi phục mật khẩu của bạn.</small>
                                    </fieldset>
                                    <p style="color: red; font-style: italic;">${error}</p>
                                    <div>
                                        <button type="submit">Gửi yêu cầu</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </main>
        <div class="modal-overlay" id="verifyModal">
            <div class="modal-box">
                <span class="close-modal-x">&times;</span>
                <div class="modal-icon">
                    <i class="fa-regular fa-envelope-open"></i>
                </div>
                <div class="modal-title">Đăng ký thành công!</div>
                <div class="modal-message">
                    Xin chào, tài khoản của bạn đã được tạo.<br>
                    Vui lòng kiểm tra email <b><span id="userEmailDisplay"></span></b> và nhấp vào liên kết để kích hoạt
                    tài khoản trước khi đăng nhập.
                </div>
                <button class="modal-btn">Đã hiểu</button>
            </div>
            <% Boolean showPopup=(Boolean) session.getAttribute("showVerifyPopup"); String registeredEmail=(String)
                session.getAttribute("registeredEmail"); if (showPopup !=null && showPopup) { %>
                <input type="hidden" id="bridge-show-popup" value="true">
                <input type="hidden" id="bridge-user-email"
                    value="<%= (registeredEmail != null) ? registeredEmail : "" %>">
                <% session.removeAttribute("showVerifyPopup"); session.removeAttribute("registeredEmail"); } %>
        </div>
        <jsp:include page="footer.jsp" />
    </body>

    </html>