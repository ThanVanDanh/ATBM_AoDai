<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Việt Sắc Đỏ - Giỏ hàng</title>
                <link rel="icon" href="image/logoaodai.jpg" type="image/jpeg">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css"
                    integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw=="
                    crossorigin="anonymous" referrerpolicy="no-referrer" />
                <link rel="stylesheet" href="style/giohang.css">
                <link rel="stylesheet" href="style/style-header.css">
                <script src="scripts/home.js"></script>
                <link rel="stylesheet" href="style/footer.css">
                <link rel="stylesheet" href="style/backtop.css">
                <script src="scripts/backtop.js"></script>
                <link rel="stylesheet" href="style/breadcrumb.css">
                <link rel="stylesheet" href="style/aodai.css">
                <link rel="stylesheet" href="https://unpkg.com/swiper/swiper-bundle.min.css">
                <script src="https://unpkg.com/swiper/swiper-bundle.min.js"></script>
                <link rel="stylesheet" href="style/quick-view.css">
            </head>

            <body>
                <jsp:include page="header.jsp" />
                <div class="breadcrumb-container">
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Trang Chủ</a>
                            </li>
                            <li class="breadcrumb-item active" aria-current="page">Giỏ hàng</li>
                        </ol>
                    </nav>
                </div>
                <main>
                    <section class="shopping-cart">
                        <div class="container-sp">
                            <div class="header-cart-sp">
                                <h1>GIỎ HÀNG</h1>
                            </div>
                            <c:choose>
                                <c:when test="${empty sessionScope.cart or sessionScope.cart.totalQuantity == 0}">
                                    <div id="empty-cart-message" style="text-align: center; padding: 50px 20px;">
                                        <img src="./image/empty_cart.png" alt="Giỏ hàng trống"
                                            style="width: 100px; margin-bottom: 20px; opacity: 0.5;">
                                        <h3>Giỏ hàng của bạn đang trống</h3>
                                        <p>Hãy dạo một vòng và chọn những món đồ ưng ý nhé!</p>
                                        <a href="all-product.jsp" class="btn-pay"
                                            style="margin-top: 20px; text-decoration: none;">Tiếp tục mua sắm</a>
                                    </div>
                                </c:when>

                                <c:otherwise>
                                    <div class="content-sp" id="cart-data-container">
                                        <div class="cart-content-sp">
                                            <div class="upsell-card-sp">
                                                <c:set var="percent"
                                                    value="${(sessionScope.cart.totalPrice / 1000000) * 100}" />
                                                <c:if test="${percent > 100}">
                                                    <c:set var="percent" value="100" />
                                                </c:if>
                                                <div class="progress-bar-sp">
                                                    <div class="progress-sp"
                                                        style="width: <fmt:formatNumber value='${percent}' maxFractionDigits='0'/>%; background-color: darkgreen;">
                                                    </div>
                                                    <span class="percent-sp">
                                                        <fmt:formatNumber value="${percent}" maxFractionDigits="0" />%
                                                    </span>
                                                </div>
                                                <p style="margin-top: 10px;">
                                                    <c:choose>
                                                        <c:when test="${sessionScope.cart.totalPrice >= 1000000}">
                                                            Chúc mừng! Đơn hàng của bạn đã đủ điều kiện được
                                                            <strong>Freeship!</strong>
                                                        </c:when>
                                                        <c:otherwise>
                                                            Mua thêm <strong>
                                                                <fmt:formatNumber
                                                                    value="${1000000 - sessionScope.cart.totalPrice}"
                                                                    pattern="#,###" />₫
                                                            </strong> để được Freeship!
                                                        </c:otherwise>
                                                    </c:choose>
                                                </p>
                                            </div>
                                            <ul class="cart-items-list-sp">
                                                <c:forEach var="item" items="${sessionScope.cart.items}">
                                                    <li class="cart-item-sp">
                                                        <a href="${pageContext.request.contextPath}/cart?action=remove&id=${item.product.id}&sku=${item.sku}"
                                                            class="remove-item"
                                                            onclick="return confirm('Bạn có chắc muốn xóa sản phẩm này?')">
                                                            <i class="fa-solid fa-xmark"></i>
                                                        </a>

                                                        <img src="${not empty item.product.images ? item.product.images[0].imageUrl : 'image/no-image.png'}"
                                                            alt="${item.product.nameProduct}">

                                                        <div class="item-info-sp">
                                                            <a href="product-detail?id=${item.product.id}"
                                                                class="item-name-sp">${item.product.nameProduct}</a>
                                                            <span class="item-meta-sp">Mã:
                                                                ${item.product.productCode}</span>
                                                        </div>

                                                        <div class="box-quantity-sp">
                                                            <div class="item-price-sp">
                                                                <span>
                                                                    <fmt:formatNumber value="${item.price}"
                                                                        pattern="#,###" />₫
                                                                </span>
                                                            </div>

                                                            <form action="cart" method="post" class="quatity-sp"
                                                                style="display: flex; align-items: center;">
                                                                <input type="hidden" name="action" value="update">
                                                                <input type="hidden" name="id"
                                                                    value="${item.product.id}">
                                                                <input type="hidden" name="sku" value="${item.sku}">
                                                                <button type="submit" name="quantity"
                                                                    value="${item.quantity - 1}" class="btn-minus"><i
                                                                        class="fa-solid fa-minus"></i></button>
                                                                <input type="text" value="${item.quantity}"
                                                                    class="quantity-input" readonly>

                                                                <button type="submit" name="quantity"
                                                                    value="${item.quantity + 1}" class="btn-plus"><i
                                                                        class="fa-solid fa-plus"></i></button>
                                                            </form>
                                                        </div>
                                                    </li>
                                                </c:forEach>
                                            </ul>

                                        </div>

                                        <div class="cart-price-sp" id="cart-summary">
                                            <div class="total-sp">
                                                <h3>Tổng cộng</h3>
                                                <span>
                                                    <fmt:formatNumber value="${sessionScope.cart.totalPrice}"
                                                        pattern="#,###" />đ
                                                </span>
                                            </div>
                                            <div>
                                                <button><span><a
                                                            href="${not empty sessionScope.account ? 'checkout' : 'login.jsp'}"
                                                            style="color: white; text-decoration: none;">Thanh
                                                            toán</a></span></button>
                                            </div>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </section>
                </main>
                <div id="quick-view-model" class="model-overlay">
                    <div class="quick-content-model">
                        <div class="model-image">
                            <img class="large-image" src="image/truyenthong1.png" alt="Áo dài truyền thống Quỳnh Hân">
                            <div class="model-group-img swiper">
                                <div class="swiper-wrapper">
                                    <div class="swiper-slide"><img src="image/truyenthong1.png"></div>
                                    <div class="swiper-slide"><img src="image/truyenthong12.png"></div>
                                    <div class="swiper-slide"><img src="image/truyenthong13.png"></div>
                                    <div class="swiper-slide"><img src="image/truyenthong14.png"></div>
                                    <div class="swiper-slide"><img src="image/truyenthong15.png"></div>
                                    <div class="swiper-slide"><img src="image/truyenthong16.png"></div>
                                </div>
                            </div>
                        </div>
                        <div class="model-content">
                            <h2>Áo dài truyền thống Quỳnh Hân</h2>
                            <div class="model-info">
                                <p>Thương hiệu: <a href="index.jsp">Việt Sắc Đỏ</a>
                                    <span>|</span>
                                    Mã sản phẩm: <span>ADTT2326A</span>
                                </p>
                            </div>
                            <div class="product-price">
                                <span class="current-price">671,500₫</span>
                                <span class="old-price">790,000₫</span>
                                <span class="discount-tag">15%</span>
                            </div>
                            <div class="model-size">
                                <label>Kích thước:</label>
                                <div class="group-size">
                                    <input type="radio" name="size" id="size-s">
                                    <label for="size-s">S</label>
                                    <input type="radio" name="size" id="size-m">
                                    <label for="size-m">M</label>
                                    <input type="radio" name="size" id="size-l">
                                    <label for="size-l">L</label>
                                    <input type="radio" name="size" id="size-Xl">
                                    <label for="size-Xl">XL</label>
                                    <input type="radio" name="size" id="size-XXl">
                                    <label for="size-XXl">XXL</label>
                                </div>
                                <div class="size-table">
                                    <a href="#">Bảng quy đổi kích cỡ</a>
                                    <div class="size">
                                        <img src="image/size-ao.png" alt="">
                                    </div>
                                </div>
                            </div>
                            <div class="model-color">
                                <label>Màu sắc:</label>
                                <div class="group-color">
                                    <input type="radio" name="color-swatch" class="color-swatch"
                                        style="background: #cab6a1">
                                    <input type="radio" name="color-swatch" class="color-swatch"
                                        style="background: #d575c9">
                                    <input type="radio" name="color-swatch" class="color-swatch"
                                        style="background: #e84569">
                                    <input type="radio" name="color-swatch" class="color-swatch"
                                        style="background: #5b25bc">
                                </div>
                            </div>
                            <div class="model-promotion">
                                <span class="promotion-title">🎁 KHUYẾN MÃI - ƯU ĐÃI</span>
                                <ul class="promotion-box">
                                    <li>Freeship toàn quốc khi mua hàng (Không áp dụng cho đơn CHỈ CÓ phụ kiện dưới
                                        350k)</li>
                                    <li>Hỏa tốc mọi ngày trong tuần</li>
                                </ul>
                            </div>
                            <div class="model-footer">
                                <div class="quantity">
                                    <button type="button" class="btn-minus"><i class="fa-solid fa-minus"></i></button>
                                    <input type="text" value="1" class="quantity-input">
                                    <button type="button" class="btn-plus"><i class="fa-solid fa-plus"></i></button>
                                </div>
                                <button class="add-shopping"><span>Thêm vào giỏ</span></button>
                            </div>
                        </div>
                        <button id="close-model" class="model-remove-item"><i class="fa-solid fa-xmark"></i></button>
                    </div>
                </div>
                <jsp:include page="footer.jsp" />
                <button onclick="scrollToTop()" id="backToTopBtn" title="Trở về đầu trang">
                    <i class="fas fa-chevron-up"></i>
                </button>

            </body>

            </html>