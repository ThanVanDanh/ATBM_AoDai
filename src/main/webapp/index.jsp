<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Việt Sắc Đỏ - Trang Chủ</title>
    <link rel="icon" href="${pageContext.request.contextPath}/image/logoaodai.jpg" type="image/jpeg">
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="https://unpkg.com/swiper/swiper-bundle.min.css">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/aodai.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/backtop.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style-header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/quick-view.css">
    
    <script src="https://unpkg.com/swiper/swiper-bundle.min.js"></script>
</head>
<body>

<jsp:include page="header.jsp" />

<main>
    <div class="pixfort_pix_slider pix_builder_bg" id="section_slider">
        <div class="container">
            <div class="sixteen columns">
                <div id="myCarousel" class="carousel slide" data-interval="false">
                    <div class="carousel-inner">
                        <c:choose>
                            <c:when test="${not empty banners}">
                                <c:forEach var="banner" items="${banners}" varStatus="status">
                                    <div class="item ${status.index == 0 ? 'active' : ''}">
                                        <img src="${banner.imageUrl}" alt="${not empty banner.altText ? banner.altText : 'Banner'}">
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="item active">
                                    <a href="${pageContext.request.contextPath}/promotionsPost.jsp">
                                        <img src="${pageContext.request.contextPath}/image/aodai.png" alt="Banner 1">
                                    </a>
                                </div>
                                <div class="item">
                                    <a href="${pageContext.request.contextPath}/promotion.jsp">
                                        <img src="${pageContext.request.contextPath}/image/linen.png" alt="Banner 2">
                                    </a>
                                </div>
                                <div class="item">
                                    <a href="${pageContext.request.contextPath}/promotion.jsp">
                                        <img src="${pageContext.request.contextPath}/image/phukien.png" alt="Banner 3">
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <a class="left carousel-control fui-arrow-left" href="#myCarousel" data-slide="prev">
                        <i class="fa-solid fa-chevron-left"></i>
                    </a>
                    <a class="right carousel-control fui-arrow-right" href="#myCarousel" data-slide="next">
                        <i class="fa-solid fa-chevron-right"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
    <c:forEach var="section" items="${dynamicSections}" varStatus="sectionStatus">
        <c:set var="sectionClass" value="${sectionStatus.index % 2 == 0 ? 'product-showcase' : 'product-showcase-top'}" />
        
        <section class="${sectionClass} tab-component">
            <div class="signature-header">
                <h1>${section.title}</h1>
            </div>
            <nav class="main-nav">
                <ul>
                    <c:forEach var="tab" items="${section.tabs}" varStatus="tabStatus">
                        <li>
                            <a href="#" class="tab-link ${tabStatus.index == 0 ? 'active' : ''}"
                               data-target="sec-${section.key}-tab-${tab.index}">
                                ${tab.title}
                            </a>
                        </li>
                    </c:forEach>
                </ul>
            </nav>

            <c:forEach var="tab" items="${section.tabs}" varStatus="tabStatus">
                <div id="sec-${section.key}-tab-${tab.index}"
                     class="product-gallery ${tabStatus.index == 0 ? 'active-gallery' : 'hidden-gallery'} tab-content">

                    <c:forEach var="product" items="${tab.products}">
                        <div class="product-card">
                            <div class="product-image-wrapper">
                                <div class="product-image">
                                    <c:set var="thumbnail" value="${not empty product.thumbnail ? product.thumbnail : ''}" />
                                    <c:if test="${empty thumbnail}">
                                        <c:set var="thumbnail" value="${pageContext.request.contextPath}/image/no-image.png" />
                                    </c:if>
                                    <a href="${pageContext.request.contextPath}/product-detail?id=${product.id}">
                                        <img src="${thumbnail}" alt="${product.nameProduct}">
                                    </a>
                                </div>
                                <div class="product-overlay">
                                    <a href="${pageContext.request.contextPath}/product-detail?id=${product.id}" 
                                       class="icon-button" title="Xem chi tiết">
                                        Xem chi tiết
                                    </a>

                                </div>
                            </div>
                            <div class="product-info">
                                <a href="${pageContext.request.contextPath}/product-detail?id=${product.id}">
                                    <p class="product-name">${product.nameProduct}</p>
                                </a>
                                <div class="product-price">
                                    <c:choose>
                                        <c:when test="${product.discountedPrice > 0 && product.discountedPrice < product.price}">
                                            <div class="current-price">
                                                <fmt:formatNumber value="${product.discountedPrice}" pattern="#,###"/>₫
                                            </div>
                                            <div class="price-meta">
                                        <span class="old-price">
                                            <fmt:formatNumber value="${product.price}" pattern="#,###"/>₫
                                        </span>
                                                <c:if test="${product.price > 0 && product.discountedPrice < product.price}">
                                                    <c:set var="rawPercent" value="${(1 - product.discountedPrice / product.price) * 100}" />
                                                    <span class="discount-tag">-<fmt:formatNumber value="${rawPercent}" maxFractionDigits="0"/>%</span>
                                                </c:if>
                                            </div>
                                        </c:when>

                                        <c:otherwise>
                                            <div class="current-price">
                                                <fmt:formatNumber value="${product.price}" pattern="#,###"/>₫
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                    <c:if test="${empty tab.products}">
                        <div class="empty-products" style="text-align: center; padding: 40px; color: #999;">
                            <i class="fas fa-box-open" style="font-size: 48px; margin-bottom: 15px;"></i>
                            <p>Chưa có sản phẩm trong danh mục này</p>
                        </div>
                    </c:if>
                </div>
            </c:forEach>
        </section>
    </c:forEach>

    <c:if test="${empty dynamicSections}">
        <section class="product-showcase">
            <div class="signature-header">
                <h1>Sản phẩm nổi bật</h1>
            </div>
            <div class="empty-products" style="text-align: center; padding: 60px; color: #999;">
                <i class="fas fa-cog" style="font-size: 48px; margin-bottom: 15px;"></i>
                <p>Chưa có cấu hình trang chủ. Vui lòng cấu hình trong Admin.</p>
            </div>
        </section>
    </c:if>
</main>
<div id="success-add-shopping" class="model-success-overlay">
    <div class="success-content-model">
        <button id="close-success-popup" class="model-remove-item"><i class="fa-solid fa-xmark"></i></button>
        <div class="success-header">
            <i class="fa-solid fa-check-circle"></i>
            <span>Thêm vào giỏ hàng thành công</span>
        </div>
        <div class="success-footer">
            <a href="${pageContext.request.contextPath}/checkout" class="checkout-btn">Thanh toán</a>
            <a href="${pageContext.request.contextPath}/cart" class="success-btn">Xem giỏ hàng</a>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />

<button onclick="scrollToTop()" id="backToTopBtn" title="Trở về đầu trang">
    <i class="fas fa-chevron-up"></i>
</button>

<script src="${pageContext.request.contextPath}/scripts/home.js"></script>
<script src="${pageContext.request.contextPath}/scripts/backtop.js"></script>
</body>
</html>
