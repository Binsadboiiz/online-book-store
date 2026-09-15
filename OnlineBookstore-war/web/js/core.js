/**
 * OnlineBookstore Core & Security JS Module
 * Role Management, Access Guards, Formatters & Utilities
 */

function getApiBaseUrl() {
    return getContextPath() + '/api/books';
}

const API_BASE_URL = getApiBaseUrl();

document.addEventListener('DOMContentLoaded', () => {
    updateRoleBadgeUI();
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

/* Role State Management */
function getUserRole() {
    return localStorage.getItem('user_role') || 'customer';
}

function setUserRole(role) {
    localStorage.setItem('user_role', role);
    updateRoleBadgeUI();
}

function toggleUserRole() {
    const current = getUserRole();
    const nextRole = current === 'admin' ? 'customer' : 'admin';
    setUserRole(nextRole);

    alert(`Role switched to: ${nextRole.toUpperCase()}`);

    if (window.location.pathname.includes('/pages/admin/') && nextRole !== 'admin') {
        window.location.href = getContextPath() + '/pages/customer/home.xhtml';
    } else {
        window.location.reload();
    }
}

function updateRoleBadgeUI() {
    const badge = document.getElementById('currentRoleBadge');
    if (badge) {
        const role = getUserRole();
        badge.textContent = role.charAt(0).toUpperCase() + role.slice(1);
    }
}

function checkAdminAccessGuard() {
    const role = getUserRole();
    if (role !== 'admin') {
        const guardContainer = document.getElementById('adminAccessGuard');
        if (guardContainer) {
            guardContainer.innerHTML = `
                <div class="card-detail-wrap" style="text-align: center; padding: 4rem 2rem; margin-top: 2rem;">
                    <div style="font-size: 3.5rem; margin-bottom: 1rem; color: var(--text-muted);"><i class="bi bi-shield-lock"></i></div>
                    <h2 style="font-size: 1.75rem; font-weight: 800; margin-bottom: 0.5rem;">403 - Access Denied</h2>
                    <p style="color: var(--text-muted); max-width: 500px; margin: 0 auto 1.5rem auto;">
                        This area is restricted to administrators. You are currently browsing as <strong>CUSTOMER</strong>.
                    </p>
                    <div style="display: flex; gap: 1rem; justify-content: center;">
                        <button class="btn btn-primary" onclick="toggleUserRole()">
                            <i class="bi bi-arrow-repeat"></i> Switch Role to Admin
                        </button>
                        <a href="${getContextPath()}/pages/customer/home.xhtml" class="btn btn-secondary">
                            <i class="bi bi-arrow-left"></i> Return to Customer Store
                        </a>
                    </div>
                </div>
            `;
        }
        return false;
    }
    return true;
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
