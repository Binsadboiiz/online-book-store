/**
 * OnlineBookstore Auth UI & UX Switcher
 * Smooth transitions between Login and Register forms, password toggle & micro-interactions
 */

(function () {
    function initAuthPage() {
        setupPasswordToggles();
        setupAuthTabs();
    }

    function setupPasswordToggles() {
        const toggleBtns = document.querySelectorAll('.password-toggle-btn');
        toggleBtns.forEach(btn => {
            btn.onclick = function (e) {
                e.preventDefault();
                const input = this.parentElement.querySelector('input');
                const icon = this.querySelector('i');

                if (input) {
                    if (input.type === 'password') {
                        input.type = 'text';
                        icon.className = 'bi bi-eye-slash';
                    } else {
                        input.type = 'password';
                        icon.className = 'bi bi-eye';
                    }
                }
            };
        });
    }

    function setupAuthTabs() {
        const loginTab = document.getElementById('tabLoginBtn');
        const registerTab = document.getElementById('tabRegisterBtn');
        const loginPanel = document.getElementById('loginFormPanel');
        const registerPanel = document.getElementById('registerFormPanel');
        const tabGlider = document.querySelector('.auth-tab-glider');

        if (!loginTab || !registerTab || !loginPanel || !registerPanel) return;

        function switchTab(activeTab) {
            if (activeTab === 'register') {
                loginTab.classList.remove('active');
                registerTab.classList.add('active');
                if (tabGlider) tabGlider.style.transform = 'translateX(100%)';

                loginPanel.classList.remove('active');
                registerPanel.classList.add('active');
            } else {
                registerTab.classList.remove('active');
                loginTab.classList.add('active');
                if (tabGlider) tabGlider.style.transform = 'translateX(0%)';

                registerPanel.classList.remove('active');
                loginPanel.classList.add('active');
            }
        }

        loginTab.onclick = function (e) {
            e.preventDefault();
            switchTab('login');
        };

        registerTab.onclick = function (e) {
            e.preventDefault();
            switchTab('register');
        };

        // Check URL parameter or body attribute for initial tab
        const urlParams = new URLSearchParams(window.location.search);
        const tabParam = urlParams.get('tab');
        if (tabParam === 'register' || document.body.dataset.activeAuth === 'register') {
            switchTab('register');
        } else {
            switchTab('login');
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initAuthPage);
    } else {
        initAuthPage();
    }

    window.initAuthPage = initAuthPage;
})();
