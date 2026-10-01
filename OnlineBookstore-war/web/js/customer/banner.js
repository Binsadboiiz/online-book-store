/**
 * OnlineBookstore Home Banner Carousel Controller
 * Minimalist Modern UI - Auto-sliding, touch support, dot indicators & controls
 */

(function () {
    let currentSlide = 0;
    let autoSlideInterval = null;
    const SLIDE_DURATION = 5000; // 5 seconds

    function initHeroBanner() {
        const carousel = document.querySelector('.hero-carousel');
        if (!carousel) return;

        const slides = carousel.querySelectorAll('.hero-slide');
        const dots = carousel.querySelectorAll('.hero-dot');
        const prevBtn = carousel.querySelector('.hero-arrow.prev');
        const nextBtn = carousel.querySelector('.hero-arrow.next');

        if (!slides.length) return;

        function showSlide(index) {
            if (index >= slides.length) currentSlide = 0;
            else if (index < 0) currentSlide = slides.length - 1;
            else currentSlide = index;

            slides.forEach((slide, i) => {
                if (i === currentSlide) {
                    slide.classList.add('active');
                } else {
                    slide.classList.remove('active');
                }
            });

            dots.forEach((dot, i) => {
                if (i === currentSlide) {
                    dot.classList.add('active');
                } else {
                    dot.classList.remove('active');
                }
            });
        }

        function nextSlide() {
            showSlide(currentSlide + 1);
        }

        function prevSlide() {
            showSlide(currentSlide - 1);
        }

        function startAutoSlide() {
            stopAutoSlide();
            autoSlideInterval = setInterval(nextSlide, SLIDE_DURATION);
        }

        function stopAutoSlide() {
            if (autoSlideInterval) {
                clearInterval(autoSlideInterval);
                autoSlideInterval = null;
            }
        }

        // Event Listeners
        if (prevBtn) {
            prevBtn.onclick = (e) => {
                e.preventDefault();
                prevSlide();
                startAutoSlide();
            };
        }

        if (nextBtn) {
            nextBtn.onclick = (e) => {
                e.preventDefault();
                nextSlide();
                startAutoSlide();
            };
        }

        dots.forEach((dot, index) => {
            dot.onclick = (e) => {
                e.preventDefault();
                showSlide(index);
                startAutoSlide();
            };
        });

        // Pause on hover
        carousel.onmouseenter = stopAutoSlide;
        carousel.onmouseleave = startAutoSlide;

        // Touch Swipe Support for Mobile
        let touchStartX = 0;
        let touchEndX = 0;

        carousel.addEventListener('touchstart', (e) => {
            touchStartX = e.changedTouches[0].screenX;
        }, { passive: true });

        carousel.addEventListener('touchend', (e) => {
            touchEndX = e.changedTouches[0].screenX;
            handleSwipe();
        }, { passive: true });

        function handleSwipe() {
            const diff = touchStartX - touchEndX;
            if (Math.abs(diff) > 40) {
                if (diff > 0) nextSlide();
                else prevSlide();
                startAutoSlide();
            }
        }

        // Initial setup
        showSlide(currentSlide);
        startAutoSlide();
    }

    // Initialize on DOM Ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initHeroBanner);
    } else {
        initHeroBanner();
    }

    // Expose init function globally if JSF AJAX re-renders component
    window.initHeroBanner = initHeroBanner;
})();
