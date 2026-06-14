<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chương trình Khuyến Mãi</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles.css">
    <link rel="icon" href="${pageContext.request.contextPath}/image/logoaodai.jpg" type="image/jpeg">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css"
          integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/promotion.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style-header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/backtop.css">
    <script src="${pageContext.request.contextPath}/scripts/backtop.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/footer.css">
    <script src="${pageContext.request.contextPath}/scripts/home.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/breadcrumb.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/style.css">
    <script src="${pageContext.request.contextPath}/scripts/product-information.js"></script>
    <script src="${pageContext.request.contextPath}/scripts/auth.js"></script>
</head>
<body>

<jsp:include page="header.jsp"/>

<div class="breadcrumb-container">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Trang Chủ</a></li>
            <li class="breadcrumb-item active" aria-current="page">Chương trình Khuyến mãi</li>
        </ol>
    </nav>

    <div class="promotion-container">
        <h1>Chương trình Khuyến Mãi</h1>

        <div class="articles-list">
            <c:choose>
                <c:when test="${empty articles}">
                    <div style="text-align:center; padding:40px;">
                        <i class="fas fa-inbox" style="font-size:48px; color:#ccc;"></i>
                        <p style="color:#999; margin-top:20px;">Hiện chưa có chương trình khuyến mãi nào được công bố.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${articles}" var="article">
                        <article class="article-item">
                            <div class="article-image">
                                <a href="${pageContext.request.contextPath}/promotion-post?id=${article.id}">
                                    <c:choose>
                                        <c:when test="${not empty article.bannerImageUrl}">
                                            <img src="${article.bannerImageUrl}" alt="${article.title}" 
                                                 onerror="this.src='${pageContext.request.contextPath}/image/default-promotion.jpg'">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/image/default-promotion.jpg" alt="${article.title}">
                                        </c:otherwise>
                                    </c:choose>
                                </a>
                            </div>
                            <div class="article-content">
                                <h3>
                                    <a href="${pageContext.request.contextPath}/promotion-post?id=${article.id}">${article.title}</a>
                                    <c:if test="${not empty article.voucherCode}">
                                        <span style="display:inline-block; background:#e74c3c; color:white; padding:4px 8px; 
                                            border-radius:4px; font-size:12px; font-weight:bold; margin-left:10px;">
                                            <i class="fas fa-ticket-alt"></i> ${article.voucherCode}
                                        </span>
                                    </c:if>
                                </h3>
                                <div class="meta">
                                    <span>
                                        <i class="far fa-calendar-alt"></i> 
                                        <c:choose>
                                            <c:when test="${not empty article.startDate}">
                                                ${article.startDate.toString().substring(0, 10).replace('-', '/')}
                                            </c:when>
                                            <c:when test="${not empty article.createdAt}">
                                                ${article.createdAt.toString().substring(0, 10).replace('-', '/')}
                                            </c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <a href="${pageContext.request.contextPath}/promotion-post?id=${article.id}" class="read-more">
                                    Đọc thông tin chi tiết...
                                </a>
                            </div>
                        </article>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>

<button onclick="scrollToTop()" id="backToTopBtn" title="Trở về đầu trang">
    <i class="fas fa-chevron-up"></i>
</button>



</body>
</html>
