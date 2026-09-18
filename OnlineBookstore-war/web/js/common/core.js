/**
 * OnlineBookstore Core & Security JS Module
 * Role Management, Access Guards, Formatters & Utilities
 */

function getApiBaseUrl() {
    return getContextPath() + '/api/books';
}

const API_BASE_URL = getApiBaseUrl();

document.addEventListener('DOMContentLoaded', () => {
    updateAuthHeaderUI();
    checkAuthPageGuard();
    checkCustomerStoreAccessGuard();
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

/* Session & Role Security Management */
function getSessionId() {
    return localStorage.getItem('sessionId') || '';
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
    }
    if (user) {
        localStorage.setItem('user_info', JSON.stringify(user));
        if (user.role) {
            localStorage.setItem('user_role', user.role.toLowerCase());
        }
    }
    updateAuthHeaderUI();
}

function clearSession() {
    localStorage.removeItem('sessionId');
    localStorage.removeItem('user_info');
    localStorage.removeItem('user_role');
    updateAuthHeaderUI();
}

function logout() {
    clearSession();
    window.location.href = getContextPath() + '/pages/auth/login.xhtml';
}

function updateRoleBadgeUI() {
    updateAuthHeaderUI();
}

function updateAuthHeaderUI() {
    const user = getUserInfo();
    const role = getUserRole();
    const loggedIn = isLoggedIn();

    const authContainers = document.querySelectorAll('#headerAuthContainer, .nav-actions-auth');
    if (authContainers.length > 0) {
        authContainers.forEach(container => {
            const context = getContextPath();
            if (loggedIn && user) {
                container.innerHTML = `
                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                        <span class="user-badge" style="display: inline-flex; align-items: center; gap: 0.4rem; padding: 0.25rem 0.65rem; background: var(--border-color, #e4e4e7); border-radius: 4px; font-size: 0.85rem; font-weight: 600;">
                            <i class="bi bi-person-circle"></i>
                            <span>${escapeHtml(user.fullName || user.username)}</span>
                            <span class="role-chip" style="background: #09090b; color: #fff; padding: 0.1rem 0.4rem; border-radius: 3px; font-size: 0.7rem; font-weight: 700; text-transform: uppercase;">${escapeHtml(role)}</span>
                        </span>
                        <button class="btn btn-secondary btn-sm" onclick="logout()" title="Sign Out">
                            <i class="bi bi-box-arrow-right"></i> Logout
                        </button>
                    </div>
                `;
            } else {
                container.innerHTML = `
                    <div style="display: flex; align-items: center; gap: 0.5rem;">
                        <a href="${context}/pages/auth/login.xhtml" class="btn btn-secondary btn-sm">
                            <i class="bi bi-box-arrow-in-right"></i> Sign In
                        </a>
                        <a href="${context}/pages/auth/register.xhtml" class="btn btn-primary btn-sm">
                            <i class="bi bi-person-plus"></i> Register
                        </a>
                    </div>
                `;
            }
        });
    }
}

function checkAdminAccessGuard() {
    const loggedIn = isLoggedIn();
    const role = getUserRole();

    if (!loggedIn) {
        const redirectUrl = getContextPath() + '/pages/auth/login.xhtml?redirect=' + encodeURIComponent(window.location.pathname);
        const guardContainer = document.getElementById('adminAccessGuard');
        if (guardContainer) {
            guardContainer.innerHTML = `
                <div class="card-detail-wrap" style="text-align: center; padding: 4rem 2rem; margin-top: 2rem;">
                    <div style="font-size: 3.5rem; margin-bottom: 1rem; color: var(--text-muted);"><i class="bi bi-lock"></i></div>
                    <h2 style="font-size: 1.75rem; font-weight: 800; margin-bottom: 0.5rem;">Authentication Required</h2>
                    <p style="color: var(--text-muted); max-width: 500px; margin: 0 auto 1.5rem auto;">
                        You must be signed in with a Manager or Admin account to access the Admin Portal.
                    </p>
                    <div style="display: flex; gap: 1rem; justify-content: center;">
                        <a href="${redirectUrl}" class="btn btn-primary">
                            <i class="bi bi-box-arrow-in-right"></i> Sign In to Account
                        </a>
                    </div>
                </div>
            `;
        }
        return false;
    }

    if (role !== 'manager' && role !== 'admin') {
        const guardContainer = document.getElementById('adminAccessGuard');
        if (guardContainer) {
            guardContainer.innerHTML = `
                <div class="card-detail-wrap" style="text-align: center; padding: 4rem 2rem; margin-top: 2rem;">
                    <div style="font-size: 3.5rem; margin-bottom: 1rem; color: #ef4444;"><i class="bi bi-shield-slash"></i></div>
                    <h2 style="font-size: 1.75rem; font-weight: 800; margin-bottom: 0.5rem;">403 - Access Denied</h2>
                    <p style="color: var(--text-muted); max-width: 500px; margin: 0 auto 1.5rem auto;">
                        Your account role is <strong>${escapeHtml(role.toUpperCase() || 'CUSTOMER')}</strong>. This area is restricted strictly to <strong>MANAGER / ADMIN</strong> users.
                    </p>
                    <div style="display: flex; gap: 1rem; justify-content: center;">
                        <button class="btn btn-secondary" onclick="logout()">
                            <i class="bi bi-box-arrow-right"></i> Sign Out
                        </button>
                    </div>
                </div>
            `;
        }
        return false;
    }

    return true;
}

function checkCustomerStoreAccessGuard() {
    if (!isLoggedIn()) return;
    const role = getUserRole();
    if (role === 'manager' || role === 'admin') {
        const currentPath = window.location.pathname;
        if (currentPath.includes('/pages/customer/')) {
            window.location.href = getContextPath() + '/pages/admin/dashboard.xhtml';
        }
    }
}

function checkAuthPageGuard() {
    const currentPath = window.location.pathname;
    if (currentPath.includes('/pages/auth/login.xhtml') || currentPath.includes('/pages/auth/register.xhtml')) {
        if (isLoggedIn()) {
            const role = getUserRole();
            const target = (role === 'manager' || role === 'admin') ? '/pages/admin/dashboard.xhtml' : '/pages/customer/home.xhtml';
            window.location.href = getContextPath() + target;
        }
    }
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

    if (num > 1000) {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(num);
    }
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
