/**
 * OnlineBookstore Client Application JS
 * Minimalist Monochrome UI Theme (Bootstrap Icons & Role-Based Access Control)
 */

var allBooks = window.allBooks || [];

document.addEventListener('DOMContentLoaded', () => {
    updateRoleBadgeUI();
    setupEventListeners();
});

/* Role & Security Helper Methods */
function getUserRole() {
    if (typeof getUserInfo === 'function') {
        const user = getUserInfo();
        if (user && user.role) return user.role.toLowerCase();
    }
    return localStorage.getItem('user_role') || '';
}

function updateRoleBadgeUI() {
    if (typeof updateAuthHeaderUI === 'function') {
        updateAuthHeaderUI();
    }
}

function setupEventListeners() {
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

    const categorySelect = document.getElementById('categorySelect');
    if (categorySelect) {
        categorySelect.addEventListener('change', (e) => {
            const categoryId = e.target.value;
            fetchBooks('', categoryId);
        });
    }

    const addBookForm = document.getElementById('addBookForm');
    if (addBookForm) {
        addBookForm.addEventListener('submit', handleAddBookSubmit);
    }
}

async function fetchBooks(searchQuery = '', categoryId = '') {
    const grid = document.getElementById('bookGrid');
    if (!grid) return;

    grid.innerHTML = `<div class="empty-state"><div class="empty-title">Loading catalog...</div></div>`;

    try {
        let url = `${API_BASE_URL}`;
        const params = new URLSearchParams();
        if (searchQuery) params.append('q', searchQuery);
        if (categoryId) params.append('categoryId', categoryId);
        if (params.toString()) url += `?${params.toString()}`;

        const response = await fetch(url);
        if (response.ok) {
            const data = await response.json();
            allBooks = data.data || [];
            renderBookGrid(allBooks);
        } else {
            console.warn('API fetch failed, utilizing fallback dataset');
            allBooks = getDemoBooks();
            renderBookGrid(allBooks);
        }
    } catch (e) {
        console.warn('API error, using demo catalog dataset:', e);
        allBooks = getDemoBooks();
        renderBookGrid(allBooks);
    }
}

function renderBookGrid(books) {
    const grid = document.getElementById('bookGrid');
    if (!grid) return;

    if (!books || books.length === 0) {
        grid.innerHTML = `
            <div class="empty-state">
                <div class="empty-title">No Books Found</div>
                <p class="empty-desc">Try clearing filters or search with a different keyword.</p>
            </div>
        `;
        return;
    }

    grid.innerHTML = books.map(b => {
        const coverHTML = b.coverImage
            ? `<img src="${escapeHtml(b.coverImage)}" alt="${escapeHtml(b.title)}" class="book-cover" onerror="this.outerHTML='<div class=\\'book-cover-placeholder\\'><i class=\\'bi bi-journal-text\\' style=\\'font-size: 2.5rem;\\'></i><span>No Cover</span></div>'">`
            : `<div class="book-cover-placeholder"><i class="bi bi-journal-text" style="font-size: 2.5rem;"></i><span>No Cover</span></div>`;

        const priceHTML = b.discountPrice && b.discountPrice < b.price
            ? `<span class="book-price-old">${formatCurrency(b.price)}</span> <strong>${formatCurrency(b.discountPrice)}</strong>`
            : `<strong>${formatCurrency(b.price)}</strong>`;

        return `
            <div class="book-card" onclick="openBookModal(${b.id})">
                <div class="book-cover-wrap">
                    ${coverHTML}
                </div>
                <div class="book-card-body">
                    <span class="book-category-badge">${escapeHtml(b.categoryName || 'General')}</span>
                    <h3 class="book-title">${escapeHtml(b.title)}</h3>
                    <p class="book-author">By ${escapeHtml(b.authorName || 'Unknown Author')}</p>
                    <div class="book-footer">
                        <div class="book-price">${priceHTML}</div>
                        <button class="btn btn-secondary btn-sm" onclick="event.stopPropagation(); addToCartDirect(${b.id})">
                            <i class="bi bi-cart-plus"></i> Add
                        </button>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

async function openBookModal(bookId) {
    let book = allBooks.find(b => b.id === bookId);
    if (!book) {
        try {
            const response = await fetch(`${API_BASE_URL}/${bookId}`);
            if (response.ok) {
                const data = await response.json();
                book = data.data;
            }
        } catch (e) {
            console.warn('Could not fetch book details from API');
        }
    }

    if (!book) return;

    const modalContent = document.getElementById('bookDetailContent');
    const modal = document.getElementById('bookDetailModal');
    if (!modalContent || !modal) return;

    modalContent.innerHTML = generateBookDetailHTML(book);
    modal.classList.add('active');
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

                <button class="btn btn-primary" style="width: 100%;" onclick="addToCartDirect(${book.id})">
                    <i class="bi bi-cart-plus" style="margin-right: 0.4rem;"></i> Add to Cart
                </button>
            </div>
        </div>
    `;
}

function getDemoBooks() {
    return [
        {
            id: 1,
            title: "Clean Architecture: A Craftsman's Guide",
            authorName: "Robert C. Martin",
            categoryName: "Software Engineering",
            price: 45.00,
            isbn: "978-0134494166",
            stockQuantity: 15,
            publishedYear: 2017,
            pages: 432,
            language: "English",
            description: "Practical software architecture solutions for professional software developers."
        },
        {
            id: 2,
            title: "Design Patterns: Elements of Reusable Object-Oriented Software",
            authorName: "Erich Gamma et al.",
            categoryName: "Computer Science",
            price: 54.99,
            isbn: "978-0201633610",
            stockQuantity: 8,
            publishedYear: 1994,
            pages: 416,
            language: "English",
            description: "Captures a wealth of experience in designing object-oriented software."
        },
        {
            id: 3,
            title: "System Design Interview – An Insider's Guide",
            authorName: "Alex Xu",
            categoryName: "Architecture",
            price: 38.50,
            isbn: "978-1736049112",
            stockQuantity: 20,
            publishedYear: 2020,
            pages: 320,
            language: "English",
            description: "Step-by-step framework to master system design interviews."
        }
    ];
}
