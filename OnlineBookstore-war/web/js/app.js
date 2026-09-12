/**
 * OnlineBookstore Client Application JS
 * Minimalist Monochrome UI Theme (Bootstrap Icons & Role-Based Access Control)
 */

const API_BASE_URL = '/OnlineBookstore-war/api/books';

let allBooks = [];

document.addEventListener('DOMContentLoaded', () => {
    updateRoleBadgeUI();
    setupEventListeners();
});

/* Role & Security Helper Methods */
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

    // If currently on an Admin page and switched to Customer, redirect to Customer storefront
    if (window.location.pathname.includes('/pages/admin/') && nextRole !== 'admin') {
        window.location.href = getContextPath() + '/pages/customer/home.jsp';
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
                        <a href="${getContextPath()}/pages/customer/home.jsp" class="btn btn-secondary">
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
    const path = window.location.pathname;
    if (path.includes('/OnlineBookstore-war')) return '/OnlineBookstore-war';
    return '';
}

function setupEventListeners() {
    // Search Input
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        let debounceTimer;
        searchInput.addEventListener('input', (e) => {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => {
                const keyword = e.target.value.trim();
                fetchBooks(keyword);
            }, 300);
        });
    }

    // Modal Close handlers
    document.querySelectorAll('.modal-close, .modal-backdrop').forEach(element => {
        element.addEventListener('click', (e) => {
            if (e.target === element) {
                closeModals();
            }
        });
    });

    // Add / Edit Book Form submit
    const addBookForm = document.getElementById('addBookForm');
    if (addBookForm) {
        addBookForm.addEventListener('submit', handleAddBookSubmit);
    }
}

async function fetchBooks(keyword = '', limit = null) {
    const grid = document.getElementById('bookGrid');
    if (!grid) return;

    grid.innerHTML = `<div class="empty-state"><div class="empty-title"><i class="bi bi-arrow-repeat spin"></i> Loading books...</div></div>`;

    try {
        let url = '/api/books';
        if (keyword) {
            url += `?q=${encodeURIComponent(keyword)}`;
        }

        let response = await fetch(url);
        if (!response.ok) {
            response = await fetch(`${API_BASE_URL}${keyword ? `?q=${encodeURIComponent(keyword)}` : ''}`);
        }

        if (response.ok) {
            const data = await response.json();
            allBooks = data.data || [];
            renderBooks(allBooks, limit);
            return;
        }
        throw new Error(`HTTP error! Status: ${response.status}`);
    } catch (error) {
        console.warn('Backend API connection offline, utilizing English demo dataset:', error);
        renderFallbackOrEmpty(limit);
    }
}

function renderBooks(books, limit = null) {
    const grid = document.getElementById('bookGrid');
    if (!grid) return;

    let displayBooks = books;
    if (limit && limit > 0) {
        displayBooks = books.slice(0, limit);
    }

    if (!displayBooks || displayBooks.length === 0) {
        grid.innerHTML = `
            <div class="empty-state">
                <div class="empty-title"><i class="bi bi-search"></i> No Books Found</div>
                <div class="empty-desc">Try searching with a different keyword or check back later.</div>
            </div>
        `;
        return;
    }

    grid.innerHTML = displayBooks.map(book => createBookCardHTML(book)).join('');
}

function createBookCardHTML(book) {
    const formattedPrice = formatCurrency(book.finalPrice || book.price);
    const hasDiscount = book.discountPrice && book.discountPrice > 0 && book.discountPrice < book.price;
    const formattedOldPrice = hasDiscount ? formatCurrency(book.price) : '';

    const coverHTML = book.coverImage
        ? `<img src="${escapeHtml(book.coverImage)}" alt="${escapeHtml(book.title)}" class="book-cover" onerror="this.outerHTML='<div class=\\'book-cover-placeholder\\'><i class=\\'bi bi-journal-bookmark\\' style=\\'font-size: 2.25rem;\\'></i><span>Cover Art</span></div>'">`
        : `<div class="book-cover-placeholder"><i class="bi bi-journal-bookmark" style="font-size: 2.25rem;"></i><span>Cover Art</span></div>`;

    return `
        <div class="book-card" onclick="openBookDetailModal(${book.id})">
            <div class="book-cover-wrap">
                ${coverHTML}
            </div>
            <div class="book-card-body">
                <span class="book-category-badge">${escapeHtml(book.categoryName || 'General')}</span>
                <h3 class="book-title" title="${escapeHtml(book.title)}">${escapeHtml(book.title)}</h3>
                <p class="book-author">By ${escapeHtml(book.authorName || 'Unknown Author')}</p>
                <div class="book-footer">
                    <div>
                        ${hasDiscount ? `<span class="book-price-old">${formattedOldPrice}</span>` : ''}
                        <span class="book-price">${formattedPrice}</span>
                    </div>
                    <button class="btn btn-secondary btn-sm" onclick="event.stopPropagation(); openBookDetailModal(${book.id})">
                        Details <i class="bi bi-arrow-right"></i>
                    </button>
                </div>
            </div>
        </div>
    `;
}

