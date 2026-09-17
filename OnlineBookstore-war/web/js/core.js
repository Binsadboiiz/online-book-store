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
    window.location.href = getContextPath() + '/pages/customer/home.xhtml';
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
                const isRoleAdmin = role === 'admin';
                const isOnAdminPage = window.location.pathname.includes('/pages/admin/');

                container.innerHTML = `
                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                        <span class="user-badge" style="display: inline-flex; align-items: center; gap: 0.4rem; padding: 0.25rem 0.65rem; background: var(--border-color, #e4e4e7); border-radius: 4px; font-size: 0.85rem; font-weight: 600;">
                            <i class="bi bi-person-circle"></i>
                            <span>${escapeHtml(user.fullName || user.username)}</span>
                            <span class="role-chip" style="background: #09090b; color: #fff; padding: 0.1rem 0.4rem; border-radius: 3px; font-size: 0.7rem; font-weight: 700; text-transform: uppercase;">${escapeHtml(role)}</span>
                        </span>
                        ${isRoleAdmin && !isOnAdminPage ? `
                            <a href="${context}/pages/admin/dashboard.xhtml" class="btn btn-secondary btn-sm" title="Admin Portal">
                                <i class="bi bi-shield-lock-fill"></i> Admin Portal
                            </a>
                        ` : ''}
                        ${isRoleAdmin && isOnAdminPage ? `
                            <a href="${context}/pages/customer/home.xhtml" class="btn btn-secondary btn-sm" title="Customer Store">
                                <i class="bi bi-shop"></i> Customer Store
                            </a>
                        ` : ''}
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
                        You must be signed in with an Administrator account to access the Admin Portal.
                    </p>
                    <div style="display: flex; gap: 1rem; justify-content: center;">
                        <a href="${redirectUrl}" class="btn btn-primary">
                            <i class="bi bi-box-arrow-in-right"></i> Sign In to Admin Account
                        </a>
                        <a href="${getContextPath()}/pages/customer/home.xhtml" class="btn btn-secondary">
                            <i class="bi bi-arrow-left"></i> Return to Customer Store
                        </a>
                    </div>
                </div>
            `;
        }
        return false;
    }

    if (role !== 'admin') {
        const guardContainer = document.getElementById('adminAccessGuard');
        if (guardContainer) {
            guardContainer.innerHTML = `
                <div class="card-detail-wrap" style="text-align: center; padding: 4rem 2rem; margin-top: 2rem;">
                    <div style="font-size: 3.5rem; margin-bottom: 1rem; color: #ef4444;"><i class="bi bi-shield-slash"></i></div>
                    <h2 style="font-size: 1.75rem; font-weight: 800; margin-bottom: 0.5rem;">403 - Access Denied</h2>
                    <p style="color: var(--text-muted); max-width: 500px; margin: 0 auto 1.5rem auto;">
                        Your account role is <strong>${escapeHtml(role.toUpperCase() || 'CUSTOMER')}</strong>. This area is restricted strictly to <strong>ADMINISTRATOR</strong> users.
                    </p>
                    <div style="display: flex; gap: 1rem; justify-content: center;">
                        <a href="${getContextPath()}/pages/customer/home.xhtml" class="btn btn-primary">
                            <i class="bi bi-house"></i> Return to Customer Store
                        </a>
                        <button class="btn btn-secondary" onclick="logout()">
                            <i class="bi bi-box-arrow-right"></i> Switch Account
                        </button>
                    </div>
                </div>
            `;
        }
        return false;
    }

    return true;
}

function checkAuthPageGuard() {
    const currentPath = window.location.pathname;
    if (currentPath.includes('/pages/auth/login.xhtml') || currentPath.includes('/pages/auth/register.xhtml')) {
        if (isLoggedIn()) {
            const role = getUserRole();
            const target = role === 'admin' ? '/pages/admin/dashboard.xhtml' : '/pages/customer/home.xhtml';
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
    return '';
}

function setupModalBaseEvents() {
    document.querySelectorAll('.modal-close, .modal-backdrop').forEach(element => {
        element.addEventListener('click', (e) => {
            if (e.target === element) {
                closeModals();
            }
        });
    });
}

function closeModals() {
    document.querySelectorAll('.modal-backdrop').forEach(modal => {
        modal.classList.remove('active');
    });
}

function formatCurrency(amount) {
    if (amount === undefined || amount === null) return '$0.00';
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount);
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

function getDemoBooks() {
    return [
        {
            id: 1,
            title: "Designing Data-Intensive Applications",
            authorName: "Martin Kleppmann",
            categoryName: "Architecture",
            price: 49.99,
            discountPrice: 39.99,
            finalPrice: 39.99,
            stockQuantity: 25,
            description: "The definitive guide to data system architecture, distributed systems, replication, partitioning, and consistency models.",
            coverImage: "",
            isbn: "978-1449373320",
            publisherName: "O'Reilly Media",
            publishedYear: 2017,
            pages: 616,
            language: "English"
        },
        {
            id: 2,
            title: "Clean Code: A Handbook of Agile Software Craftsmanship",
            authorName: "Robert C. Martin",
            categoryName: "Software Engineering",
            price: 44.95,
            discountPrice: null,
            finalPrice: 44.95,
            stockQuantity: 12,
            description: "Even bad code can function. But if code isn't clean, it can bring a development organization to its knees.",
            coverImage: "",
            isbn: "978-0132350884",
            publisherName: "Prentice Hall",
            publishedYear: 2008,
            pages: 464,
            language: "English"
        },
        {
            id: 3,
            title: "System Design Interview – An Insider's Guide",
            authorName: "Alex Xu",
            categoryName: "Architecture",
            price: 36.00,
            discountPrice: 29.99,
            finalPrice: 29.99,
            stockQuantity: 18,
            description: "Step-by-step framework to tackle system design questions with clear diagrams and real-world scalability patterns.",
            coverImage: "",
            isbn: "978-1736049112",
            publisherName: "ByteByteGo",
            publishedYear: 2020,
            pages: 320,
            language: "English"
        }
    ];
}
