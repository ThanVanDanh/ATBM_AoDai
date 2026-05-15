
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}

function closeSuccessPopup() {
    let popup = document.getElementById("success-add-shopping");
    if (popup) popup.classList.remove('active');
}

function addToCart(productId, isBuyNow = false) {
    let quantityInput = document.getElementById("quantityInput") || document.getElementById("product-quantity");
    let quantity = quantityInput ? quantityInput.value : 1;

    let priceInput = document.getElementById("selectedVariantPrice");
    let price = priceInput ? priceInput.value : "";

    let skuInput = document.getElementById("selectedVariantSku");
    let currentSku = skuInput ? skuInput.value : "";

    let sizeInput = document.getElementById("selectedVariantSize");
    let currentSize = sizeInput ? sizeInput.value : "";

    let url = 'add-cart?productId=' + productId +
        '&quantity=' + quantity +
        '&price=' + price +
        '&sku=' + encodeURIComponent(currentSku) +
        '&size=' + encodeURIComponent(currentSize);

    fetch(url)
        .then(response => response.json())
        .then(data => {
            if (data.status === 'success') {

                document.querySelectorAll(".count_item_pr").forEach(el => {
                    el.innerText = data.totalQuantity;
                });
                document.querySelectorAll(".mini-count_item").forEach(el => {
                    el.innerText = data.totalQuantity;
                });
                let allEmptyCarts = document.querySelectorAll(".js-mini-cart-empty");
                let allHasItemCarts = document.querySelectorAll(".js-mini-cart-has-item");

                allEmptyCarts.forEach(el => el.style.display = "none");
                allHasItemCarts.forEach(el => el.style.display = "block");

                let listHtml = "";
                let itemsArray = [];
                if (Array.isArray(data.items)) itemsArray = data.items;
                else if (data.items) itemsArray = Object.values(data.items);

                if (itemsArray.length > 0) {
                    itemsArray.forEach(item => {
                        let product = item.product || {};
                        let images = product.images || [];
                        let imgUrl = (images.length > 0) ? images[0].imageUrl : 'image/no-image.png';
                        let productName = product.nameProduct || "Sản phẩm";

                        let itemSku = item.sku || "";
                        let itemSize = item.size || "";

                        let removeUrl = `cart?action=remove&id=${product.id}&sku=${encodeURIComponent(itemSku)}`;
                        listHtml += `
                        <li class="item-cart-row">
                            <div class="img-container">
                                <img src="${imgUrl}" alt="${productName}">
                            </div>
                            <div class="mini-item-info">
                                <a href="product-detail?id=${product.id}" class="mini-item-name">
                                    ${productName}
                                </a>
                                
                                <div class="mini-item-meta" style="font-size: 12px; color: #666; margin-bottom: 5px;">
                                    ${itemSize ? `Size: <strong>${itemSize}</strong>` : ''} 
                                    ${itemSku ? (itemSize ? ' / ' : '') + `Mã: ${itemSku}` : ''}
                                </div>

                                <span class="mini-item-price">${formatCurrency(item.price)}</span>
                                <span class="mini-quantity">x${item.quantity}</span>
                            </div>
                            <a href="${removeUrl}" class="remove-item" onclick="return confirm('Xóa sản phẩm này?')"><i class="fa-solid fa-xmark"></i> </a>
                       </li>`;
                    });
                }

                let allMiniLists = document.querySelectorAll(".js-mini-cart-list");
                allMiniLists.forEach(el => el.innerHTML = listHtml);

                let allTotalPrices = document.querySelectorAll(".js-mini-total-price");
                allTotalPrices.forEach(el => el.innerText = formatCurrency(data.totalPrice));

                const hasItems = itemsArray.length > 0;
                document.querySelectorAll('.mini-cart-menu').forEach(menu => {
                    const listEl = menu.querySelector('.mini-cart-items-list');
                    const footerEl = menu.querySelector('.mini-cart-footer');
                    const emptyEl = menu.querySelector('.mini-empty-cart');

                    if (listEl) listEl.style.display = hasItems ? 'block' : 'none';
                    if (footerEl) footerEl.style.display = hasItems ? 'block' : 'none';
                    if (emptyEl) emptyEl.style.display = hasItems ? 'none' : 'block';
                });

                if (isBuyNow) {
                    window.location.href = 'checkout';
                } else {
                    showSuccessPopup(quantity, currentSize, currentSku, data);
                }

            } else {
                alert("Lỗi: " + data.message);
            }
        })
        .catch(error => console.error('Error fetching cart:', error));
}

function buyNow(productId) {
    if (typeof window.isLoggedIn !== 'undefined' && !window.isLoggedIn) {
        window.location.href = 'login.jsp';
        return;
    }
    addToCart(productId, true);
}

function showSuccessPopup(quantity, currentSize, currentSku, data) {
    let popup = document.getElementById("success-add-shopping");
    if (!popup) return;

    let currentImg = document.getElementById("mainImg");
    let currentName = document.querySelector(".product-details h1");

    if (document.getElementById("popup-img") && currentImg)
        document.getElementById("popup-img").src = currentImg.src;

    if (document.getElementById("popup-name") && currentName)
        document.getElementById("popup-name").innerText = currentName.innerText;

    if (document.getElementById("popup-meta")) {
        let metaText = "";
        if (currentSize) metaText += "Size: " + currentSize;
        if (currentSku) metaText += (metaText ? " / " : "") + "Mã: " + currentSku;
        if (!metaText) metaText = "Số lượng: " + quantity;
        document.getElementById("popup-meta").innerText = metaText;
    }

    if (document.getElementById("popup-total-price"))
        document.getElementById("popup-total-price").innerText = formatCurrency(data.totalPrice);

    if (document.getElementById("popup-total-quantity"))
        document.getElementById("popup-total-quantity").innerText = data.totalQuantity;

    popup.classList.add('active');
}