function openBookDetailModal(bookId) {
    const book = allBooks.find(b => b.id === bookId);
    if (!book) return;

    const modalBackdrop = document.getElementById('detailModal');
    const modalBody = document.getElementById('detailModalBody');
    if (!modalBackdrop || !modalBody) return;

    modalBody.innerHTML = generateBookDetailHTML(book);
    modalBackdrop.classList.add('active');
}

async function loadStandaloneBookDetail(bookId) {
    const container = document.getElementById('standaloneBookDetail');
    if (!container) return;

    container.innerHTML = `<div class="empty-state"><div class="empty-title"><i class="bi bi-arrow-repeat spin"></i> Loading book details...</div></div>`;

    try {
        let response = await fetch(`/api/books/${bookId}`);
        if (!response.ok) {
            response = await fetch(`${API_BASE_URL}/${bookId}`);
        }
        if (response.ok) {
            const data = await response.json();
            container.innerHTML = generateBookDetailHTML(data.data);
            return;
        }
    } catch (err) {
        console.warn('Using offline details:', err);
    }

    const fallbackBook = getDemoBooks().find(b => b.id == bookId) || getDemoBooks()[0];
    container.innerHTML = generateBookDetailHTML(fallbackBook);
}

function generateBookDetailHTML(book) {
    const formattedPrice = formatCurrency(book.finalPrice || book.price);
    const coverHTML = book.coverImage
        ? `<img src="${escapeHtml(book.coverImage)}" alt="${escapeHtml(book.title)}" class="modal-cover" onerror="this.outerHTML='<div class=\\'book-cover-placeholder\\'><i class=\\'bi bi-journal-text\\' style=\\'font-size: 2.5rem;\\'></i><span>No Cover</span></div>'">`
        : `<div class="book-cover-placeholder"><i class="bi bi-journal-text" style="font-size: 2.5rem;"></i><span>No Cover</span></div>`;

    return `
        <div class="modal-grid">
            <div class="modal-cover-wrap">
                ${coverHTML}
            </div>
            <div>
                <span class="book-category-badge">${escapeHtml(book.categoryName || 'General')}</span>
                <h2 class="modal-detail-title">${escapeHtml(book.title)}</h2>
                <p class="modal-detail-meta">Author: <strong>${escapeHtml(book.authorName || 'Unknown Author')}</strong></p>
                <div class="modal-detail-price">${formattedPrice}</div>
                
                <p class="modal-detail-desc">${escapeHtml(book.description || 'No detailed description available for this book.')}</p>
                
                <table class="specs-table">
                    <tr>
                        <td>ISBN Code:</td>
                        <td>${escapeHtml(book.isbn || 'N/A')}</td>
                    </tr>
                    <tr>
                        <td>Publisher:</td>
                        <td>${escapeHtml(book.publisherName || 'N/A')}</td>
                    </tr>
                    <tr>
                        <td>Published Year:</td>
                        <td>${book.publishedYear || 'N/A'}</td>
                    </tr>
                    <tr>
                        <td>Page Count:</td>
                        <td>${book.pages || 'N/A'} pages</td>
                    </tr>
                    <tr>
                        <td>Language:</td>
                        <td>${escapeHtml(book.language || 'English')}</td>
                    </tr>
                    <tr>
                        <td>Stock Status:</td>
                        <td>${book.stockQuantity > 0 ? `In Stock (${book.stockQuantity})` : 'Out of Stock'}</td>
                    </tr>
                </table>

                <button class="btn btn-primary" style="width: 100%;" onclick="alert('Item added to Shopping Cart!')">
                    <i class="bi bi-cart-plus" style="margin-right: 0.4rem;"></i> Add to Cart
                </button>
            </div>
        </div>
    `;
}

async function fetchAdminBookTable() {
    const tbody = document.getElementById('adminBookTableBody');
    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; padding: 2rem;">Loading inventory data...</td></tr>`;

    try {
        let response = await fetch('/api/books');
        if (!response.ok) {
            response = await fetch(API_BASE_URL);
        }

        if (response.ok) {
            const data = await response.json();
            allBooks = data.data || [];
            renderAdminTable(allBooks);
            return;
        }
    } catch (e) {
        console.warn('API offline, rendering demo admin table:', e);
    }

    allBooks = getDemoBooks();
    renderAdminTable(allBooks);
}

function renderAdminTable(books) {
    const tbody = document.getElementById('adminBookTableBody');
    if (!tbody) return;

    if (!books || books.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; padding: 2rem;">No books in inventory. Click "+ Add New Book" to create one.</td></tr>`;
        return;
    }

    tbody.innerHTML = books.map(b => `
        <tr>
            <td>#${b.id}</td>
            <td>
                <strong>${escapeHtml(b.title)}</strong><br>
                <span style="font-size: 0.8rem; color: var(--text-muted);">${escapeHtml(b.authorName || 'No Author')}</span>
            </td>
            <td><code>${escapeHtml(b.isbn || 'N/A')}</code></td>
            <td>${formatCurrency(b.price)}</td>
            <td>${b.stockQuantity} pcs</td>
            <td><span class="book-category-badge" style="margin: 0;">${b.active !== false ? 'Active' : 'Disabled'}</span></td>
            <td>
                <button class="btn btn-secondary btn-sm" onclick="editBookAdmin(${b.id})">
                    <i class="bi bi-pencil"></i> Edit
                </button>
                <button class="btn btn-danger btn-sm" onclick="deleteBookAdmin(${b.id})">
                    <i class="bi bi-trash"></i> Delete
                </button>
            </td>
        </tr>
    `).join('');
}

