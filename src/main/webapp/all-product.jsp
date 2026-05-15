<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Áo dài Linen</title>
    <link rel="icon" href="image/logoaodai.jpg" type="image/jpeg">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css" integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="style/aodai.css">
    <link rel="stylesheet" href="style/style-header.css">
    <link rel="stylesheet" href="style/footer.css">
    <link rel="stylesheet" href="style/breadcrumb.css">
    <script src="scripts/home.js"></script>
    <script src="scripts/backtop.js"></script>
    <link rel="stylesheet" href="style/backtop.css">
    <link rel="stylesheet" href="https://unpkg.com/swiper/swiper-bundle.min.css">
    <script src="https://unpkg.com/swiper/swiper-bundle.min.js"></script>
    <link rel="stylesheet" href="style/quick-view.css">

</head>
<body>
<c:set var="pageTitle" value="${currentCategory.nameCategory}" scope="request" />
<c:set var="searchParam" value="${not empty searchKeyword ? '&search='.concat(searchKeyword) : ''}" />
<jsp:include page="header.jsp" />
<main>
    <section class="product-showcase tab-component">
        <div class="title-h1-linen">
            <h1>TẤT CẢ SẢN PHẨM</h1>
        </div>
        <div class="toolbar-container">
            <div class="sort-by-wrapper">
                <label for="sort-by">Sắp xếp:</label>
                <div class="custom-select-wrapper">
                    <select id="sort-by" name="sort-by" onchange="location.href='?page=1&sort-by='+this.value+'${searchParam}'">
                        <option value="alpha-asc" ${sortBy == 'alpha-asc' ? 'selected' : ''}>Tên A → Z</option>
                        <option value="alpha-desc" ${sortBy == 'alpha-desc' ? 'selected' : ''}>Tên Z → A</option>
                        <option value="price-asc" ${sortBy == 'price-asc' ? 'selected' : ''}>Giá tăng dần</option>
                        <option value="price-desc" ${sortBy == 'price-desc' ? 'selected' : ''}>Giá giảm dần</option>
                        <option value="created-desc" ${sortBy == 'created-desc' ? 'selected' : ''}>Hàng mới</option>
                    </select>
                    <span class="custom-arrow"></span>
                </div>
            </div>
        </div>
        <div class="product-grid">
            <c:choose>
                <c:when test="${empty list}">
                    <div style="grid-column: 1/-1; text-align: center; padding: 40px;">
                        <p>Hiện chưa có sản phẩm nào.</p>
                    </div>
                </c:when>

                <c:otherwise>
                    <c:forEach var="p" items="${list}">
                        <div class="product-card">
                            <div class="product-image-wrapper">
                                <div class="product-image">
                                    <a href="${pageContext.request.contextPath}/product-detail?id=${p.id}">
                                        <img src="${p.thumbnail}"
                                             alt="${p.nameProduct}"
                                             style="width: 100%; height: auto; object-fit: cover;"
                                             onerror="this.src='https://via.placeholder.com/300x400?text=No+Image'">
                                    </a>
                                </div>
                                <div class="product-overlay">
                                    <a href="${pageContext.request.contextPath}/product-detail?id=${p.id}"
                                       class="icon-button" title="Tùy chọn">
                                        <i class="fa-solid fa-cart-shopping"></i>
                                    </a>
                                    <a href="#" class="icon-button" title="Xem nhanh">
                                        <i class="fa-solid fa-eye"></i>
                                        Xem chi tiết
