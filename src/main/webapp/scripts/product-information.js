
let MAX_QTY = 1;

function updateVariant(element, sizeName) {
    document.querySelectorAll('.size-btn').forEach(btn => btn.classList.remove('active'));
    element.classList.add('active');

    const sizeDisplay = document.getElementById('selected-size');
    if (sizeDisplay) sizeDisplay.innerText = sizeName;

    const sizeInput = document.getElementById('selectedVariantSize');
    if (sizeInput) sizeInput.value = sizeName;

    // 2. Lấy dữ liệu từ data attributes
    const discountedPrice = parseFloat(element.getAttribute('data-price')) || 0; // Giá sau giảm
    const currentPrice = parseFloat(element.getAttribute('data-old-price')) || 0; // Giá gốc

    const priceDisplay = document.getElementById('display-price');
    const oldPriceDisplay = document.querySelector('.old-price');
    const discountTag = document.querySelector('.discount-tag');
    const savingSection = document.querySelector('.saving');
    const savingPrice = document.querySelector('.saving .price');

    if (discountedPrice > 0 && discountedPrice < currentPrice) {
        if (priceDisplay) priceDisplay.innerText = discountedPrice.toLocaleString('vi-VN') + '₫';
        if (oldPriceDisplay) {
            oldPriceDisplay.innerText = currentPrice.toLocaleString('vi-VN') + '₫';
            oldPriceDisplay.style.display = 'inline-block';
        }
        if (discountTag) {
            const percent = Math.round(((currentPrice - discountedPrice) / currentPrice) * 100);
            discountTag.innerText = percent + '%';
            discountTag.style.display = 'inline-block';
        }
        if (savingSection) {
            savingSection.style.display = 'block';
            if (savingPrice) savingPrice.innerText = (currentPrice - discountedPrice).toLocaleString('vi-VN') + '₫';
        }
    } else {
        if (priceDisplay) priceDisplay.innerText = currentPrice.toLocaleString('vi-VN') + '₫';
        if (oldPriceDisplay) oldPriceDisplay.style.display = 'none';
        if (discountTag) discountTag.style.display = 'none';
        if (savingSection) savingSection.style.display = 'none';
    }

    const sku = element.getAttribute('data-sku');
    const skuDisplay = document.getElementById('sku-value');
    if (skuDisplay && sku) skuDisplay.innerText = sku;

    // Cập nhật màu sắc
    const color = element.getAttribute('data-color');
    const colorDisplay = document.getElementById('selected-color');
    if (colorDisplay && color) colorDisplay.innerText = color;

    const skuInput = document.getElementById('selectedVariantSku');
    if (skuInput && sku) skuInput.value = sku;

    const priceInput = document.getElementById('selectedVariantPrice');
    if (priceInput) priceInput.value = (discountedPrice > 0) ? discountedPrice : currentPrice;

    MAX_QTY = parseInt(element.getAttribute('data-stock')) || 0;
    updateStockUI(MAX_QTY);
}

function updateStockUI(stock) {
    const addToCartBtn = document.getElementById('them-vao-gio-hang');
    const buyNowBtn = document.getElementById('mua-ngay');
    const outOfStockBtn = document.getElementById('het-hang');
    const qtyInput = document.getElementById('product-quantity');
    const quality = document.getElementById('quality');

    if (stock > 0) {
        addToCartBtn.style.display = 'inline-block';
        buyNowBtn.style.display = 'inline-block';
        outOfStockBtn.style.display = 'none';
        quality.style.display = 'inline-block';
    } else {
        addToCartBtn.style.display = 'none';
        buyNowBtn.style.display = 'none';
        outOfStockBtn.style.display = 'inline-block';
        quality.style.display = 'none';
        qtyInput.value = 1;
    }
}



document.addEventListener('DOMContentLoaded', function () {
    const qtyInput = document.getElementById('product-quantity');
    const btnMinus = document.querySelector('.qty-minus');
    const btnPlus = document.querySelector('.qty-plus');

    if (!qtyInput || !btnMinus || !btnPlus) return;

    const MIN_QTY = 1;

    btnPlus.addEventListener('click', () => {
        let qty = parseInt(qtyInput.value) || MIN_QTY;
        if (qty < MAX_QTY) {
            qtyInput.value = qty + 1;
        }
    });

    btnMinus.addEventListener('click', () => {
        let qty = parseInt(qtyInput.value) || MIN_QTY;
        if (qty > MIN_QTY) {
            qtyInput.value = qty - 1;
        }
    });

    const mainImage = document.getElementById('mainImg');
    const thumbnails = document.querySelectorAll('.thumbnail');
    const prevBtn = document.querySelector('.fa-chevron-left');
    const nextBtn = document.querySelector('.fa-chevron-right');
    const defaultActiveBtn = document.querySelector('.size-btn.active') || document.querySelector('.size-btn');

    if (defaultActiveBtn) {
        let stock = parseInt(defaultActiveBtn.getAttribute('data-stock'));
        if (isNaN(stock)) stock = 0;
        MAX_QTY = stock;
        updateStockUI(MAX_QTY);

        if (!document.querySelector('.size-btn.active')) {
            defaultActiveBtn.click();
        }
    }

    if (mainImage && thumbnails.length > 0) {
        const imagePaths = Array.from(thumbnails).map(thumb => thumb.src);
        let currentIndex = 0;

        const changeImage = (index) => {
            currentIndex = index;
            mainImage.src = imagePaths[currentIndex];
            thumbnails.forEach((t, i) => t.classList.toggle('active', i === currentIndex));
        };

        thumbnails.forEach((thumb, i) => {
            thumb.addEventListener('click', () => changeImage(i));
        });

        prevBtn?.parentElement.addEventListener('click', (e) => {
            e.preventDefault();
            currentIndex = (currentIndex - 1 + imagePaths.length) % imagePaths.length;
            changeImage(currentIndex);
        });

        nextBtn?.parentElement.addEventListener('click', (e) => {
            e.preventDefault();
            currentIndex = (currentIndex + 1) % imagePaths.length;
            changeImage(currentIndex);
        });
    }

    const btnAdd = document.getElementById('them-vao-gio-hang');
    const popup = document.getElementById('success-add-shopping');
    const btnClose = document.getElementById('close-success-popup');

    if (btnClose) btnClose.onclick = () => popup.classList.remove('active');

    window.addEventListener('click', (e) => {
        if (e.target === popup) popup.classList.remove('active');
    });
});
function buyNow(productId) {
    const sku = document.getElementById('selectedVariantSku').value;
    const quantity = document.getElementById('product-quantity').value;
    const size = document.getElementById('selectedVariantSize').value;

    if (!sku || sku.trim() === "") {
        alert("Vui lòng chọn Kích thước và Màu sắc trước khi mua hàng!");
        return;
    }

    if (parseInt(quantity) < 1) {
        alert("Số lượng phải lớn hơn 0");
        return;
    }

    fetch('cart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: new URLSearchParams({
            'action': 'add',
            'productId': productId,
            'sku': sku,
            'size': size,
            'quantity': quantity
        })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                window.location.href = "thanhtoan.jsp";
            } else {
                alert(data.message || "Có lỗi xảy ra khi thêm vào giỏ hàng. Vui lòng thử lại!");
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("Lỗi kết nối đến server.");
        });
}