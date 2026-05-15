<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
        <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Chi tiết sản phẩm</title>
                <link rel="icon" href="image/logoaodai.jpg" type="image/jpeg">
                <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap"
                    rel="stylesheet">
                <link rel="stylesheet" href="style/product-infomation.css">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css"
                    integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw=="
                    crossorigin="anonymous" referrerpolicy="no-referrer" />
                <script src="${pageContext.request.contextPath}/scripts/home.js"></script>
                <%-- <script src="scripts/product-information.js"></script>--%>
                    <link rel="stylesheet" href="style/style-header.css">
                    <link rel="stylesheet" href="style/footer.css">
                    <link rel="stylesheet" href="style/backtop.css">
                    <script src="scripts/backtop.js"></script>
                    <link rel="stylesheet" href="style/breadcrumb.css">
                    <link rel="stylesheet" href="style/style.css">
                    <link rel="stylesheet" href="style/quick-view.css">
                    <link rel="stylesheet" href="https://unpkg.com/swiper/swiper-bundle.min.css">
                    <script src="https://unpkg.com/swiper/swiper-bundle.min.js"></script>
            </head>

            <body>
                <c:set var="pageTitle" value="${currentCategory.nameCategory}" scope="request" />
                <jsp:include page="header.jsp" />

                <div class="product-container">
                    <div class="product-image-gallery">
                        <div class="thumbnails">
                            <c:forEach items="${p.images}" var="img" varStatus="status">
                                <img src="${img.imageUrl}" alt="Ảnh nhỏ ${status.count}"
                                    class="thumbnail ${status.first ? 'active' : ''}">
                            </c:forEach>
                        </div>
                        <div class="main-image-wrapper">
                            <c:if test="${not empty p.images}">
                                <img id="mainImg" src="${p.images[0].imageUrl}" alt="${p.nameProduct}"
                                    class="main-image">
                            </c:if>
                            <c:if test="${empty p.images}">
                                <img id="mainImg" src="image/default.jpg" alt="Chưa có ảnh" class="main-image">
                            </c:if>
                            <a class="left carousel-control fui-arrow-left" href="#myCarousel" data-slide="prev"><i
                                    class="fa-solid fa-chevron-left"></i></a>
                            <a class="right carousel-control fui-arrow-right" href="#myCarousel" data-slide="next"><i
                                    class="fa-solid fa-chevron-right"></i></a>
                        </div>
                    </div>

                    <div class="product-details">
                        <h1>${p.nameProduct}</h1>
                        <p class="sku">
                            Mã sản phẩm: <span id="sku-value">${p.variants[0].sku}</span>
                        </p>

        <div class="product-price">
            <c:set var="v" value="${p.variants[0]}" />
            <c:choose>
                <c:when test="${v.discountedPrice > 0 && v.discountedPrice < v.currentPrice}">
                    <span class="old-price"><fmt:formatNumber value="${v.currentPrice}" pattern="#,###"/>₫</span>
                    <span class="current-price" id="display-price"><fmt:formatNumber value="${v.discountedPrice}" pattern="#,###"/>₫</span>
                        <c:set var="percent" value="${Math.round((1 - v.discountedPrice/v.currentPrice) * 100)}" />
                            <span class="discount-tag">${percent}%</span>
                    <p class="saving">(<span class="save">Tiết kiệm </span><span class="price"><fmt:formatNumber value="${v.currentPrice - v.discountedPrice}" pattern="#,###"/>₫</span>)</p>
                </c:when>
                <c:otherwise>
                    <span class="current-price" id="display-price"><fmt:formatNumber value="${v.currentPrice}" pattern="#,###"/>₫</span>
                        <span class="old-price" style="display: none;"></span>
                        <span class="discount-tag" style="display: none;"></span>
                        <p class="saving" style="display: none;"></p>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="section-title">Kích thước: <span id="selected-size">${p.variants[0].size}</span></div>

                        <div class="size-options">
                            <c:forEach items="${p.variants}" var="variant" varStatus="status">
                                <button class="size-btn ${status.first ? 'active' : ''}"
                                    data-price="${variant.discountedPrice}" data-old-price="${variant.currentPrice}"
                                    data-sku="${variant.sku}" data-color="${variant.color}"
                                    data-stock="${variant.stockQuantity}"
                                    onclick="updateVariant(this, '${variant.size}')">
                                    ${variant.size}
                                </button>
                            </c:forEach>
                        </div>

                        <div class="section-title">
                            Màu sắc: <span id="selected-color">${p.variants[0].color}</span>
                        </div>
                        <div class="purchase-actions">
                            <input type="hidden" id="selectedVariantPrice" value="${p.variants[0].currentPrice}">
                            <input type="hidden" id="selectedVariantSku" value="${p.variants[0].sku}">
                            <input type="hidden" id="selectedVariantSize" value="${p.variants[0].size}">
                            <div class="quantity-control" id="quality">
                                <button type="button" class="qty-btn qty-minus">-</button>
                                <input type="text" name="quantity" id="product-quantity" value="1" readonly>
                                <button type="button" class="qty-btn qty-plus">+</button>
                            </div>
                            <button class="add-to-cart-btn" id="them-vao-gio-hang" onclick="addToCart(${p.id})">THÊM VÀO
                                GIỎ</button>
                        </div>
                        <button class="buy-now-btn" id="mua-ngay" onclick="buyNow(${p.id})">MUA NGAY</button>
                        <button id="het-hang" class="het_hang" style="display:none;">Hết hàng </button>
                        <hr class="dashed-line">
                        <details class="baohanh-details">
                            <summary>Xem thêm chi tiết</summary>
                            <div class="content-chitiet">
                                <h5>Thông tin sản phẩm</h5>
                                <c:choose>
                                    <c:when test="${not empty p.description}">
                                        <div>${p.description}</div>
                                    </c:when>
                                    <c:otherwise>
                                        <p>Chưa có thông tin chi tiết cho sản phẩm này.</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </details>
                        <hr class="dashed-line">
                        <details class="doitra-details">
                            <summary>Chính sách đổi trả</summary>
                            <hr>
                            <c:choose>
                                <c:when test="${not empty policy and not empty policy.policyText}">
                                    <div class="content-chinhsach">
                                        ${policy.policyText}
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="content-chinhsach">
                                        <p>Chưa có chính sách đổi trả cho sản phẩm này</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </details>
                    </div>

                </div>
                <section class="product-showcase-information tab-component">
                    <div class="title-h1-linen">
                        <h1>SẢN PHẨM CÙNG LOẠI</h1>
                    </div>

                    <div class="product-grid">
                        <c:if test="${not empty relatedProducts}">
                            <c:forEach items="${relatedProducts}" var="rp">
                                <div class="product-card">
                                    <div class="product-image-wrapper">
                                        <div class="product-image">
                                                <a href="${pageContext.request.contextPath}/product-detail?id=${rp.id}">
                                                    <img src="${rp.thumbnail}" alt="${rp.nameProduct}">
                                                </a>
                                        </div>
                                        <div class="product-overlay">
                                            <a href="${pageContext.request.contextPath}/product-detail?id=${rp.id}"
                                                class="icon-button" title="Xem chi tiết">
                                                Xem chi tiết
                                            </a>
                                        </div>
                                    </div>

                                    <div class="product-info">
                                        <a href="${pageContext.request.contextPath}/product-detail?id=${rp.id}">
                                            <p class="product-name">${rp.nameProduct}</p>
                                        </a>
                                        <div class="product-price">
                                            <c:choose>
                                                    <c:when
                                                        test="${rp.discountedPrice > 0 && rp.discountedPrice < rp.price}">
                                                        <div class="current-price">
                                                            <fmt:formatNumber value="${rp.discountedPrice}"
                                                                pattern="#,###" />₫
                                                        </div>

                                                        <div class="price-meta">
                                                            <span class="old-price">
                                                                <fmt:formatNumber value="${rp.price}" pattern="#,###" />
                                                                ₫
                                                            </span>
                                                            <c:set var="percent"
                                                                value="${Math.round((1 - rp.discountedPrice/rp.price) * 100)}" />
                                                            <span class="discount-tag">
                                                                -${percent}%
                                                            </span>
                                                        </div>
                                                    </c:when>

                                                        <c:otherwise>
                                                            <div class="current-price">
                                                                <fmt:formatNumber value="${rp.price}" pattern="#,###" />
                                                                ₫
                                                            </div>
                                                        </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>

                        <c:if test="${empty relatedProducts}">
                            <p style="text-align:center; width:100%; color: #999;">Không có sản phẩm cùng loại nào khác.
                            </p>
                        </c:if>
                    </div>
                </section>
                <section class="product-showcase-information tab-component">
                    <div class="title-h1-linen">
                        <h1>SẢN PHẨM ĐÃ XEM</h1>
                    </div>
                    <div class="product-grid">
                        <c:if test="${not empty viewedProducts}">
                            <c:forEach items="${viewedProducts}" var="vp">
                                <c:if test="${vp.id != p.id}">

                                    <div class="product-card">
                                        <div class="product-image-wrapper">
                                            <div class="product-image">
                                                <a href="product-detail?id=${vp.id}">
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
                                            <a href="product-detail?id=${vp.id}">
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

                                </c:if>
                            </c:forEach>
                        </c:if>
                        <c:if test="${empty viewedProducts}">
                            <div style="width: 100%; text-align: center; padding: 20px;">
                                <p style="color: #666;">Bạn chưa xem sản phẩm nào gần đây.</p>
                            </div>
                        </c:if>
                    </div>
                </section>
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
                                <button class="add-shopping" onclick="addToCart(${product.id})"><span>Thêm vào
                                        giỏ</span></button>
                            </div>
                        </div>
                        <button id="close-model" class="model-remove-item"><i class="fa-solid fa-xmark"></i></button>
                    </div>
                </div>
                <div id="success-add-shopping" class="model-success-overlay">
                    <div class="success-content-model">
                        <button id="close-success-popup" class="model-remove-item" onclick="closeSuccessPopup()"><i
                                class="fa-solid fa-xmark"></i></button>

                        <div class="success-header">
                            <i class="fa-solid fa-check-circle"></i>
                            <span>Thêm vào giỏ hàng thành công</span>
                        </div>

                        <div class="success-product-info">
                            <img id="popup-img" src="" alt="">
                            <div class="item-info">
                                <a href="#" class="item-name" id="popup-name"></a>
                                <span class="item-meta" id="popup-meta"></span>
                            </div>
                        </div>

                        <div class="success-box-quantity">
                            <span>Giỏ hàng hiện có</span>
                            <div class="item-price">
                                <span id="popup-total-price"></span>
                                <small>(<span id="popup-total-quantity"></span>) sản phẩm</small>
                            </div>
                        </div>

                        <div class="success-footer">
                            <a href="${pageContext.request.contextPath}/checkout" class="checkout-btn">Thanh toán</a>
                            <a href="cart.jsp" class="success-btn">Xem giỏ hàng</a>
                        </div>
                    </div>
                </div>
                <jsp:include page="footer.jsp" />
                <button onclick="scrollToTop()" id="backToTopBtn" title="Trở về đầu trang">
                    <i class="fas fa-chevron-up"></i>
                </button>
                <script src="${pageContext.request.contextPath}/scripts/product-information.js"></script>
                <script src="${pageContext.request.contextPath}/scripts/cart.js"></script>
            </body>

            </html>