<%--                                        <i class="fa-solid fa-cart-shopping"></i>--%>
                                    </a>
                                </div>
                            </div>

                            <div class="product-info">
                                <a href="${pageContext.request.contextPath}/product-detail?id=${p.id}">
                                    <p class="product-name">${p.nameProduct}</p>
                                </a>

                                <div class="product-price">
                                    <c:choose>
                                        <c:when test="${p.discountedPrice > 0 && p.discountedPrice < p.price}">
                                            <div class="current-price">
                                                <fmt:formatNumber value="${p.discountedPrice}" pattern="#,###"/>₫
                                            </div>
                                            <div class="price-meta">
                                        <span class="old-price">
                                            <fmt:formatNumber value="${p.price}" pattern="#,###"/>₫
                                        </span>
                                                <c:if test="${p.price > 0 && p.discountedPrice < p.price}">
                                                    <c:set var="rawPercent" value="${(1 - p.discountedPrice / p.price) * 100}" />
                                                    <span class="discount-tag">-<fmt:formatNumber value="${rawPercent}" maxFractionDigits="0"/>%</span>
                                                </c:if>
                                            </div>
                                        </c:when>

                                        <c:otherwise>
                                            <div class="current-price">
                                                <fmt:formatNumber value="${p.price}" pattern="#,###"/>₫
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="pagination">
            <c:if test="${currentPage > 1}">
                <a href="?page=${currentPage - 1}&sort-by=${sortBy}${searchParam}">
                    <img src="${pageContext.request.contextPath}/image/chevron_left.png" alt="Prev">
                </a>
            </c:if>

            <c:forEach begin="1" end="${totalPages}" var="i">
                <a href="?page=${i}&sort-by=${sortBy}${searchParam}" class="${currentPage == i ? 'active' : ''}">${i}</a>
            </c:forEach>

            <c:if test="${currentPage < totalPages}">
                <a href="?page=${currentPage + 1}&sort-by=${sortBy}${searchParam}">
                    <img src="${pageContext.request.contextPath}/image/chevron_right.png" alt="Next">
                </a>
            </c:if>
        </div>
    </section>
    <section class="product-showcase tab-component">
        <div class="title-h1-linen">
            <h1>SẢN PHẨM ĐÃ XEM</h1>
        </div>

        <c:choose>
            <c:when test="${empty viewedProducts}">
                <div style="width: 100%; text-align: center; padding: 20px;">
                    <p style="color: #666;">Bạn chưa xem sản phẩm nào gần đây.</p>
                </div>
            </c:when>

            <c:otherwise>
                <div class="product-grid">
                    <c:forEach items="${viewedProducts}" var="vp">
                        <div class="product-card">
                            <div class="product-image-wrapper">
                                <div class="product-image">
                                    <a href="${pageContext.request.contextPath}/product-detail?id=${vp.id}">
                                        <img src="${vp.thumbnail}"
                                             alt="${vp.nameProduct}"
                                             style="width: 100%; height: auto; object-fit: cover;"
                                             onerror="this.src='https://via.placeholder.com/300x400?text=No+Image'">
                                    </a>
                                </div>
                                <div class="product-overlay">
                                    <a href="${pageContext.request.contextPath}/product-detail?id=${vp.id}" class="icon-button" title="Tùy chọn">
                                        <i class="fa-solid fa-cart-shopping"></i>
                                    </a>
                                    <a href="#" class="icon-button" title="Xem nhanh">
                                        <i class="fa-solid fa-eye"></i>
                                    </a>
                                </div>
                            </div>

                            <div class="product-info">
                                <a href="${pageContext.request.contextPath}/product-detail?id=${vp.id}">
                                    <p class="product-name">${vp.nameProduct}</p>
                                </a>

                                <div class="product-price">
                                    <c:choose>
                                        <c:when test="${vp.discountedPrice > 0 && vp.discountedPrice < vp.price}">
                                            <div class="current-price">
                                                <fmt:formatNumber value="${vp.discountedPrice}" pattern="#,###"/>₫
                                            </div>

                                            <div class="price-meta">
                                                <span class="old-price">
                                                    <fmt:formatNumber value="${vp.price}" pattern="#,###"/>₫
                                                </span>

                                                <c:if test="${vp.price > 0}">
                                                    <c:set var="rawPercent" value="${(1 - vp.discountedPrice / vp.price) * 100}" />
                                                    <span class="discount-tag">
                                                        -<fmt:formatNumber value="${rawPercent}" maxFractionDigits="0"/>%
                                                    </span>
                                                </c:if>
                                            </div>
                                        </c:when>

                                        <c:otherwise>
                                            <div class="current-price">
                                                <fmt:formatNumber value="${vp.price}" pattern="#,###"/>₫
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
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
                    <input type="radio" name="color-swatch" class="color-swatch" style="background: #cab6a1">
                    <input type="radio" name="color-swatch" class="color-swatch" style="background: #d575c9">
                    <input type="radio" name="color-swatch" class="color-swatch" style="background: #e84569">
                    <input type="radio" name="color-swatch" class="color-swatch" style="background: #5b25bc">
                </div>
            </div>
            <div class="model-promotion">
                <span class="promotion-title">🎁 KHUYẾN MÃI - ƯU ĐÃI</span>
                <ul class="promotion-box">
                    <li>Freeship toàn quốc khi mua hàng (Không áp dụng cho đơn CHỈ CÓ phụ kiện dưới 350k)</li>
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