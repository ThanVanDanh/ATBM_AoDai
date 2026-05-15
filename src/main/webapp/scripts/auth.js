import { initializeApp } from "https://www.gstatic.com/firebasejs/10.7.1/firebase-app.js";
import { getAuth, GoogleAuthProvider, FacebookAuthProvider, signInWithPopup } from "https://www.gstatic.com/firebasejs/10.7.1/firebase-auth.js";

const firebaseConfig = {
    apiKey: "AIzaSyBcuuZMwTkWjkFTGlVlB38cLtOW_FlWxVQ",
    authDomain: "vietsacdo-ck.firebaseapp.com",
    projectId: "vietsacdo-ck",
    storageBucket: "vietsacdo-ck.firebasestorage.app",
    messagingSenderId: "57602782048",
    appId: "1:57602782048:web:7df607358f7769c7c5e405",
    measurementId: "G-LTWM6DGKHZ"
};
const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const provider = new GoogleAuthProvider();
const facebookProvider = new FacebookAuthProvider();

document.addEventListener('DOMContentLoaded', function () {
    const googleBtn = document.querySelector('.btn-google');
    if (googleBtn) {
        const btnWrapper = googleBtn.closest('button');

        if (btnWrapper) {
            btnWrapper.addEventListener('click', (e) => {
                e.preventDefault();
                signInWithPopup(auth, provider)
                    .then((result) => {
                        const user = result.user;
                        doLoginSocial(user.email, user.displayName, user.uid, 'google');
                    })
                    .catch((error) => {
                        console.error("Lỗi Google Login:", error);
                        alert("Đăng nhập thất bại: " + error.message);
                    });
            });
        }
    }

    const facebookBtn = document.getElementById('btn-facebook');
    if (facebookBtn) {
        facebookBtn.addEventListener('click', (e) => {
            e.preventDefault();
            signInWithPopup(auth, facebookProvider)
                .then((result) => {
                    const user = result.user;
                    doLoginSocial(user.email, user.displayName, user.uid, 'facebook');
                })
                .catch((error) => {
                    console.error("Lỗi Facebook Login:", error);
                    if (error.code === 'auth/account-exists-with-different-credential') {
                        alert("Email này đã được đăng ký bằng phương thức khác (Google/Email).");
                    } else {
                        alert("Đăng nhập Facebook thất bại: " + error.message);
                    }
                });
        });
    }

    function validatePassword(password) {
        const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$/;
        return regex.test(password);
    }
    const signupForm = document.getElementById('signup');
    if (signupForm) {
        signupForm.addEventListener('submit', function (e) {
            const passwordInput = document.querySelector('input[name="password"]');
            const password = passwordInput.value;

            const errorDiv = document.getElementById('js-error');
            if (errorDiv) {
                errorDiv.style.display = 'none';
                errorDiv.innerText = '';
            }

            if (!validatePassword(password)) {
                e.preventDefault();
                errorDiv.innerText = "Mật khẩu yếu: Phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và ký tự đặc biệt!";
                errorDiv.style.display = 'block';
                passwordInput.focus();
                passwordInput.style.border = "1px solid red";
            } else {
                passwordInput.style.border = "";
            }
        });
    }
    const resetForm = document.getElementById('reset-password-form');
    if (resetForm) {
        resetForm.addEventListener('submit', function (e) {
            const newPassInput = resetForm.querySelector('input[name="new_password"]');
            const confirmPassInput = resetForm.querySelector('input[name="confirm_password"]');
            const newPass = newPassInput.value;
            const confirmPass = confirmPassInput.value;
            const errorDiv = document.getElementById('js-reset-error');

            if (errorDiv) {
                errorDiv.style.display = 'none';
                errorDiv.innerText = '';
            }
            newPassInput.style.border = "";
            confirmPassInput.style.border = "";

            if (!validatePassword(newPass)) {
                e.preventDefault();
                if (errorDiv) {
                    errorDiv.innerText = "Mật khẩu mới yếu: Cần 8 ký tự, 1 Hoa, 1 thường, 1 đặc biệt!";
                    errorDiv.style.display = 'block';
                }
                newPassInput.focus();
                newPassInput.style.border = "1px solid red";
                return;
            }
            if (newPass !== confirmPass) {
                e.preventDefault();

                if (errorDiv) {
                    errorDiv.innerText = "Mật khẩu xác nhận không khớp!";
                    errorDiv.style.display = 'block';
                } else {
                    alert("Mật khẩu xác nhận không khớp!");
                }

                confirmPassInput.focus();
                confirmPassInput.style.border = "1px solid red";
            }

        });
    }
    const loginView = document.getElementById('login_view');
    const forgotView = document.getElementById('forgot_view');
    const showForgotLink = document.getElementById('show_forgot_view');
    const showLoginLink = document.getElementById('show_login_view');
    if (showForgotLink) {
        showForgotLink.addEventListener('click', function (event) {
            event.preventDefault();
            if (loginView && forgotView) {
                loginView.style.display = 'none';
                forgotView.style.display = 'block';
                document.title = 'Việt Sắc Đỏ - Khôi phục mật khẩu';
            }
        });
    }

    if (showLoginLink) {
        showLoginLink.addEventListener('click', function (event) {
            event.preventDefault();

            if (loginView && forgotView) {
                loginView.style.display = 'block';
                forgotView.style.display = 'none';
                document.title = 'Việt Sắc Đỏ - Đăng nhập';
            }
        });
    }

    const bridgeShowPopup = document.getElementById('bridge-show-popup');
    const bridgeUserEmail = document.getElementById('bridge-user-email');
    const verifyModal = document.getElementById('verifyModal');
    const userEmailDisplay = document.getElementById('userEmailDisplay');

    if (bridgeShowPopup && verifyModal) {
        if (bridgeUserEmail && userEmailDisplay) {
            userEmailDisplay.innerText = bridgeUserEmail.value;
        }
        verifyModal.style.display = 'flex';
        setTimeout(() => {
            verifyModal.classList.add('active');
        }, 50);
    }

    function closeVerifyPopup() {
        if (verifyModal) {
            verifyModal.classList.remove('active');
            setTimeout(() => {
                verifyModal.style.display = 'none';
            }, 300);
        }
    }

    const closeX = document.querySelector('.close-modal-x');
    if (closeX) {
        closeX.addEventListener('click', closeVerifyPopup);
    }
    const confirmBtn = document.querySelector('.modal-btn');
    if (confirmBtn) {
        confirmBtn.addEventListener('click', closeVerifyPopup);
    }
    window.addEventListener('click', function (e) {
        if (e.target === verifyModal) {
            closeVerifyPopup();
        }
    });
});

document.body.addEventListener('click', function (e) {
    if (e.target.classList.contains('toggle-password')) {
        const inputId = e.target.getAttribute('toggle');
        const input = document.getElementById(inputId); // Changed to getElementById for safer selection

        if (input) {
            const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
            input.setAttribute('type', type);

            // Toggle icon
            e.target.classList.toggle('fa-eye');
            e.target.classList.toggle('fa-eye-slash');
        }
    }
});


function doLoginSocial(email, name, uid, providerType) {
    const params = new URLSearchParams();
    params.append('action', providerType);
    params.append('email', email);
    params.append('name', name);
    params.append('uid', uid);

    fetch('Login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
        },
        body: params
    }).then(response => {
        if (response.redirected) {
            window.location.href = response.url;
        } else {
            return response.text().then(text => {
                console.log("Server response:", text);
                window.location.href = "index.jsp";
            });
        }
    }).catch(err => console.error("Fetch error:", err));
}
