<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <title>${currentCategory.nameCategory} | Việt Sắc Đỏ</title>
                <link rel="icon" href="image/logoaodai.jpg" type="image/jpeg">
                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css" />

                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/breadcrumb.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/backtop.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/quick-view.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/aodai.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/footer.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style-header.css">

    <script src="${pageContext.request.contextPath}/scripts/home.js"></script>
</head>
<body>
<c:set var="pageTitle" value="${currentCategory.nameCategory}" scope="request" />
<jsp:include page="header.jsp" />
<main>
    <section class="product-showcase tab-component">
        <div class="title-h1-linen">
            <h1>${currentCategory.nameCategory}</h1>
        </div>

        <div class="toolbar-container">
            <div class="sort-by-wrapper">
                <label for="sort-by">Sắp xếp:</label>
                <div class="custom-select-wrapper">
                    <select id="sort-by" name="sort-by" onchange="location.href='?page=1&sort-by='+this.value">
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
                            <c:forEach var="pdto" items="${list}">
                                <div class="product-card">
                                    <div class="product-image-wrapper">
                                        <div class="product-image">
                                            <a href="${pageContext.request.contextPath}/product-detail?id=${pdto.id}">
                                                <img src="${pdto.thumbnail}" alt="${pdto.nameProduct}">
                                            </a>
                                        </div>
                                        <div class="product-overlay">
                                            <a href="${pageContext.request.contextPath}/product-detail?id=${pdto.id}"
                                                class="icon-button" title="Xem chi tiết">
                                                Xem chi tiết
                                            </a>
                                        </div>
                                    </div>

                                    <div class="product-info">
                                        <a href="${pageContext.request.contextPath}/product-detail?id=${pdto.id}">
                                            <p class="product-name">${pdto.nameProduct}</p>
                                        </a>

                                        <div class="product-price">
                                            <c:choose>
                                                    <c:when
                                                        test="${pdto.discountedPrice > 0 && pdto.discountedPrice < pdto.price}">
                                                        <div class="current-price">
                                                            <fmt:formatNumber value="${pdto.discountedPrice}"
                                                                pattern="#,###" />₫
                                                        </div>

                                                        <div class="price-meta">
                                                            <span class="old-price">
                                                                <fmt:formatNumber value="${pdto.price}"
                                                                    pattern="#,###" />₫
                                                            </span>
                                                            <c:set var="percent"
                                                                value="${Math.round((1 - pdto.discountedPrice/pdto.price) * 100)}" />
                                                            <span class="discount-tag">
                                                                -${percent}%
                                                            </span>
                                                        </div>
                                                    </c:when>

                                                        <c:otherwise>
                                                            <div class="current-price">
                                                                <fmt:formatNumber value="${pdto.price}"
                                                                    pattern="#,###" />₫
                                                            </div>
                                                        </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>

                        <div class="pagination">
                            <c:if test="${currentPage > 1}">
                                <a href="?page=${currentPage - 1}&sort-by=${sortBy}">
                                    <img src="${pageContext.request.contextPath}/image/chevron_left.png" alt="Prev">
                                </a>
                            </c:if>

                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <a href="?page=${i}&sort-by=${sortBy}"
                                    class="${currentPage == i ? 'active' : ''}">${i}</a>
                            </c:forEach>

                            <c:if test="${currentPage < totalPages}">
                                <a href="?page=${currentPage + 1}&sort-by=${sortBy}">
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
                                                    <a
                                                        href="${pageContext.request.contextPath}/product-detail?id=${vp.id}">
                                                        <img src="${vp.thumbnail}" alt="${vp.nameProduct}">
                                                    </a>
                                                </div>
                                                <div class="product-overlay">
                                                    <a href="${pageContext.request.contextPath}/product-detail?id=${vp.id}"
                                                        class="icon-button" title="Xem chi tiết">
                                                        Xem chi tiết
                                                    </a>
                                                </div>
                                            </div>

                                            <div class="product-info">
                                                <a href="${pageContext.request.contextPath}/product-detail?id=${vp.id}">
                                                    <p class="product-name">${vp.nameProduct}</p>
                                                </a>

                                                <div class="product-price">
                                                    <c:choose>
                                                            <c:when
                                                                test="${vp.discountedPrice > 0 && vp.discountedPrice < vp.price}">
                                                                <div class="current-price">
                                                                    <fmt:formatNumber value="${vp.discountedPrice}"
                                                                        pattern="#,###" />₫
                                                                </div>

                                                                <div class="price-meta">
                                                                    <span class="old-price">
                                                                        <fmt:formatNumber value="${vp.price}"
                                                                            pattern="#,###" />₫
                                                                    </span>
                                                                    <c:set var="percent"
                                                                        value="${Math.round((1 - vp.discountedPrice/vp.price) * 100)}" />
                                                                    <span class="discount-tag">
                                                                        -${percent}%
                                                                    </span>
                                                                </div>
                                                            </c:when>

                                                                <c:otherwise>
                                                                    <div class="current-price">
                                                                        <fmt:formatNumber value="${vp.price}"
                                                                            pattern="#,###" />₫
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

                <jsp:include page="footer.jsp" />
                <button onclick="scrollToTop()" id="backToTopBtn" title="Trở về đầu trang">
                    <i class="fas fa-chevron-up"></i>
                </button>
            </body>

            </html>