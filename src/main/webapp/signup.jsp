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
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Trang Chủ</a></li>
                    <li class="breadcrumb-item"><a href="login.jsp">Tài khoản</a></li>
                    <li class="breadcrumb-item active" aria-current="page">Đăng ký</li>
                </ol>
            </nav>
        </div>
        <main>
            <section class="section">
                <div class="container">
                    <div class="wrap_background" id="signup_view">
                        <div class="heading-bar">
                            <h1>Đăng ký tài khoản</h1>
                            <p>Bạn đã có tài khoản ? <a href="login.jsp">Đăng nhập tại đây</a></p>
                        </div>
                        <div class="row">
                            <div class="col">
                                <form class="page_auth" id="signup" action="signup" method="post">
                                    <% String error=(String) request.getAttribute("error"); %>
                                        <% if (error !=null) { %>
                                            <div style="color: red; margin-bottom: 10px; font-weight: bold;">
                                                <%= error %>
                                            </div>
                                            <% } %>

                                                <div id="js-error"
                                                    style="color: red; margin-bottom: 10px; font-weight: bold; display: none;">
                                                </div>
                                                <fieldset class="form-group">
                                                    <label>
                                                        Họ và tên
                                                        <span class="req">*</span>
                                                    </label>
                                                    <input type="text" name="fullname" id="name" placeholder="Họ và tên"
                                                        required autocomplete="name">
                                                </fieldset>
                                                <fieldset class="form-group">
                                                    <label>
                                                        Số điện thoại
                                                        <span class="req">*</span>
                                                    </label>
                                                    <input type="text" name="phone" id="phone"
                                                        placeholder="Số điện thoại" required autocomplete="tel">
                                                </fieldset>
                                                <fieldset class="form-group">
                                                    <label>
                                                        Email
                                                        <span class="req">*</span>
                                                    </label>
                                                    <input type="email" name="email" id="customer_email"
                                                        placeholder="Email" required autocomplete="email">
                                                </fieldset>
                                                <fieldset class="form-group">
                                                    <label>
                                                        Mật khẩu
                                                        <span class="req">*</span>
                                                    </label>
                                                    <div class="password-wrapper">
                                                        <input type="password" name="password" id="customer_password"
                                                            placeholder="Mật khẩu" required autocomplete="new-password">
                                                        <i class="fa-regular fa-eye toggle-password"
                                                            toggle="customer_password"></i>
                                                    </div>
                                                </fieldset>
                                                <div>
                                                    <button type="submit">Đăng ký</button>
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
                </div>

            </section>
        </main>
        <jsp:include page="footer.jsp" />
    </body>

    </html>