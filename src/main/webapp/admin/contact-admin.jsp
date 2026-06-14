<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Quản lý Liên hệ | Việt Sắc Đỏ</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css" integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/contact-admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/alert.css">
</head>
<body>
<div class="admin-container">
    <jsp:include page="sidebar.jsp" />

    <c:if test="${not empty sessionScope.message}">
        <div id="alert-message" class="alert-toast ${sessionScope.messageType == 'success' ? 'alert-toast-success' : 'alert-toast-danger'}">
            <i class="fas ${sessionScope.messageType == 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}" style="margin-right: 10px;"></i>
            <span>${sessionScope.message}</span>
            <span id="close-alert" onclick="this.parentElement.remove()" style="margin-left: auto; cursor: pointer; font-weight: bold; padding-left: 15px; pointer-events: auto;">&times;</span>
        </div>
        <% session.removeAttribute("message"); %>
    </c:if>

    <main class="main-content">
        <header class="admin-header">
            <div class="header-actions">
                <a href="../login.jsp" class="btn-logout"><i class="fas fa-user-circle"></i> Đăng xuất</a>
            </div>
        </header>
        <div class="dashboard-recent-activity">
            <div class="recent-panel" style="grid-column: 1 / -1;">
                <h1>Quản lý Liên hệ</h1>

                <table class="recent-table">
                    <thead>
                    <tr>
                        <th>#ID</th>
                        <th>Tên Khách hàng</th>
                        <th>Email</th>
                        <th>Ngày gửi</th>
                        <th>Trích đoạn</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="c" items="${contactList}">
                        <tr>
                            <td>#${c.id}</td>
                            <td>${c.fullName}</td>
                            <td>${c.email}</td>
                            <td>${c.formattedDate}</td>
                            <td class="excerpt-column">
                                <div style="max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                        ${c.messageBody}
                                </div>
                            </td>

                            <td>
                                <span class="status-badge ${c.statusMessage == 'new' ? 'status-new' : ''}">
                                        ${c.statusMessage != null ? c.statusMessage : 'Mới'}
                                </span>
                            </td>

                            <td>
                                <button class="btn btn-sm btn-reply-email"
                                        title="Phản hồi Email"
                                        data-recipient-email="${c.email}"
                                        data-id="${c.id}">
                                    <i class="fas fa-reply"></i>
                                </button>

                                <button class="btn btn-sm btn-view-detail"
                                        data-id="${c.id}"
                                        data-name="${c.fullName}"
                                        data-email="${c.email}"
                                        data-date="${c.formattedDate}"
                                        data-message="${c.messageBody}">
                                    <i class="fas fa-eye"></i>
                                </button>

                                <button class="btn btn-sm delete" title="Xóa" data-id="${c.id}">
                                    <i class="fas fa-trash-alt"></i>
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<div id="contact-modal" class="modal">
    <div class="modal-content">
        <span class="close-button">&times;</span>
        <h3>Chi tiết Tin nhắn Liên hệ</h3>
        <p><strong>ID:</strong> <span id="modal-id"></span></p>
        <p><strong>Tên Khách hàng:</strong> <span id="modal-name"></span></p>
        <p><strong>Email:</strong> <span id="modal-email"></span></p>
        <p><strong>Ngày gửi:</strong> <span id="modal-date"></span></p>
        <hr>
        <div class="message-body">
            <h4>Nội dung đầy đủ:</h4>
            <p id="modal-message-full"></p>
        </div>
    </div>
</div>

<div id="delete-modal" class="modal delete-modal">
    <div class="modal-content">
        <span class="close-button">&times;</span>
        <h3>Xác nhận Xóa Tin nhắn</h3>
        <p>Bạn có chắc muốn xóa tin nhắn của: <strong id="delete-name"></strong>?</p>
        <p>ID tin nhắn: <strong id="delete-id-display"></strong></p>

        <form action="${pageContext.request.contextPath}/delete_message" method="post">
            <input type="hidden" name="id" id="input-delete-id">

            <div class="modal-actions">
                <button type="button" class="btn btn-sm status-complete" id="cancel-delete">Hủy</button>
                <button type="submit" class="btn btn-sm status-cancel">Xác nhận Xóa</button>
            </div>
        </form>
    </div>
</div>

<div id="reply-modal" class="modal reply-modal">
    <div class="modal-content">
        <form id="reply-form" action="${pageContext.request.contextPath}/admin/contact-reply" method="post">
            <input type="hidden" name="id" id="reply-id">

            <div class="form-group">
                <label>Đến:</label>
                <input type="text" name="email" id="reply-to" class="form-control" readonly>
            </div>

            <input type="hidden" name="subject" value="Re: Hỗ trợ Việt Sắc Đỏ">

            <div class="form-group">
                <label>Nội dung:</label>
                <textarea name="content" id="reply-body" class="form-control" required rows="5" placeholder="Nhập nội dung phản hồi..."></textarea>
            </div>

            <div class="modal-actions">
                <button type="button" class="btn btn-sm status-cancel" id="cancel-reply">Hủy</button>
                <button type="submit" class="btn btn-sm status-shipping">Gửi Email</button>
            </div>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/scripts/contact.js"></script>
<script src="${pageContext.request.contextPath}/scripts/admin/admin.js"></script>

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const replyButtons = document.querySelectorAll(".btn-reply-email");
        const replyModal = document.getElementById("reply-modal");
        const replyEmailInput = document.getElementById("reply-to");
        const replyIdInput = document.getElementById("reply-id");
        const cancelReplyBtn = document.getElementById("cancel-reply");

        replyButtons.forEach(btn => {
            btn.addEventListener("click", function() {
                const email = this.getAttribute("data-recipient-email");
                const id = this.getAttribute("data-id");

                replyEmailInput.value = email;
                replyIdInput.value = id;

                if(replyModal) {
                    replyModal.style.display = "block";
                    replyModal.classList.add("active");
                }
            });
        });

        if(cancelReplyBtn && replyModal) {
            cancelReplyBtn.addEventListener("click", function() {
                replyModal.style.display = "none";
                replyModal.classList.remove("active");
            });
        }

        window.addEventListener("click", function(event) {
            if (event.target == replyModal) {
                replyModal.style.display = "none";
                replyModal.classList.remove("active");
            }
        });
    });
</script>

</body>
</html>