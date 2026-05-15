document.addEventListener('DOMContentLoaded', function () {
    var discountMarketingBtn = document.getElementById('discountMarketing');
    var discountModal = document.getElementById('discountMarketingModal');
    var closeDiscountModalBtn = document.getElementById('closeDiscountModalBtn');
    var cancelDiscountModalBtn = document.getElementById('cancelDiscountModalBtn');
    var discountMethodSelect = document.getElementById('discount-method');
    var singleDiscountSection = document.getElementById('single-discount-section');
    var batchDiscountSection = document.getElementById('batch-discount-section');
    var checkProductBtn = document.getElementById('check-product-btn');
    var applyDiscountBtn = document.getElementById('applyDiscountBtn');
    var singleDiscountType = document.getElementById('single-discount-type');
    var batchDiscountType = document.getElementById('batch-discount-type');

    function openModal(modal) {
        if (!modal) return;
        modal.classList.add('active');
        modal.style.display = 'block';
        modal.setAttribute('aria-hidden', 'false');
    }

    function closeModal(modal) {
        if (!modal) return;
        modal.classList.remove('active');
        modal.style.display = 'none';
        modal.setAttribute('aria-hidden', 'true');
    }

    if (discountMarketingBtn) {
        discountMarketingBtn.addEventListener('click', function (e) {
            e.preventDefault();
            openModal(discountModal);
            loadCategoriesForDiscount();
        });
    }

    if (closeDiscountModalBtn) {
        closeDiscountModalBtn.addEventListener('click', function () {
            closeModal(discountModal);
            resetDiscountForm();
        });
    }

    if (cancelDiscountModalBtn) {
        cancelDiscountModalBtn.addEventListener('click', function () {
            closeModal(discountModal);
            resetDiscountForm();
        });
    }

    if (discountMethodSelect) {
        discountMethodSelect.addEventListener('change', function () {
            var method = this.value;
            if (method === 'single') {
                singleDiscountSection.style.display = 'block';
                batchDiscountSection.style.display = 'none';
            } else if (method === 'batch') {
                singleDiscountSection.style.display = 'none';
                batchDiscountSection.style.display = 'block';
            } else {
                singleDiscountSection.style.display = 'none';
                batchDiscountSection.style.display = 'none';
            }
        });
    }

    if (singleDiscountType) {
        singleDiscountType.addEventListener('change', function () {
            var label = document.getElementById('single-discount-label');
            if (this.value === 'percentage') {
                label.textContent = 'Phần trăm giảm (%)';
                document.getElementById('single-discount-value').placeholder = 'VD: 10';
            } else {
                label.textContent = 'Số tiền giảm (VNĐ)';
                document.getElementById('single-discount-value').placeholder = 'VD: 50000';
            }
        });
    }

    if (batchDiscountType) {
        batchDiscountType.addEventListener('change', function () {
            var label = document.getElementById('batch-discount-label');
            if (this.value === 'percentage') {
                label.textContent = 'Phần trăm giảm (%)';
                document.getElementById('batch-discount-value').placeholder = 'VD: 15';
            } else {
                label.textContent = 'Số tiền giảm (VNĐ)';
                document.getElementById('batch-discount-value').placeholder = 'VD: 100000';
            }
        });
    }

    if (checkProductBtn) {
        checkProductBtn.addEventListener('click', function () {
            var sku = document.getElementById('product-code-discount').value.trim();
            if (!sku) {
                alert('Vui lòng nhập SKU sản phẩm');
                return;
            }

            checkProductBtn.disabled = true;
            checkProductBtn.textContent = 'Đang kiểm tra...';

            fetch(CTX + '/admin/discount/get-product?sku=' + encodeURIComponent(sku))
                .then(function (response) {
                    if (!response.ok) {
                        return response.json().then(function (data) {
                            throw new Error(data.error || 'Không thể lấy thông tin sản phẩm');
                        });
                    }
                    return response.json();
                })
                .then(function (data) {
                    if (data.success) {
                        var priceDisplay = document.getElementById('current-price-display');
                        priceDisplay.value = formatPrice(data.currentPrice) + ' VNĐ';
                        
                        if (data.discountedPrice && data.discountedPrice > 0) {
                            priceDisplay.value += ' (Giá giảm hiện tại: ' + formatPrice(data.discountedPrice) + ' VNĐ)';
                        }
                    }
                })
                .catch(function (error) {
                    alert('Lỗi: ' + error.message);
                    document.getElementById('current-price-display').value = '';
                })
                .finally(function () {
                    checkProductBtn.disabled = false;
                    checkProductBtn.textContent = 'Kiểm tra giá';
                });
        });
    }

    if (applyDiscountBtn) {
        applyDiscountBtn.addEventListener('click', function () {
            var method = discountMethodSelect.value;

            if (!method) {
                alert('Vui lòng chọn phương thức giảm giá');
                return;
            }

            if (method === 'single') {
                applySingleDiscount();
            } else if (method === 'batch') {
                applyBatchDiscount();
            }
        });
    }

    function applySingleDiscount() {
        var sku = document.getElementById('product-code-discount').value.trim();
        var discountType = document.getElementById('single-discount-type').value;
        var discountValue = document.getElementById('single-discount-value').value.trim();

        if (!sku) {
            alert('Vui lòng nhập SKU sản phẩm');
            return;
        }

        if (!discountValue || parseFloat(discountValue) <= 0) {
            alert('Vui lòng nhập giá trị giảm hợp lệ');
            return;
        }

        applyDiscountBtn.disabled = true;
        applyDiscountBtn.textContent = 'Đang áp dụng...';

        var params = new URLSearchParams();
        params.append('sku', sku);
        params.append('discountType', discountType);
        params.append('discountValue', discountValue);

        fetch(CTX + '/admin/discount/apply-single', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params
        })
            .then(function (response) {
                if (!response.ok) {
                    return response.json().then(function (data) {
                        throw new Error(data.error || 'Không thể áp dụng giảm giá');
                    });
                }
                return response.json();
            })
            .then(function (data) {
                if (data.success) {
                    alert(data.message || 'Áp dụng giảm giá thành công!');
                    closeModal(discountModal);
                    resetDiscountForm();
                    if (typeof loadProducts === 'function') {
                        loadProducts();
                    }
                }
            })
            .catch(function (error) {
                alert('Lỗi: ' + error.message);
            })
            .finally(function () {
                applyDiscountBtn.disabled = false;
                applyDiscountBtn.textContent = 'Lưu giảm giá';
            });
    }

    function applyBatchDiscount() {
        var discountType = document.getElementById('batch-discount-type').value;
        var discountValue = document.getElementById('batch-discount-value').value.trim();
        
        var checkboxes = document.querySelectorAll('#category-checkbox-list input[type="checkbox"]:checked');
        var categoryIds = Array.prototype.map.call(checkboxes, function (cb) {
            return cb.value;
        });

        if (categoryIds.length === 0) {
            alert('Vui lòng chọn ít nhất một danh mục');
            return;
        }

        if (!discountValue || parseFloat(discountValue) <= 0) {
            alert('Vui lòng nhập giá trị giảm hợp lệ');
            return;
        }

        if (!confirm('Bạn có chắc muốn áp dụng giảm giá cho ' + categoryIds.length + ' danh mục?')) {
            return;
        }

        applyDiscountBtn.disabled = true;
        applyDiscountBtn.textContent = 'Đang áp dụng...';

        var params = new URLSearchParams();
        categoryIds.forEach(function (id) {
            params.append('categoryIds', id);
        });
        params.append('discountType', discountType);
        params.append('discountValue', discountValue);

        fetch(CTX + '/admin/discount/apply-batch', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params
        })
            .then(function (response) {
                if (!response.ok) {
                    return response.json().then(function (data) {
                        throw new Error(data.error || 'Không thể áp dụng giảm giá');
                    });
                }
                return response.json();
            })
            .then(function (data) {
                if (data.success) {
                    alert(data.message || 'Áp dụng giảm giá thành công!');
                    closeModal(discountModal);
                    resetDiscountForm();
                    if (typeof loadProducts === 'function') {
                        loadProducts();
                    }
                }
            })
            .catch(function (error) {
                alert('Lỗi: ' + error.message);
            })
            .finally(function () {
                applyDiscountBtn.disabled = false;
                applyDiscountBtn.textContent = 'Lưu giảm giá';
            });
    }

    function loadCategoriesForDiscount() {
        fetch(CTX + '/admin/category/list')
            .then(function (response) {
                if (!response.ok) throw new Error('HTTP ' + response.status);
                return response.json();
            })
            .then(function (categories) {
                var container = document.getElementById('category-checkbox-list');
                if (!container) return;

                container.innerHTML = '';

                if (!categories || categories.length === 0) {
                    container.innerHTML = '<p style="color:#999;">Chưa có danh mục nào</p>';
                    return;
                }

                categories.forEach(function (cat) {
                    var label = document.createElement('label');
                    label.style.display = 'block';
                    label.style.marginBottom = '8px';
                    label.style.cursor = 'pointer';

                    var checkbox = document.createElement('input');
                    checkbox.type = 'checkbox';
                    checkbox.value = cat.id;
                    checkbox.style.marginRight = '8px';

                    var text = document.createTextNode(cat.nameCategory + ' (' + (cat.productCount || 0) + ' SP)');

                    label.appendChild(checkbox);
                    label.appendChild(text);
                    container.appendChild(label);
                });
            })
            .catch(function (error) {
                console.error('Error loading categories:', error);
            });
    }

    function resetDiscountForm() {
        if (discountMethodSelect) discountMethodSelect.value = '';
        if (singleDiscountSection) singleDiscountSection.style.display = 'none';
        if (batchDiscountSection) batchDiscountSection.style.display = 'none';
        
        document.getElementById('product-code-discount').value = '';
        document.getElementById('current-price-display').value = '';
        document.getElementById('single-discount-value').value = '';
        document.getElementById('batch-discount-value').value = '';
        
        var checkboxes = document.querySelectorAll('#category-checkbox-list input[type="checkbox"]');
        Array.prototype.forEach.call(checkboxes, function (cb) {
            cb.checked = false;
        });
    }

    function formatPrice(price) {
        if (!price) return '0';
        return Math.round(price).toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    }
});
