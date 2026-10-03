/**
 * OnlineBookstore Gloss Sheen & Micro-Elevation Engine + Scroll Reveal Motion
 * Clean 2D micro-elevation, specular glass sheen sweep, and smooth entrance motion.
 * (Zero 3D tilt/rotation for crisp, high-class readability)
 */

(function () {
    let requestFrameId = null;

    function initCardEffectsAndMotion() {
        setupGlossSheenElements();
        setupScrollReveal();
    }

    /**
     * Sleek Gloss Sheen Sweep & Micro-Elevation Effect
     */
    function setupGlossSheenElements() {
        const targetElements = document.querySelectorAll('.book-card, .feature-card, .genre-card, .hero-graphic-card, .testimonial-card');

        targetElements.forEach(el => {
            if (el.dataset.sheenInitialized) return;
            el.dataset.sheenInitialized = 'true';

            // Ensure relative positioning
            if (window.getComputedStyle(el).position === 'static') {
                el.style.position = 'relative';
            }

            // Create specular gloss sheen overlay if not present
            let sheen = el.querySelector('.gloss-sheen-overlay');
            if (!sheen) {
                sheen = document.createElement('div');
                sheen.className = 'gloss-sheen-overlay';
                sheen.style.cssText = `
                    position: absolute;
                    top: 0; left: 0; width: 100%; height: 100%;
                    border-radius: inherit;
                    pointer-events: none;
                    background: linear-gradient(115deg, transparent 35%, rgba(255,255,255,0.22) 50%, transparent 65%);
                    background-size: 200% 200%;
                    background-position: -100% -100%;
                    opacity: 0;
                    transition: opacity 0.3s ease, background-position 0.4s ease;
                    z-index: 5;
                `;
                el.appendChild(sheen);
            }

            el.addEventListener('mousemove', (e) => {
                const rect = el.getBoundingClientRect();
                const x = e.clientX - rect.left;
                const y = e.clientY - rect.top;

                const posX = (x / rect.width) * 100;
                const posY = (y / rect.height) * 100;

                if (requestFrameId) cancelAnimationFrame(requestFrameId);

                requestFrameId = requestAnimationFrame(() => {
                    sheen.style.opacity = '1';
                    sheen.style.backgroundPosition = `${posX * 1.5}% ${posY * 1.5}%`;
                });
            });

            el.addEventListener('mouseleave', () => {
                if (requestFrameId) cancelAnimationFrame(requestFrameId);
                requestFrameId = requestAnimationFrame(() => {
                    sheen.style.opacity = '0';
                    sheen.style.backgroundPosition = '-100% -100%';
                });
            });
        });
    }

    /**
     * Staggered Scroll Reveal Entry Motion
     */
    function setupScrollReveal() {
        const revealElements = document.querySelectorAll('.animate-on-scroll, .book-grid > div, .features-bar > div, .genre-grid > a');
        if (!revealElements.length) return;

        revealElements.forEach((el, idx) => {
            if (!el.classList.contains('animate-on-scroll')) {
                el.classList.add('animate-on-scroll');
            }
            el.style.setProperty('--scroll-index', (idx % 6).toString());
        });

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('is-visible');
                    observer.unobserve(entry.target);
                }
            });
        }, {
            threshold: 0.1,
            rootMargin: '0px 0px -40px 0px'
        });

        revealElements.forEach(el => observer.observe(el));
    }

    // Initialize on DOM ready or AJAX re-renders
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initCardEffectsAndMotion);
    } else {
        initCardEffectsAndMotion();
    }

    // JSF AJAX Event Handler for dynamic re-renders
    if (typeof jsf !== 'undefined' && jsf.ajax) {
        jsf.ajax.addOnEvent((data) => {
            if (data.status === 'success') {
                setTimeout(initCardEffectsAndMotion, 50);
            }
        });
    }

    window.initCardEffectsAndMotion = initCardEffectsAndMotion;
})();
