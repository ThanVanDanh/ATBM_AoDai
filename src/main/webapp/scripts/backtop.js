document.addEventListener('DOMContentLoaded', (event) => {
    const backToTopBtn = document.getElementById("backToTopBtn");

    if (!backToTopBtn) return;

    const scrollThreshold = 300;

    function toggleBackToTopButton() {
        const scrollTopPosition = document.body.scrollTop || document.documentElement.scrollTop;

        if (scrollTopPosition > scrollThreshold) {
            backToTopBtn.classList.add('show-btn');
        } else {
            backToTopBtn.classList.remove('show-btn');
        }
    }

    window.scrollToTop = function() {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    };

    window.onscroll = function() {
        toggleBackToTopButton();
    };
});