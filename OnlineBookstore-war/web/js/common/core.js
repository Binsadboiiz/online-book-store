/**
 * OnlineBookstore Core & Security JS Module
 * Utilities, Modal Helpers, Formatters
 */

function getApiBaseUrl() {
    return getContextPath() + '/api/books';
}

var API_BASE_URL = window.API_BASE_URL || getApiBaseUrl();
window.API_BASE_URL = API_BASE_URL;

document.addEventListener('DOMContentLoaded', () => {
    setupModalBaseEvents();
    highlightActiveNavLink();
});

function highlightActiveNavLink() {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-menu .nav-link');
    let hasActive = Array.from(navLinks).some(link => link.classList.contains('active'));

    if (!hasActive) {
        navLinks.forEach(link => {
            const href = link.getAttribute('href');
            if (href) {
                const cleanHref = href.split('?')[0].split('#')[0];
                if (cleanHref && (currentPath.endsWith(cleanHref) || (cleanHref.length > 5 && currentPath.includes(cleanHref)))) {
                    link.classList.add('active');
                }
            }
        });
    }
}

function handleBookPreviewModal(data) {
    if (data && data.status === 'success') {
        const modal = document.getElementById('detailModal');
        if (modal) modal.classList.add('active');
    }
}

function getSessionId() {
    return localStorage.getItem('sessionId') || localStorage.getItem('auth_token') || '';
}

function getUserInfo() {
    try {
        const raw = localStorage.getItem('user_info');
        return raw ? JSON.parse(raw) : null;
    } catch (e) {
        return null;
    }
}

function getUserRole() {
    const user = getUserInfo();
    if (user && user.role) {
        return user.role.toLowerCase();
    }
    return localStorage.getItem('user_role') || '';
}

function isLoggedIn() {
    return !!getSessionId();
}

function saveSession(sessionId, user) {
    if (sessionId) {
        localStorage.setItem('sessionId', sessionId);
        localStorage.setItem('auth_token', sessionId);
    }
    if (user) {
        localStorage.setItem('user_info', JSON.stringify(user));
        if (user.role) {
            localStorage.setItem('user_role', user.role.toLowerCase());
        }
    }
}

function clearSession() {
    localStorage.removeItem('sessionId');
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_info');
    localStorage.removeItem('user_role');
}

function logout() {
    clearSession();
    window.location.href = getContextPath() + '/pages/auth/login.xhtml';
}

function getContextPath() {
    if (window.CONTEXT_PATH !== undefined) {
        return window.CONTEXT_PATH;
    }
    const scripts = document.getElementsByTagName('script');
    for (let i = 0; i < scripts.length; i++) {
        const src = scripts[i].getAttribute('src');
        if (src && src.includes('/js/')) {
            const idx = src.indexOf('/js/');
            window.CONTEXT_PATH = src.substring(0, idx);
            return window.CONTEXT_PATH;
        }
    }
    const path = window.location.pathname;
    if (path.includes('/pages/')) {
        const idx = path.indexOf('/pages/');
        window.CONTEXT_PATH = path.substring(0, idx);
        return window.CONTEXT_PATH;
    }
    if (path.includes('/OnlineBookstore-war')) {
        window.CONTEXT_PATH = '/OnlineBookstore-war';
        return window.CONTEXT_PATH;
    }
    window.CONTEXT_PATH = '';
    return window.CONTEXT_PATH;
}

/* Modal Helper Events */
function setupModalBaseEvents() {
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            closeModals();
        }
    });

    document.addEventListener('click', (e) => {
        if (e.target.classList.contains('modal-backdrop') || e.target.classList.contains('modal-overlay')) {
            closeModals();
        }
    });
}

function closeModals() {
    const modals = document.querySelectorAll('.modal-backdrop.active, .modal-overlay.active');
    modals.forEach(m => m.classList.remove('active'));
}

/* Formatters & String Safety */
function formatCurrency(amount) {
    if (amount === null || amount === undefined) return '$0.00';
    const num = Number(amount);
    if (isNaN(num)) return '$0.00';

    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(num);
}

function escapeHtml(str) {
    if (str === null || str === undefined) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

window.handleBookPreviewModal = handleBookPreviewModal;
window.closeModals = closeModals;
