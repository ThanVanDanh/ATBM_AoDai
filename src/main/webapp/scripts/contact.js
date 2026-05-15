document.addEventListener('DOMContentLoaded', () => {
    const viewModal = document.getElementById('contact-modal');
    const deleteModal = document.getElementById('delete-modal');
    const replyModal = document.getElementById('reply-modal');

    const viewButtons = document.querySelectorAll('.btn-view-detail');
    viewButtons.forEach(button => {
        button.addEventListener('click', function() {
            const id = this.getAttribute('data-id');
            const name = this.getAttribute('data-name');
            const email = this.getAttribute('data-email');
            const date = this.getAttribute('data-date');
            const message = this.getAttribute('data-message');

            document.getElementById('modal-id').textContent = id;
            document.getElementById('modal-name').textContent = name;
            document.getElementById('modal-email').textContent = email;
            document.getElementById('modal-date').textContent = date;
            document.getElementById('modal-message-full').textContent = message;

            viewModal.style.display = 'block';
        });
    });
    const deleteButtons = document.querySelectorAll('.delete');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            const id = this.getAttribute('data-id');

            const row = this.closest('tr');
            const name = row.querySelector('td:nth-child(2)').textContent;

            const displayId = document.getElementById('delete-id-display');
            if (displayId) displayId.textContent = id;

            const displayName = document.getElementById('delete-name');
            if (displayName) displayName.textContent = name;

            const inputHiddenId = document.getElementById('input-delete-id');
            if (inputHiddenId) {
                inputHiddenId.value = id;
            } else {
                console.error("Lỗi: Không tìm thấy thẻ input có id='input-delete-id' trong Modal Xóa!");
            }

            deleteModal.style.display = 'block';
        });
    });

    const replyButtons = document.querySelectorAll('.btn-reply-email');
    replyButtons.forEach(button => {
        button.addEventListener('click', function() {
            let email = this.getAttribute('data-recipient-email');

            if (!email) {
                const row = this.closest('tr');
                email = row.querySelector('td:nth-child(3)').textContent;
            }

            document.getElementById('reply-to').value = email;

            replyModal.style.display = 'block';
        });
    });


    function closeAllModals() {
        viewModal.style.display = 'none';
        deleteModal.style.display = 'none';
        replyModal.style.display = 'none';
    }

    document.querySelectorAll('.close-button').forEach(btn => {
        btn.onclick = closeAllModals;
    });

    const cancelDeleteBtn = document.getElementById('cancel-delete');
    if (cancelDeleteBtn) cancelDeleteBtn.onclick = closeAllModals;

    const cancelReplyBtn = document.getElementById('cancel-reply');
    if (cancelReplyBtn) cancelReplyBtn.onclick = closeAllModals;

    window.onclick = function(event) {
        if (event.target == viewModal || event.target == deleteModal || event.target == replyModal) {
            closeAllModals();
        }
    }

});
document.addEventListener("DOMContentLoaded", function() {
    const replyButtons = document.querySelectorAll(".btn-reply-email");
    const replyModal = document.getElementById("reply-modal");
    const replyEmailInput = document.getElementById("reply-to");
    const replyIdInput = document.getElementById("reply-id");

    replyButtons.forEach(btn => {
        btn.addEventListener("click", function() {
            const email = this.getAttribute("data-recipient-email");
            const id = this.getAttribute("data-id");

            replyEmailInput.value = email;
            replyIdInput.value = id;
X
            replyModal.style.display = "block";
            replyModal.classList.add("active");
        });
    });
});

