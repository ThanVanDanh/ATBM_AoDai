document.addEventListener("DOMContentLoaded", () => {
    const modal = document.getElementById('customer-modal');
    if (modal) {
        const customerViewBtn = document.querySelectorAll('.customer-table .btn-view');
        const customerClose = modal.querySelector('.close-modal');
        customerViewBtn.forEach(nut => {
            nut.addEventListener('click', () => {
                document.getElementById('modal-fullName').textContent = nut.getAttribute('data-fullname') || '';
                document.getElementById('modal-email').textContent = nut.getAttribute('data-email') || '';
                document.getElementById('modal-phone').textContent = nut.getAttribute('data-phone') || '';
                document.getElementById('modal-address').textContent = nut.getAttribute('data-address') || '';
                document.getElementById('modal-createdAt').textContent = nut.getAttribute('data-createdat') || '';
                const status = nut.getAttribute('data-status');
                let statusText = '';
                if (status === 'active') statusText = 'Hoạt động';
                else if (status === 'banned') statusText = 'Bị khóa';
                else statusText = 'Chưa kích hoạt';
                document.getElementById('modal-status').textContent = statusText;

                const historyList = document.getElementById('order-history-list');
                historyList.innerHTML = '<li style="text-align:center;">Đang tải...</li>';

                const userId = nut.getAttribute('data-id');
                if (userId) {
                    fetch('get-customer-orders?userId=' + userId)
                        .then(res => res.json())
                        .then(orders => {
                            historyList.innerHTML = '';
                            if (!orders || orders.length === 0) {
                                historyList.innerHTML = '<li>Khách hàng chưa có đơn hàng nào.</li>';
                                return;
                            }
                            orders.forEach(o => {
                                const li = document.createElement('li');
                                li.classList.add('order-history-item');
                                let statusClass = 'status-processing';
                                let statusText = 'Đang xử lý';
                                if (o.orderStatus === 'hoàn thành') {
                                    statusClass = 'status-complete';
                                    statusText = 'Hoàn thành';
                                } else if (o.orderStatus === 'đã hủy') {
                                    statusClass = 'status-cancel';
                                    statusText = 'Đã hủy';
                                } else if (o.orderStatus === 'đang giao') {
                                    statusClass = 'status-shipping';
                                    statusText = 'Đang giao';
                                }

                                const total = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(o.totalAmount);
                                const dateDisplay = o.created_at || o.createdAt;

                                let html = `
                                    <div class="timeline-date">${dateDisplay}</div>
                                    <div class="timeline-content">
                                        <div class="timeline-header">
                                            <span class="order-code">#${o.orderCode || 'ORD...'}</span>
                                            <span class="${statusClass}">${statusText}</span>
                                        </div>
                                        <div class="timeline-body">
                                            <div class="order-total">${total}</div>
                                        </div>
                                `;

                                if (o.orderStatus === 'đã hủy' && o.cancelReason) {
                                    html += `<span class="cancel-reason">Lý do: ${o.cancelReason}</span>`;
                                }

                                html += `</div>`;
                                li.innerHTML = html;
                                historyList.appendChild(li);
                            });
                        })
                        .catch(err => {
                            console.error(err);
                            historyList.innerHTML = '<li style="color:red;">Lỗi tải lịch sử đơn hàng.</li>';
                        });
                }

                modal.style.display = 'flex';
            });
        });
        customerClose.addEventListener('click', () => {
            modal.style.display = 'none';
        });
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.style.display = 'none';
            }
        });
    }

    const order = document.getElementById('order-modal');
    const orderViewBtn = document.querySelectorAll('.order-table .btn-view');
    if (order) {
        const orderClose = order.querySelector('.close-modal');
        orderViewBtn.forEach(nut => {
            nut.addEventListener('click', () => {
                order.style.display = 'flex';
            });
        });
        orderClose.addEventListener('click', () => {
            order.style.display = 'none';
        });
        order.addEventListener('click', (e) => {
            if (e.target === order) {
                order.style.display = 'none';
            }
        });
    }

    const tabLinks = document.querySelectorAll(".tab-link");
    if (tabLinks) {
        const tabContents = document.querySelectorAll(".tab-content");
        tabLinks.forEach(link => {
            link.addEventListener("click", () => {
                tabLinks.forEach(btn => btn.classList.remove("active"));
                tabContents.forEach(tab => tab.classList.remove("active"));
                link.classList.add("active");
                document.getElementById(link.dataset.tab).classList.add("active");
            });
        });
    }

    const deleteModal = document.getElementById('delete-confirm-modal');
    if (deleteModal) {
        const deleteModalBtn = document.querySelectorAll('.customer-table .btn-delete');
        const deleteClose = deleteModal.querySelector('.close-modal');
        const cancelDeleteBtn = document.getElementById('btn-cancel-delete');
        const confirmDeleteBtn = document.getElementById('btn-confirm-delete');
        let rowToDelete = null;

        deleteModalBtn.forEach(button => {
            button.addEventListener('click', function () {
                rowToDelete = this.closest('tr');
                const userName = this.getAttribute('data-name');
                document.getElementById('customer-name-to-delete').textContent = userName;
                deleteModal.style.display = 'flex';
            });
        });

        deleteClose.addEventListener('click', () => {
            deleteModal.style.display = 'none';
        });

        cancelDeleteBtn.addEventListener('click', () => {
            deleteModal.style.display = 'none';
        });

        confirmDeleteBtn.addEventListener('click', () => {
            if (rowToDelete) {
                const btn = rowToDelete.querySelector('.btn-delete');
                const userId = btn.getAttribute('data-id');

                fetch('delete-user', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `id=${userId}`
                })
                    .then(response => {
                        if (response.ok) {
                            rowToDelete.style.transition = "opacity 0.5s";
                            rowToDelete.style.opacity = "0";
                            setTimeout(() => {
                                rowToDelete.remove();
                                const tbody = document.querySelector('.table-general tbody');
                                checkEmptyTable(tbody);
                                deleteModal.style.display = 'none';
                                rowToDelete = null;
                            }, 100);
                        } else {
                            alert("Không thể xóa khách hàng này (có thể do dính líu đến đơn hàng cũ).");
                            deleteModal.style.display = 'none';
                        }
                    })
                    .catch(err => {
                        console.error(err);
                        alert("Lỗi kết nối server.");
                    });
            }
        });
    }
    function checkEmptyTable(tbody) {
        const emptyRow = tbody.querySelector('.empty-state-row');
        const allRows = tbody.querySelectorAll('tr:not(#empty-state-row)');
        const pagination = document.querySelector('.pagination');

        let visibleRowCount = 0;
        allRows.forEach(row => {
            if (row.style.display !== 'none') {
                visibleRowCount++;
            }
        });

        if (visibleRowCount === 0) {
            if (emptyRow) emptyRow.style.display = 'table-row';
            if (pagination) pagination.style.display = 'none';
        } else {
            if (emptyRow) emptyRow.style.display = 'none';
            if (pagination) pagination.style.display = 'flex';
        }
    }

    const statusModal = document.getElementById('status-confirm-modal');
    if (statusModal) {
        const actionButtons = document.querySelectorAll('.btn-block, .btn-unlock');
        const confirmBtn = document.getElementById('btn-confirm-status');
        const cancelBtn = document.getElementById('btn-cancel-status');
        const closeModal = statusModal.querySelector('.close-modal');

        const modalTitle = document.getElementById('modal-status-title');
        const modalActionText = document.getElementById('modal-action-text');
        const modalCusName = document.getElementById('modal-customer-name');

        let targetRow = null;
        let targetId = null;
        let targetAction = null;

        actionButtons.forEach(btn => {
            btn.addEventListener('click', function () {
                targetRow = this.closest('tr');
                targetId = this.getAttribute('data-id');
                const name = this.getAttribute('data-name');

                if (this.classList.contains('btn-block')) {
                    targetAction = 'block';
                    modalTitle.textContent = "Xác nhận Khóa tài khoản";
                    modalActionText.textContent = "KHÓA";
                } else {
                    targetAction = 'unlock';
                    modalTitle.textContent = "Xác nhận Mở khóa";
                    modalActionText.textContent = "MỞ KHÓA";
                }

                modalCusName.textContent = name;
                statusModal.style.display = 'flex';
            });
        });

        confirmBtn.addEventListener('click', () => {
            if (targetId && targetAction) {
                fetch('block-user', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `id=${targetId}&action=${targetAction}`
                })
                    .then(response => {
                        if (response.ok) {
                            const newStatus = (targetAction === 'block') ? 'blocked' : 'active';
                            toggleLockStatus(targetRow, newStatus);

                            statusModal.style.display = 'none';
                        } else {
                            alert("Có lỗi xảy ra, vui lòng thử lại!");
                        }
                    })
                    .catch(err => {
                        console.error(err);
                        alert("Lỗi kết nối server.");
                    });
            }
        });

        const closeFunc = () => { statusModal.style.display = 'none'; };
        closeModal.addEventListener('click', closeFunc);
        cancelBtn.addEventListener('click', closeFunc);
        window.addEventListener('click', (e) => {
            if (e.target === statusModal) closeFunc();
        });

        function toggleLockStatus(row, status) {
            const badgeActive = row.querySelector('.status-active');
            const badgeBlocked = row.querySelector('.status-blocked');
            const btnBlock = row.querySelector('.btn-block');
            const btnUnlock = row.querySelector('.btn-unlock');

            if (status === 'blocked') {
                if (badgeActive) badgeActive.style.display = 'none';
                if (badgeBlocked) badgeBlocked.style.display = 'inline-block';

                if (btnBlock) btnBlock.style.display = 'none';
                if (btnUnlock) btnUnlock.style.display = 'inline-block';
            } else {
                if (badgeActive) badgeActive.style.display = 'inline-block';
                if (badgeBlocked) badgeBlocked.style.display = 'none';

                if (btnBlock) btnBlock.style.display = 'inline-block';
                if (btnUnlock) btnUnlock.style.display = 'none';
            }
        }
    }
    const navItems = document.querySelectorAll('.sidebar-nav .nav-item');
    const currentPath = window.location.pathname;

    navItems.forEach(item => {
        const link = item.querySelector('a');
        const href = link.getAttribute('href');

        item.addEventListener('click', function () {
            navItems.forEach(i => i.classList.remove('active'));
            this.classList.add('active');
        });

        if (currentPath.includes(href) && href !== "#") {
            navItems.forEach(i => i.classList.remove('active'));
            item.classList.add('active');
        }
    });

});