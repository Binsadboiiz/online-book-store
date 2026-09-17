/**
 * OnlineBookstore Customer Books Module JS
 * Catalog Browsing, Live Search & Book Details View
 */

let allBooks = [];

document.addEventListener('DOMContentLoaded', () => {
    setupSearchInputEvent();
});

function setupSearchInputEvent() {
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
}

async function fetchBooks(keyword = '', limit = null) {
    const grid = document.getElementById('bookGrid');
    if (!grid) return;

    grid.innerHTML = `<div class="empty-state"><div class="empty-title"><i class="bi bi-arrow-repeat spin"></i> Loading books...</div></div>`;

    try {
        allBooks = await BookApi.getAll({ q: keyword });
        renderBooks(allBooks, limit);
    } catch (error) {
        console.error('Error fetching books:', error);
        allBooks = getDemoBooks();
        renderBooks(allBooks, limit);
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

    const book = await BookApi.getById(bookId);
    if (book) {
        container.innerHTML = generateBookDetailHTML(book);
    } else {
        container.innerHTML = `<div class="empty-state"><div class="empty-title">Book Not Found</div></div>`;
    }
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
