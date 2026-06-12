document.addEventListener('DOMContentLoaded', function () {
    const accountInfo = document.getElementById('nav-details');
    const address = document.getElementById('nav-addresses');
    const contentInfo = document.getElementById('account-details');
    const contentAddress = document.getElementById('account-addresses');
    const tabKey = document.getElementById('nav-key');
    const contentKey = document.getElementById('account-key');

    if (accountInfo && address && contentInfo && contentAddress) {
        accountInfo.addEventListener('click', e => {
            contentInfo.style.display = 'block';
            contentAddress.style.display = 'none';
            if (contentKey) contentKey.style.display = 'none';
            accountInfo.classList.add('active');
            address.classList.remove('active');
            if (tabKey) tabKey.classList.remove('active');
        });

        address.addEventListener('click', e => {
            contentInfo.style.display = 'none';
            contentAddress.style.display = 'block';
            if (contentKey) contentKey.style.display = 'none';
            accountInfo.classList.remove('active');
            address.classList.add('active');
            if (tabKey) tabKey.classList.remove('active');
        });

        if (tabKey && contentKey) {
            tabKey.addEventListener('click', e => {
                e.preventDefault();
                contentInfo.style.display = 'none';
                contentAddress.style.display = 'none';
                contentKey.style.display = 'block';

                accountInfo.classList.remove('active');
                address.classList.remove('active');
                tabKey.classList.add('active');
            });
        }
    }

    const addModal = document.getElementById('add-address-modal');
    const editModal = document.getElementById('edit-address-modal');
    const addBtn = document.getElementById('add-address-btn');
    const editBtns = document.querySelectorAll('.edit-address-link');

    if (addBtn && addModal) {
        addBtn.addEventListener('click', () => {
            addModal.style.display = 'flex';
        });
    }

    editBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();

            const id = btn.getAttribute('data-id');
            const fullName = btn.getAttribute('data-name');
            const phone = btn.getAttribute('data-phone');
            const addr = btn.getAttribute('data-addr');
            const city = btn.getAttribute('data-city');
            const isDefault = btn.getAttribute('data-default') === 'true';

            let lastSpaceIndex = fullName.lastIndexOf(" ");
            let ho = "";
            let ten = fullName;
            if (lastSpaceIndex !== -1) {
                ho = fullName.substring(0, lastSpaceIndex);
                ten = fullName.substring(lastSpaceIndex + 1);
            }

            document.getElementById('edit-id').value = id;
            document.getElementById('edit-ho').value = ho;
            document.getElementById('edit-ten').value = ten;
            document.getElementById('edit-sdt').value = phone;
            document.getElementById('edit-diachi').value = addr;
            document.getElementById('edit-default').checked = isDefault;

            const citySelect = document.getElementById('edit-tinhthanh');
            if (citySelect) citySelect.value = city;

            editModal.style.display = 'flex';
        });
    });
    const deleteModal = document.getElementById('delete-address-modal');
    const deleteBtns = document.querySelectorAll('.delete-address-link');
    const confirmDeleteBtn = document.getElementById('confirm-delete-btn');

    deleteBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();

            const id = btn.getAttribute('data-id');

            confirmDeleteBtn.href = `delete-address?id=${id}`;

            if (deleteModal) {
                deleteModal.style.display = 'flex';
            }
        });
    });
    document.querySelectorAll('.modal-close').forEach(btn => {
        btn.addEventListener('click', () => {
            const overlay = btn.closest('.modal-overlay');
            if (overlay) {
                overlay.style.display = 'none';
            }
        });
    });

    window.addEventListener('click', (e) => {
        if (e.target === addModal) addModal.style.display = 'none';
        if (e.target === editModal) editModal.style.display = 'none';
        if (deleteModal && e.target === deleteModal) deleteModal.style.display = 'none';
    });
});

window.cancelOrder = function (orderId) {
    document.getElementById('cancel-order-id').value = orderId;
    document.getElementById('cancel-order-modal').style.display = 'flex';

    const firstRadio = document.querySelector('input[name="cancel-reason"]');
    if (firstRadio) firstRadio.checked = true;
    document.getElementById('other-reason-container').style.display = 'none';
    document.getElementById('other-reason-text').value = '';
};