function openAddBookModal(editBookId = null) {
    if (getUserRole() !== 'admin') {
        alert('Access Restricted: Only Admin users can perform book management operations.');
        return;
    }

    const modal = document.getElementById('addBookModal');
    const form = document.getElementById('addBookForm');
    const titleEl = document.getElementById('modalFormTitle');
    
    if (!modal || !form) return;

    form.reset();
    document.getElementById('bookIdInput').value = '';

    if (editBookId) {
        const book = allBooks.find(b => b.id === editBookId);
        if (book) {
            if (titleEl) titleEl.textContent = 'Edit Book #' + book.id;
            document.getElementById('bookIdInput').value = book.id;
            form.title.value = book.title || '';
            form.isbn.value = book.isbn || '';
            form.price.value = book.price || 0;
            form.discountPrice.value = book.discountPrice || '';
            form.stockQuantity.value = book.stockQuantity || 0;
            form.description.value = book.description || '';
            form.coverImage.value = book.coverImage || '';
            form.publishedYear.value = book.publishedYear || 2024;
            form.pages.value = book.pages || '';
            form.language.value = book.language || 'English';
        }
    } else {
        if (titleEl) titleEl.textContent = 'Add New Book';
    }

    modal.classList.add('active');
}

function editBookAdmin(id) {
    openAddBookModal(id);
}

async function deleteBookAdmin(id) {
    if (getUserRole() !== 'admin') {
        alert('Access Restricted: Only Admin users can delete books.');
        return;
    }

    if (!confirm('Are you sure you want to delete book #' + id + '?')) return;

    try {
        let response = await fetch(`/api/books/${id}`, { method: 'DELETE' });
        if (!response.ok) {
            response = await fetch(`${API_BASE_URL}/${id}`, { method: 'DELETE' });
        }
        if (response.ok) {
            alert('Book deleted successfully!');
            fetchAdminBookTable();
            return;
        }
    } catch (err) {
        console.error('Delete failed:', err);
    }

    // Client-side fallback if offline
    allBooks = allBooks.filter(b => b.id !== id);
    renderAdminTable(allBooks);
    alert('Book removed from local inventory view!');
}

function closeModals() {
    document.querySelectorAll('.modal-backdrop').forEach(modal => {
        modal.classList.remove('active');
    });
}

async function handleAddBookSubmit(e) {
    e.preventDefault();
    if (getUserRole() !== 'admin') {
        alert('Access Restricted: Only Admin users can create or modify books.');
        return;
    }

    const form = e.target;
    const bookId = document.getElementById('bookIdInput').value;
    
    const requestData = {
        title: form.title.value.trim(),
        isbn: form.isbn.value.trim() || null,
        price: parseFloat(form.price.value),
        discountPrice: form.discountPrice.value ? parseFloat(form.discountPrice.value) : null,
        stockQuantity: parseInt(form.stockQuantity.value) || 0,
        description: form.description.value.trim() || null,
        coverImage: form.coverImage.value.trim() || null,
        publishedYear: form.publishedYear.value ? parseInt(form.publishedYear.value) : null,
        pages: form.pages.value ? parseInt(form.pages.value) : null,
        language: form.language.value.trim() || 'English',
        active: true
    };

    const isEdit = !!bookId;
    const endpoint = isEdit ? `/api/books/${bookId}` : '/api/books';
    const method = isEdit ? 'PUT' : 'POST';

    try {
        let response = await fetch(endpoint, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestData)
        });

        if (response.ok) {
            const result = await response.json();
            alert(isEdit ? 'Book updated successfully!' : 'Book created successfully!');
            form.reset();
            closeModals();
            if (document.getElementById('adminBookTableBody')) fetchAdminBookTable();
            else fetchBooks();
            return;
        }
    } catch (err) {
        console.error('API Error:', err);
    }

    // Local fallback for UI demo
    if (isEdit) {
        const index = allBooks.findIndex(b => b.id == bookId);
        if (index !== -1) {
            allBooks[index] = { ...allBooks[index], ...requestData };
        }
    } else {
        const newId = allBooks.length > 0 ? Math.max(...allBooks.map(b => b.id)) + 1 : 1;
        allBooks.unshift({ id: newId, ...requestData, categoryName: 'General', authorName: 'Various Authors' });
    }

    alert(isEdit ? 'Book updated successfully (Demo)!' : 'Book added successfully (Demo)!');
    closeModals();
    if (document.getElementById('adminBookTableBody')) renderAdminTable(allBooks);
    else renderBooks(allBooks);
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

function renderFallbackOrEmpty(limit = null) {
    allBooks = getDemoBooks();
    renderBooks(allBooks, limit);
}
