document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('customer-modal');
    const closeBtn = document.querySelector('.close-modal');
    const viewButtons = document.querySelectorAll('.btn-view');

    const tabLinks = document.querySelectorAll('.tab-link');
    const tabContents = document.querySelectorAll('.tab-content');

    function openModal(userData) {
        document.getElementById('modal-fullName').textContent = userData.fullName;
        document.getElementById('modal-email').textContent = userData.email;
        document.getElementById('modal-phone').textContent = userData.phone;
        document.getElementById('modal-address').textContent = userData.address;
        document.getElementById('modal-createdAt').textContent = userData.createdAt;

        const statusSpan = document.getElementById('modal-status');
        statusSpan.textContent = userData.status === 'active' ? 'Hoạt động' :
            (userData.status === 'banned' ? 'Bị khóa' : 'Chưa kích hoạt');
        statusSpan.className = '';
        statusSpan.classList.add('status-badge');
        statusSpan.classList.add(userData.status === 'active' ? 'status-active' :
            (userData.status === 'banned' ? 'status-blocked' : 'status-inactive'));

        document.querySelector('.tab-link[data-tab="tab-info"]').click();

        const historyList = document.getElementById('order-history-list');
        historyList.innerHTML = '<li style="text-align:center; padding: 20px;">Đang tải...</li>';

        if (userData.id) {
            fetch(APP_CTX + '/admin/get-customer-orders?userId=' + userData.id)
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => { throw new Error(text || 'Server returned ' + response.status) });
                    }
                    return response.json();
                })
                .then(orders => {
                    historyList.innerHTML = ''; // Clear loading
                    if (orders.length === 0) {
                        historyList.innerHTML = '<li style="text-align:center; padding: 20px; color:#777;">Chưa có đơn hàng nào.</li>';
                    } else {
                        orders.forEach(o => {
                            const li = document.createElement('li');
                            li.classList.add('order-history-item');
                            let statusClass = 'badge-status pending';
                            let statusText = o.orderStatus;

                            if (o.orderStatus.toLowerCase() === 'hoàn thành') { statusClass = 'badge-status complete'; }
                            else if (o.orderStatus.toLowerCase().includes('hủy')) { statusClass = 'badge-status cancel'; }
                            else if (o.orderStatus.toLowerCase().includes('giao')) { statusClass = 'badge-status shipping'; }

                            const total = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(o.totalAmount);

                            let dateDisplay = o.created_at || o.createdAt || o.formattedCreatedAt;

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

                            if (o.orderStatus.toLowerCase().includes('hủy') && o.cancelReason) {
                                html += `<span class="cancel-reason">Lý do: ${o.cancelReason}</span>`;
                            }

                            html += `</div>`;
                            li.innerHTML = html;
                            historyList.appendChild(li);
                        });
                    }
                })
                .catch(error => {
                    console.error('Error fetching orders:', error);
                    historyList.innerHTML = '<li style="text-align:center; color:red; padding:20px;">Lỗi tải lịch sử đơn hàng.<br><small>' + error.message + '</small></li>';
                });
        }

        modal.style.display = 'flex';
    }

    viewButtons.forEach(btn => {
        btn.addEventListener('click', function () {
            const userData = {
                id: this.dataset.id,
                fullName: this.dataset.fullname,
                email: this.dataset.email,
                phone: this.dataset.phone,
                address: this.dataset.address,
                createdAt: this.dataset.createdat,
                status: this.dataset.status
            };
            openModal(userData);
        });
    });

    closeBtn.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.style.display = 'none';
        }
    });

    tabLinks.forEach(link => {
        link.addEventListener('click', function () {
            tabLinks.forEach(l => l.classList.remove('active'));
            tabContents.forEach(c => c.classList.remove('active'));

            this.classList.add('active');
            const tabId = this.dataset.tab;
            document.getElementById(tabId).classList.add('active');
        });
    });

});