document.addEventListener('DOMContentLoaded', function () {
    const cancelModal = document.getElementById('cancel-order-modal');
    if (!cancelModal) return;

    const closeModalBtn = document.getElementById('cancel-modal-close-btn');
    const closeBtn = cancelModal.querySelector('.modal-close');
    const confirmBtn = document.getElementById('confirm-cancel-btn');
    const otherContainer = document.getElementById('other-reason-container');
    const otherText = document.getElementById('other-reason-text');

    document.querySelectorAll('input[name="cancel-reason"]').forEach(radio => {
        radio.addEventListener('change', function () {
            if (this.value === 'other') {
                otherContainer.style.display = 'block';
            } else {
                otherContainer.style.display = 'none';
            }
        });
    });

    function closeCancelModal() {
        cancelModal.style.display = 'none';
    }

    if (closeModalBtn) closeModalBtn.addEventListener('click', closeCancelModal);
    if (closeBtn) closeBtn.addEventListener('click', closeCancelModal);

    cancelModal.addEventListener('click', function (e) {
        if (e.target === cancelModal) closeCancelModal();
    });

    if (confirmBtn) {
        confirmBtn.addEventListener('click', function () {
            const orderId = document.getElementById('cancel-order-id').value;
            const selectedReason = document.querySelector('input[name="cancel-reason"]:checked');

            if (!selectedReason) {
                alert('Vui lòng chọn lý do hủy đơn!');
                return;
            }

            let cancelReason = selectedReason.value;

            if (cancelReason === 'other') {
                cancelReason = otherText.value.trim();
                if (!cancelReason) {
                    alert('Vui lòng nhập lý do hủy đơn!');
                    return;
                }
            }

            fetch('cancel-order', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'orderId=' + orderId + '&cancelReason=' + encodeURIComponent(cancelReason)
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('Đã hủy đơn hàng thành công!');
                        closeCancelModal();
                        location.reload();
                    } else {
                        alert('Lỗi: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Đã xảy ra lỗi kết nối. Vui lòng thử lại.');
                });
        });
    }
});

window.viewOrderDetails = function (orderId) {
    const modal = document.getElementById('order-details-modal');
    const closeBtn = document.getElementById('close-order-details');

    document.getElementById('modal-order-items-body').innerHTML = '<tr><td colspan="4" style="text-align:center;">Đang tải...</td></tr>';

    fetch('order-details?id=' + orderId)
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                const order = data.order;
                const items = data.items;

                document.getElementById('modal-order-code').textContent = order.orderCode;
                document.getElementById('modal-order-date').textContent = order.formattedCreatedAt;
                document.getElementById('modal-order-status').textContent = order.orderStatus;
                document.getElementById('modal-order-address').textContent = order.shippingAddress;

                const fmt = new Intl.NumberFormat('vi-VN', {style: 'currency', currency: 'VND'});

                document.getElementById('modal-subtotal').textContent = fmt.format(order.subtotalAmount);
                document.getElementById('modal-shipping').textContent = fmt.format(order.shippingFee);
                document.getElementById('modal-discount').textContent = fmt.format(order.discountAmount);
                document.getElementById('modal-total').textContent = fmt.format(order.totalAmount);

                const tbody = document.getElementById('modal-order-items-body');
                tbody.innerHTML = '';

                items.forEach(item => {
                    const productName = item.productName || '<span style="color: #999; font-style: italic;">Sản phẩm không còn tồn tại</span>';
                    const size = item.size || 'N/A';
                    const productImage = item.productImage || 'image/no-image.png';

                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                    <td style="display: flex; align-items: center; gap: 10px;">
                        <img src="${productImage}" alt="Product" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px; border: 1px solid #eee;">
                        <div>
                            <p style="margin: 0; font-weight: 600; color: #333;">${productName}</p>
                            <span style="font-size: 0.85em; color: #777;">Size: ${size}</span>
                        </div>
                    </td>
                    <td>${fmt.format(item.priceAtPurchase)}</td>
                    <td>x${item.quantity}</td>
                    <td style="font-weight: 600;">${fmt.format(item.priceAtPurchase * item.quantity)}</td>
                `;
                    tbody.appendChild(tr);
                });

                modal.style.display = 'flex';
            } else {
                alert(data.message);
            }
        })
        .catch(err => {
            console.error(err);
            alert('Lỗi tải dữ liệu chi tiết.');
        });

    closeBtn.onclick = function () {
        modal.style.display = 'none';
    }
    window.onclick = function (event) {
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    }
}