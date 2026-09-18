/**
 * OnlineBookstore Customer Catalog & Book Detail JS Module
 */

var customerBooks = window.customerBooks || [];

document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        let debounceTimer;
        searchInput.addEventListener('input', (e) => {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => {
                const categorySelect = document.getElementById('categoryFilter');
                const catVal = categorySelect ? categorySelect.value : '';
                fetchBooks(e.target.value.trim(), catVal);
            }, 300);
        });
    }
});

async function fetchBooks(searchQuery, secondParam) {
    const grid = document.getElementById('bookGrid') || document.getElementById('customerBookGrid');
    if (!grid) return;

    grid.innerHTML = `<div class="empty-state"><div class="empty-title">Loading catalog...</div></div>`;

    const searchInput = document.getElementById('searchInput');
    const categorySelect = document.getElementById('categoryFilter');

    if (searchQuery === undefined && searchInput) {
        searchQuery = searchInput.value.trim();
    }
    if (secondParam === undefined && categorySelect) {
        secondParam = categorySelect.value;
    }

    try {
        const params = {};
        if (typeof searchQuery === 'string' && searchQuery.trim()) {
            params.q = searchQuery.trim();
        }

        let limit = null;
        if (typeof secondParam === 'number') {
            limit = secondParam;
        } else if (secondParam) {
            params.categoryId = secondParam;
        }

        let books = await BookApi.getAll(params);
        if (!Array.isArray(books)) books = [];

        if (limit && limit > 0) {
            books = books.slice(0, limit);
        }

        customerBooks = books;
        renderCustomerBookGrid(books);
    } catch (e) {
        console.warn('Failed to load books from API:', e);
        renderCustomerBookGrid([]);
        if (window.Toast) Toast.error('Failed to load catalog: ' + (e.message || ''));
    }
}

async function fetchCustomerBooks(params = {}) {
    return fetchBooks(params.q || '', params.categoryId || '');
}

function renderCustomerBookGrid(books) {
    const grid = document.getElementById('bookGrid') || document.getElementById('customerBookGrid');
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
            <div class="book-card" onclick="openStandaloneBookDetail(${b.id})">
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

function addToCartDirect(bookId) {
    const role = typeof getUserRole === 'function' ? getUserRole() : (localStorage.getItem('user_role') || '');
    if (role === 'manager' || role === 'admin') {
        if (window.Toast) Toast.warning('Managers and Admins are restricted from shopping cart operations.');
        return;
    }
    if (window.Toast) {
        Toast.success('Item added to Shopping Cart!');
    } else {
        alert('Item added to Shopping Cart!');
    }
}

async function loadStandaloneBookDetail(bookId) {
    const container = document.getElementById('standaloneBookDetail');
    if (!container) return;

    container.innerHTML = `<div class="empty-state"><div class="empty-title">Loading book details...</div></div>`;

    try {
        const book = await BookApi.getById(bookId);
        if (book) {
            container.innerHTML = generateBookDetailHTML(book);
        } else {
            container.innerHTML = `<div class="empty-state"><div class="empty-title">Book Not Found</div></div>`;
        }
    } catch (e) {
        console.warn('Failed to fetch book detail:', e);
        container.innerHTML = `<div class="empty-state"><div class="empty-title">Unable to load details</div></div>`;
        if (window.Toast) Toast.error('Unable to load book details: ' + (e.message || ''));
    }
}

function openStandaloneBookDetail(bookId) {
    window.location.href = getContextPath() + '/pages/customer/book-detail.xhtml?id=' + bookId;
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

window.fetchBooks = fetchBooks;
window.fetchCustomerBooks = fetchCustomerBooks;
window.renderCustomerBookGrid = renderCustomerBookGrid;
window.addToCartDirect = addToCartDirect;
window.openStandaloneBookDetail = openStandaloneBookDetail;
window.loadStandaloneBookDetail = loadStandaloneBookDetail;
