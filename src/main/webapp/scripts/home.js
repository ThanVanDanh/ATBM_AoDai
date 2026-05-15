    document.addEventListener('DOMContentLoaded', function () {
        const carousel = document.querySelector('#myCarousel');
        if (!carousel) return;
        const carouselInner = carousel.querySelector('.carousel-inner');
        const items = carousel.querySelectorAll('.item');
        const prevButton = carousel.querySelector('.left.carousel-control');
        const nextButton = carousel.querySelector('.right.carousel-control');

        if (items.length <= 1) return;

        const firstClone = items[0].cloneNode(true);
        const lastClone = items[items.length - 1].cloneNode(true);

        firstClone.classList.add('clone');
        lastClone.classList.add('clone');

        carouselInner.appendChild(firstClone);
        carouselInner.insertBefore(lastClone, items[0]);

        const allItems = carousel.querySelectorAll('.item');
        const totalItems = allItems.length;

        let currentIndex = 1;
        let isTransitioning = false;
        const autoPlayDelay = 4000;
        let autoPlayInterval = null;

        function setInitialPosition() {
            carouselInner.style.transition = 'none';
            carouselInner.style.transform = `translateX(-${currentIndex * 100}%)`;

            setTimeout(() => {
                carouselInner.style.transition = 'transform 0.5s ease-in-out';
            }, 20);
        }

        setInitialPosition();

        function moveToSlide(index) {
            if (isTransitioning) return;
            isTransitioning = true;

            currentIndex = index;
            carouselInner.style.transform = `translateX(-${currentIndex * 100}%)`;
        }

        nextButton.addEventListener('click', (event) => {
            event.preventDefault();
            moveToSlide(currentIndex + 1);
        });

        prevButton.addEventListener('click', (event) => {
            event.preventDefault();
            moveToSlide(currentIndex - 1);
        });

        carouselInner.addEventListener('transitionend', () => {
            if (allItems[currentIndex].classList.contains('clone')) {
                carouselInner.style.transition = 'none';

                if (currentIndex === 0) {
                    currentIndex = totalItems - 2;
                } else if (currentIndex === totalItems - 1) {
                    currentIndex = 1;
                }

                carouselInner.style.transform = `translateX(-${currentIndex * 100}%)`;

                carouselInner.offsetHeight;
                carouselInner.style.transition = 'transform 0.5s ease-in-out';
            }

            isTransitioning = false;
        });
        function startAutoPlay() {
            if (autoPlayInterval) return;
            autoPlayInterval = setInterval(() => {
                moveToSlide(currentIndex + 1);
            }, autoPlayDelay);
        }

        function stopAutoPlay() {
            clearInterval(autoPlayInterval);
            autoPlayInterval = null;
        }

        carousel.addEventListener('mouseenter', stopAutoPlay);

        carousel.addEventListener('mouseleave', startAutoPlay);
        startAutoPlay();
    });
    document.addEventListener('DOMContentLoaded', () => {
        const tabLinks = document.querySelectorAll('.tab-link');

        tabLinks.forEach(link => {
            link.addEventListener('click', function (event) {
                event.preventDefault();

                const currentComponent = this.closest('.tab-component');

                const targetId = this.dataset.target;

                if (!currentComponent) return;

                const linksInComponent = currentComponent.querySelectorAll('.tab-link');
                linksInComponent.forEach(l => l.classList.remove('active'));
                this.classList.add('active');

                const galleriesInComponent = currentComponent.querySelectorAll('.tab-content');
                galleriesInComponent.forEach(gallery => {
                    gallery.classList.remove('active-gallery');
                    gallery.classList.add('hidden-gallery');
                });

                const targetGallery = document.getElementById(targetId);
                if (targetGallery) {
                    targetGallery.classList.remove('hidden-gallery');
                    targetGallery.classList.add('active-gallery');
                }
            });
        });
    });

    document.addEventListener('DOMContentLoaded', function() {

        const searchTrigger = document.getElementById('searchTrigger');
        const searchOverlay = document.getElementById('searchOverlay');
        const searchCloseArea = document.getElementById('searchCloseArea');
        const searchInput = document.getElementById('searchInput');

        searchTrigger.addEventListener('click', function(event) {
            event.preventDefault();

            searchOverlay.classList.add('search-overlay--active');

            setTimeout(() => {
                searchInput.focus();
            }, 400);
        });

        searchCloseArea.addEventListener('click', function() {
            searchOverlay.classList.remove('search-overlay--active');
        });

    });
    document.addEventListener("DOMContentLoaded", () => {
        const modal = document.getElementById("quick-view-model");
        if (modal) {
            const closeBtn = document.getElementById("close-model");
            const quickViewButtons = document.querySelectorAll('.icon-button[title="Xem nhanh"]');

            const successPopup = document.getElementById("success-add-shopping");
            const closeSuccessPopupBtn = document.getElementById("close-success-popup");
            const addToCartBtn = modal.querySelector(".add-shopping");

            const swiper = new Swiper('.model-group-img', {
                slidesPerView: 4,
                loop: true,
                spaceBetween: 10,
                autoplay: {
                    delay: 2500,
                    disableOnInteraction: false,
                },
            });
            quickViewButtons.forEach(btn => {
                btn.addEventListener("click", (e) => {
                    e.preventDefault();
                    modal.classList.add("active");
                });
            });
            addToCartBtn.addEventListener("click", (e) => {
                e.preventDefault();
                modal.classList.remove("active");
                successPopup.classList.add("active");
            });
            closeBtn.addEventListener("click", () => {
                modal.classList.remove("active");
            });

            modal.addEventListener("click", (e) => {
                if (e.target === modal) {
                    modal.classList.remove("active");
                }
            });
            closeSuccessPopupBtn.addEventListener("click", () => {
                successPopup.classList.remove("active");
            });
            successPopup.addEventListener("click", (e) => {
                if (e.target === successPopup) {
                    successPopup.classList.remove("active");
                }
            });

            const quantityWrappers = document.querySelectorAll('.quantity, .quatity-sp');

            quantityWrappers.forEach(wrapper => {
                const btnMinus = wrapper.querySelector('.btn-minus');
                const btnPlus = wrapper.querySelector('.btn-plus');
                const input = wrapper.querySelector('input');

                if (btnMinus && btnPlus && input) {
                    btnMinus.addEventListener('click', (e) => {
                        e.preventDefault();
                        let val = parseInt(input.value) || 1;
                        if (val > 1) input.value = val - 1;
                    });

                    btnPlus.addEventListener('click', (e) => {
                        e.preventDefault();
                        let val = parseInt(input.value) || 1;
                        input.value = val + 1;
                    });

                    input.addEventListener('input', function (e) {
                        e.preventDefault();
                        this.value = this.value.replace(/[^0-9]/g, '1');
                    });


                    input.addEventListener('keypress', function (e) {
                        if (!/[0-9]/.test(e.key)) {
                            e.preventDefault();
                        }
                    });
                }
            });
        }
        const removeButtons = document.querySelectorAll('.remove-item');
        removeButtons.forEach(btn => {
            btn.addEventListener('click', function (e) {
                const href = (this.getAttribute('href') || '').trim();

                const isRealLink = href && href !== '#' && !href.toLowerCase().startsWith('javascript:');
                if (isRealLink) {
                    return;
                }

                e.preventDefault();
                const item = this.closest('li');
                if (item) {
                    item.remove();
                    updateMiniCartCount();
                    checkEmptyCart();
                    checkMiniCart();
                }
            });
        });
        updateMiniCartCount();
        checkEmptyCart();
        checkMiniCart();

        function checkEmptyCart() {
            const cartList = document.querySelector('.cart-items-list-sp');
            const dataContainer = document.getElementById('cart-data-container');
            const emptyMessage = document.getElementById('empty-cart-message');

            if (cartList) {
                const itemCount = cartList.querySelectorAll('li').length;

                if (itemCount === 0) {
                    if(dataContainer) dataContainer.style.display = 'none';
                    if(emptyMessage) emptyMessage.style.display = 'block';

                } else {
                    if(dataContainer) dataContainer.style.display = 'flex';
                    if(emptyMessage) emptyMessage.style.display = 'none';
                }
            }
        }
        function checkMiniCart() {
            const cartMenus = document.querySelectorAll('.mini-cart-menu');

            cartMenus.forEach(menu => {
                const cartList = menu.querySelector('.mini-cart-items-list');
                const cartFooter = menu.querySelector('.mini-cart-footer');
                const empCart = menu.querySelector('.mini-empty-cart');

                if (!cartList) return;

                const itemCount = cartList.querySelectorAll('li').length;

                if (itemCount === 0) {
                    cartList.style.display = 'none';
                    if (cartFooter) cartFooter.style.display = 'none';
                    if (empCart) empCart.style.display = 'block';
                } else {
                    cartList.style.display = 'block';
                    if (cartFooter) cartFooter.style.display = 'block';
                    if (empCart) empCart.style.display = 'none';
                }
            });
        }
        function updateMiniCartCount() {
            const cartMenus = document.querySelectorAll('.mini-cart-menu');

            cartMenus.forEach(menu => {
                const cartList = menu.querySelector('.mini-cart-items-list');
                const countElement = menu.querySelector('.mini-count_item');

                if (cartList && countElement) {
                    let totalQty = 0;
                    const quantityElements = cartList.querySelectorAll('.mini-quantity');

                    quantityElements.forEach(el => {
                        let text = el.textContent.replace('x', '').trim();
                        let qty = parseInt(text) || 0;
                        totalQty += qty;
                    });
                    countElement.textContent = totalQty;
                }
            });
        }

    });
