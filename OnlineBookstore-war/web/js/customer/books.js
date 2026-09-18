/**
 * OnlineBookstore Customer Catalog & Book Detail JS Module
 */

var customerBooks = window.customerBooks || [];
var selectedReviewRating = 5;

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

    const homeSearchInput = document.getElementById('homeSearchInput');
    if (homeSearchInput) {
        let debounceTimer;
        homeSearchInput.addEventListener('input', () => {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(fetchHomeBooks, 300);
        });
    }
});

async function initHomePage() {
    await loadHomeCategories();
    fetchTopSellingHome();
    fetchHomeBooks();
}

async function loadHomeCategories() {
    const filterSelect = document.getElementById('homeCategoryFilter');
    const catalogCatSelect = document.getElementById('categoryFilter');
    if (!filterSelect && !catalogCatSelect) return;

    try {
        if (window.BookApi && BookApi.getCategories) {
            const categories = await BookApi.getCategories();
            if (Array.isArray(categories)) {
                const options = `<option value="">All Categories</option>` +
                    categories.map(c => `<option value="${c.id}">${escapeHtml(c.name)}</option>`).join('');

                if (filterSelect) filterSelect.innerHTML = options;
                if (catalogCatSelect) catalogCatSelect.innerHTML = options;
            }
        }
    } catch (e) {
        console.warn('Failed to load categories for home page:', e);
    }
}

async function fetchTopSellingHome() {
    const grid = document.getElementById('topSellingGrid');
    if (!grid) return;

    grid.innerHTML = `<div class="empty-state"><div class="empty-title">Loading top sellers...</div></div>`;

    try {
        let topBooks = [];
        if (window.BookApi && BookApi.getTopSelling) {
            topBooks = await BookApi.getTopSelling(10);
        }
        if (!Array.isArray(topBooks) || topBooks.length === 0) {
            topBooks = await BookApi.getAll();
            if (Array.isArray(topBooks)) {
                topBooks = topBooks.slice(0, 10);
            } else {
                topBooks = [];
            }
        }
        renderBookGridToElement(grid, topBooks, true);
    } catch (e) {
        console.warn('Failed to load top selling books:', e);
        grid.innerHTML = `<div class="empty-state"><div class="empty-title">Unable to load top sellers</div></div>`;
    }
}

async function fetchHomeBooks() {
    const grid = document.getElementById('homeBookGrid');
    if (!grid) return;

    grid.innerHTML = `<div class="empty-state"><div class="empty-title">Loading catalog...</div></div>`;

    const searchInput = document.getElementById('homeSearchInput');
    const categorySelect = document.getElementById('homeCategoryFilter');

    const searchQuery = searchInput ? searchInput.value.trim() : '';
    const categoryId = categorySelect ? categorySelect.value : '';

    try {
        const params = {};
        if (searchQuery) params.q = searchQuery;
        if (categoryId) params.categoryId = categoryId;

        let books = await BookApi.getAll(params);
        if (!Array.isArray(books)) books = [];

        renderBookGridToElement(grid, books, false);
    } catch (e) {
        console.warn('Failed to load home catalog:', e);
        grid.innerHTML = `<div class="empty-state"><div class="empty-title">Unable to load books</div></div>`;
    }
}

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

function renderBookGridToElement(targetElement, books, isTopSelling = false) {
    if (!targetElement) return;

    if (!books || books.length === 0) {
        targetElement.innerHTML = `
            <div class="empty-state">
                <div class="empty-title">No Books Found</div>
                <p class="empty-desc">Try searching with a different keyword or selecting another category.</p>
            </div>
        `;
        return;
    }

    targetElement.innerHTML = books.map((b, idx) => {
        const coverHTML = b.coverImage
            ? `<img src="${escapeHtml(b.coverImage)}" alt="${escapeHtml(b.title)}" class="book-cover" onerror="this.outerHTML='<div class=\\'book-cover-placeholder\\'><i class=\\'bi bi-journal-text\\' style=\\'font-size: 2.5rem;\\'></i><span>No Cover</span></div>'">`
            : `<div class="book-cover-placeholder"><i class="bi bi-journal-text" style="font-size: 2.5rem;"></i><span>No Cover</span></div>`;

        const priceHTML = b.discountPrice && b.discountPrice < b.price
            ? `<span class="book-price-old">${formatCurrency(b.price)}</span> <strong>${formatCurrency(b.discountPrice)}</strong>`
            : `<strong>${formatCurrency(b.price)}</strong>`;

        const topBadge = isTopSelling
            ? `<span style="position: absolute; top: 10px; left: 10px; background: #dc2626; color: #fff; font-size: 0.75rem; font-weight: 800; padding: 0.2rem 0.6rem; border-radius: 4px; z-index: 2; box-shadow: 0 2px 5px rgba(0,0,0,0.2);">TOP #${idx + 1}</span>`
            : '';

        return `
            <div class="book-card" style="position: relative;" onclick="openBookPreviewModal(${b.id})">
                ${topBadge}
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

function renderCustomerBookGrid(books) {
    const grid = document.getElementById('bookGrid') || document.getElementById('customerBookGrid');
    renderBookGridToElement(grid, books);
}

async function addToCartDirect(bookId, quantity = 1) {
    const role = typeof getUserRole === 'function' ? getUserRole() : (localStorage.getItem('user_role') || '');
    if (role === 'manager' || role === 'admin') {
        if (window.Toast) Toast.warning('Managers and Admins are restricted from shopping cart operations.');
        else alert('Managers and Admins are restricted from shopping cart operations.');
        return;
    }
    const token = typeof getSessionId === 'function' ? getSessionId() : (localStorage.getItem('auth_token') || localStorage.getItem('sessionId'));
    if (!token) {
        if (window.Toast) Toast.warning('Please sign in to add items to your shopping cart.');
        else alert('Please sign in to add items to your shopping cart.');
        return;
    }
    try {
        if (window.CartApi && CartApi.addItem) {
            await CartApi.addItem(bookId, quantity);
            if (window.Toast) Toast.success('Item added to Shopping Cart!');
            else alert('Item added to Shopping Cart!');
            if (typeof updateCartBadgeCount === 'function') updateCartBadgeCount();
        } else {
            console.error('CartApi is not loaded on this page');
            if (window.Toast) Toast.error('Cart system error: CartApi not loaded.');
            else alert('Cart system error: CartApi not loaded.');
        }
    } catch (err) {
        if (window.Toast) Toast.error(err.message || 'Failed to add item to cart.');
        else alert(err.message || 'Failed to add item to cart.');
    }
}

async function loadStandaloneBookDetail(bookId) {
    const container = document.getElementById('standaloneBookDetail');
    if (!container) return;

    container.innerHTML = `<div class="empty-state"><div class="empty-title">Loading book details &amp; reviews...</div></div>`;

    try {
        const book = await BookApi.getById(bookId);
        if (!book) {
            container.innerHTML = `<div class="empty-state"><div class="empty-title">Book Not Found</div></div>`;
            return;
        }

        let summary = { averageRating: 0, totalReviews: 0 };
        let reviews = [];
        let canUserReview = false;

        if (window.ReviewApi) {
            summary = await ReviewApi.getSummaryByBookId(bookId);
            reviews = await ReviewApi.getByBookId(bookId);
            if (localStorage.getItem('auth_token') || localStorage.getItem('sessionId')) {
                canUserReview = await ReviewApi.canReview(bookId);
            }
        }

        if ((!summary || !summary.totalReviews || summary.totalReviews === 0) && Array.isArray(reviews) && reviews.length > 0) {
            const count = reviews.length;
            const sum = reviews.reduce((acc, r) => acc + (Number(r.rating) || 0), 0);
            summary = {
                averageRating: Math.round((sum / count) * 10) / 10,
                totalReviews: count
            };
        }

        container.innerHTML = generateBookDetailHTML(book, summary, reviews, canUserReview);

    } catch (e) {
        console.warn('Failed to fetch book detail or reviews:', e);
        container.innerHTML = `<div class="empty-state"><div class="empty-title">Unable to load details</div></div>`;
        if (window.Toast) Toast.error('Unable to load book details: ' + (e.message || ''));
    }
}

function openStandaloneBookDetail(bookId) {
    window.location.href = getContextPath() + '/pages/customer/book-detail.xhtml?id=' + bookId;
}

async function openBookPreviewModal(bookId) {
    const modal = document.getElementById('detailModal');
    const modalBody = document.getElementById('detailModalBody');
    if (!modal || !modalBody) {
        openStandaloneBookDetail(bookId);
        return;
    }

    modalBody.innerHTML = `<div class="empty-state"><div class="empty-title">Loading book preview...</div></div>`;
    modal.classList.add('active');

    try {
        const book = await BookApi.getById(bookId);
        if (!book) {
            modalBody.innerHTML = `<div class="empty-state"><div class="empty-title">Book Not Found</div></div>`;
            return;
        }

        let summary = { averageRating: 0, totalReviews: 0 };
        if (window.ReviewApi) {
            summary = await ReviewApi.getSummaryByBookId(bookId);
            const reviews = await ReviewApi.getByBookId(bookId);
            if ((!summary || !summary.totalReviews || summary.totalReviews === 0) && Array.isArray(reviews) && reviews.length > 0) {
                const count = reviews.length;
                const sum = reviews.reduce((acc, r) => acc + (Number(r.rating) || 0), 0);
                summary = {
                    averageRating: Math.round((sum / count) * 10) / 10,
                    totalReviews: count
                };
            }
        }

        modalBody.innerHTML = generateBookModalPreviewHTML(book, summary);
    } catch (e) {
        console.warn('Failed to fetch book preview:', e);
        modalBody.innerHTML = `<div class="empty-state"><div class="empty-title">Unable to load preview</div></div>`;
    }
}

function generateBookModalPreviewHTML(book, summary) {
    const formattedPrice = formatCurrency(book.finalPrice || book.price);
    const coverHTML = book.coverImage
        ? `<img src="${escapeHtml(book.coverImage)}" alt="${escapeHtml(book.title)}" class="modal-cover" onerror="this.outerHTML='<div class=\\'book-cover-placeholder\\'><i class=\\'bi bi-journal-text\\' style=\\'font-size: 2.5rem;\\'></i><span>No Cover</span></div>'">`
        : `<div class="book-cover-placeholder"><i class="bi bi-journal-text" style="font-size: 2.5rem;"></i><span>No Cover</span></div>`;

    const avgRating = summary.averageRating || 0;
    const totalReviews = summary.totalReviews || 0;

    return `
        <div class="modal-grid" style="grid-template-columns: 220px 1fr; gap: 1.5rem;">
            <div class="modal-cover-wrap">
                ${coverHTML}
            </div>
            <div>
                <span class="book-category-badge">${escapeHtml(book.categoryName || 'General')}</span>
                <h2 class="modal-detail-title" style="font-size: 1.5rem; margin-top: 0.4rem; margin-bottom: 0.3rem;">${escapeHtml(book.title)}</h2>
                <p class="modal-detail-meta" style="margin-bottom: 0.5rem;">Author: <strong>${escapeHtml(book.authorName || 'Unknown Author')}</strong></p>
                
                <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.75rem;">
                    ${renderStarRating(avgRating)}
                    <span style="font-weight: 700;">${avgRating > 0 ? avgRating : 'N/A'}</span>
                    <span style="color: var(--text-muted); font-size: 0.85rem;">(${totalReviews} reviews)</span>
                </div>

                <div class="modal-detail-price" style="font-size: 1.4rem; font-weight: 800; margin-bottom: 0.75rem;">${formattedPrice}</div>
                
                <p class="modal-detail-desc" style="font-size: 0.9rem; line-height: 1.5; margin-bottom: 1rem;">${escapeHtml(book.description || 'No detailed description available for this book.')}</p>
                
                <table class="specs-table" style="margin-bottom: 1.25rem;">
                    <tr>
                        <td style="font-weight: 600; color: var(--text-muted); width: 130px;">ISBN Code:</td>
                        <td><code>${escapeHtml(book.isbn || 'N/A')}</code></td>
                    </tr>
                    <tr>
                        <td style="font-weight: 600; color: var(--text-muted);">Language:</td>
                        <td>${escapeHtml(book.language || 'Tiếng Việt')}</td>
                    </tr>
                    <tr>
                        <td style="font-weight: 600; color: var(--text-muted);">Page:</td>
                        <td>${escapeHtml(book.pages || '0')}</td>
                    </tr>
                    <tr>
                        <td style="font-weight: 600; color: var(--text-muted);">Stock Status:</td>
                        <td>${book.stockQuantity > 0 ? `<span style="color: #166534; font-weight: 700;">In Stock (${book.stockQuantity})</span>` : '<span style="color: #991b1b; font-weight: 700;">Out of Stock</span>'}</td>
                    </tr>
                </table>

                <div style="display: flex; gap: 0.75rem; flex-wrap: wrap; margin-top: 1rem;">
                    <button class="btn btn-primary" onclick="closeModals(); openStandaloneBookDetail(${book.id});">
                        <i class="bi bi-box-arrow-up-right" style="margin-right: 0.4rem;"></i> Full Details &amp; Reviews Page
                    </button>
                    <button class="btn btn-secondary" onclick="addToCartDirect(${book.id})">
                        <i class="bi bi-cart-plus" style="margin-right: 0.4rem;"></i> Add to Cart
                    </button>
                </div>
            </div>
        </div>
    `;
}

function renderStarRating(rating) {
    const num = Math.round((rating || 0) * 2) / 2;
    let starsHTML = '';
    for (let i = 1; i <= 5; i++) {
        if (i <= num) {
            starsHTML += `<i class="bi bi-star-fill" style="color: #f59e0b; margin-right: 2px;"></i>`;
        } else if (i - 0.5 === num) {
            starsHTML += `<i class="bi bi-star-half" style="color: #f59e0b; margin-right: 2px;"></i>`;
        } else {
            starsHTML += `<i class="bi bi-star" style="color: #d1d5db; margin-right: 2px;"></i>`;
        }
    }
    return starsHTML;
}

function setReviewRating(stars) {
    selectedReviewRating = stars;
    for (let i = 1; i <= 5; i++) {
        const starEl = document.getElementById(`starInput_${i}`);
        if (starEl) {
            if (i <= stars) {
                starEl.className = 'bi bi-star-fill';
                starEl.style.color = '#f59e0b';
            } else {
                starEl.className = 'bi bi-star';
                starEl.style.color = '#d1d5db';
            }
        }
    }
}

async function submitBookReview(bookId) {
    const isLoggedIn = !!localStorage.getItem('auth_token');
    if (!isLoggedIn) {
        if (window.Toast) Toast.warning('Please sign in to submit a rating and review.');
        else alert('Please sign in to submit a review.');
        return;
    }

    const commentInput = document.getElementById('reviewCommentInput');
    const comment = commentInput ? commentInput.value.trim() : '';

    if (!comment) {
        if (window.Toast) Toast.warning('Please enter your review comment.');
        return;
    }

    try {
        await ReviewApi.createReview({
            bookId: parseInt(bookId),
            rating: selectedReviewRating,
            comment: comment
        });

        if (window.Toast) Toast.success('Thank you! Your review has been submitted.');
        if (commentInput) commentInput.value = '';
        loadStandaloneBookDetail(bookId);
    } catch (e) {
        if (window.Toast) Toast.error('Failed to submit review: ' + (e.message || ''));
    }
}

function generateBookDetailHTML(book, summary, reviews, canUserReview = false) {
    const formattedPrice = formatCurrency(book.finalPrice || book.price);
    const coverHTML = book.coverImage
        ? `<img src="${escapeHtml(book.coverImage)}" alt="${escapeHtml(book.title)}" class="modal-cover" onerror="this.outerHTML='<div class=\\'book-cover-placeholder\\'><i class=\\'bi bi-journal-text\\' style=\\'font-size: 2.5rem;\\'></i><span>No Cover</span></div>'">`
        : `<div class="book-cover-placeholder"><i class="bi bi-journal-text" style="font-size: 2.5rem;"></i><span>No Cover</span></div>`;

    const avgRating = summary.averageRating || 0;
    const totalReviews = summary.totalReviews || 0;

    let reviewsListHTML = '';
    if (!reviews || reviews.length === 0) {
        reviewsListHTML = `<div style="padding: 1.5rem; text-align: center; color: var(--text-muted); background: var(--bg-surface, #fafafa); border-radius: 8px;">No reviews yet for this book. Be the first to share your thoughts!</div>`;
    } else {
        reviewsListHTML = reviews.map(r => `
            <div style="padding: 1.25rem 0; border-bottom: 1px solid var(--border-color, #e2e8f0);">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.4rem;">
                    <div style="display: flex; align-items: center; gap: 0.6rem;">
                        <div style="width: 36px; height: 36px; border-radius: 50%; background: #09090b; color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 0.9rem;">
                            ${escapeHtml((r.userFullName || r.username || 'U').charAt(0).toUpperCase())}
                        </div>
                        <div>
                            <strong style="font-size: 0.95rem;">${escapeHtml(r.userFullName || r.username || 'Anonymous User')}</strong>
                            <div style="font-size: 0.8rem; color: var(--text-muted);">${r.createdAt ? new Date(r.createdAt).toLocaleDateString() : ''}</div>
                        </div>
                    </div>
                    <div>
                        ${renderStarRating(r.rating)}
                    </div>
                </div>
                <p style="margin: 0.5rem 0 0 0; font-size: 0.9rem; color: var(--text-main, #1e293b); line-height: 1.5;">${escapeHtml(r.comment || '')}</p>
            </div>
        `).join('');
    }

    const token = localStorage.getItem('auth_token') || localStorage.getItem('sessionId');
    const isLoggedIn = !!token;
    let reviewFormHTML = '';

    if (!isLoggedIn) {
        reviewFormHTML = `
            <div style="margin-top: 2rem; background: var(--bg-surface, #f8fafc); padding: 1.5rem; border-radius: 8px; border: 1px solid var(--border-color, #e2e8f0);">
                <h3 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 0.75rem;">Write a Rating &amp; Review</h3>
                <p style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 1rem;">You must be logged in to leave a review.</p>
                <a href="${getContextPath()}/pages/auth/login.xhtml" class="btn btn-secondary btn-sm">
                    <i class="bi bi-box-arrow-in-right"></i> Sign In to Review
                </a>
            </div>
        `;
    } else if (!canUserReview) {
        reviewFormHTML = `
            <div style="margin-top: 2rem; background: var(--bg-surface, #f8fafc); padding: 1.5rem; border-radius: 8px; border: 1px solid var(--border-color, #e2e8f0);">
                <h3 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 0.75rem;">Write a Rating &amp; Review</h3>
                <div style="padding: 0.85rem 1rem; border-radius: 6px; font-size: 0.9rem; background: #eff6ff; border: 1px solid #bfdbfe; color: #1e40af; font-weight: 600;">
                    <i class="bi bi-info-circle" style="margin-right: 0.4rem;"></i> Only users who have purchased this product may write a review.
                </div>
            </div>
        `;
    } else {
        reviewFormHTML = `
            <div style="margin-top: 2rem; background: var(--bg-surface, #f8fafc); padding: 1.5rem; border-radius: 8px; border: 1px solid var(--border-color, #e2e8f0);">
                <h3 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 0.75rem;">Write a Rating &amp; Review</h3>
                <div style="margin-bottom: 1rem;">
                    <label style="display: block; font-size: 0.85rem; font-weight: 600; margin-bottom: 0.4rem;">Your Rating *</label>
                    <div style="font-size: 1.5rem; cursor: pointer; display: flex; gap: 0.25rem;">
                        <i id="starInput_1" class="bi bi-star-fill" style="color: #f59e0b;" onclick="setReviewRating(1)"></i>
                        <i id="starInput_2" class="bi bi-star-fill" style="color: #f59e0b;" onclick="setReviewRating(2)"></i>
                        <i id="starInput_3" class="bi bi-star-fill" style="color: #f59e0b;" onclick="setReviewRating(3)"></i>
                        <i id="starInput_4" class="bi bi-star-fill" style="color: #f59e0b;" onclick="setReviewRating(4)"></i>
                        <i id="starInput_5" class="bi bi-star-fill" style="color: #f59e0b;" onclick="setReviewRating(5)"></i>
                    </div>
                </div>
                <div style="margin-bottom: 1rem;">
                    <label style="display: block; font-size: 0.85rem; font-weight: 600; margin-bottom: 0.4rem;">Your Comment *</label>
                    <textarea id="reviewCommentInput" class="input-field" rows="3" placeholder="What did you think of this book?" style="width: 100%;"></textarea>
                </div>
                <button class="btn btn-primary" onclick="submitBookReview(${book.id})">
                    <i class="bi bi-send" style="margin-right: 0.4rem;"></i> Submit Review
                </button>
            </div>
        `;
    }

    return `
        <div class="modal-grid" style="grid-template-columns: 280px 1fr; gap: 2rem;">
            <div class="modal-cover-wrap">
                ${coverHTML}
            </div>
            <div>
                <span class="book-category-badge">${escapeHtml(book.categoryName || 'General')}</span>
                <h1 class="modal-detail-title" style="font-size: 1.8rem; margin-top: 0.5rem; margin-bottom: 0.4rem;">${escapeHtml(book.title)}</h1>
                <p class="modal-detail-meta" style="font-size: 1rem; margin-bottom: 0.75rem;">Author: <strong>${escapeHtml(book.authorName || 'Unknown Author')}</strong></p>
                
                <!-- Rating Summary Badge -->
                <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 1rem;">
                    ${renderStarRating(avgRating)}
                    <span style="font-weight: 700; font-size: 1.05rem;">${avgRating > 0 ? avgRating : 'N/A'}</span>
                    <span style="color: var(--text-muted); font-size: 0.85rem;">(${totalReviews} ${totalReviews === 1 ? 'review' : 'reviews'})</span>
                </div>

                <div class="modal-detail-price" style="font-size: 1.6rem; font-weight: 800; margin-bottom: 1.25rem;">${formattedPrice}</div>
                
                <p class="modal-detail-desc" style="line-height: 1.6; font-size: 0.95rem; margin-bottom: 1.5rem;">${escapeHtml(book.description || 'No detailed description available for this book.')}</p>
                
                <table class="specs-table" style="margin-bottom: 1.5rem; width: 100%;">
                    <tr>
                        <td style="padding: 0.4rem 0; width: 140px; color: var(--text-muted); font-weight: 600;">ISBN Code:</td>
                        <td style="padding: 0.4rem 0;"><code>${escapeHtml(book.isbn || 'N/A')}</code></td>
                    </tr>
                    <tr>
                        <td style="padding: 0.4rem 0; color: var(--text-muted); font-weight: 600;">Published Year:</td>
                        <td style="padding: 0.4rem 0;">${book.publishedYear || 'N/A'}</td>
                    </tr>
                    <tr>
                        <td style="padding: 0.4rem 0; color: var(--text-muted); font-weight: 600;">Page Count:</td>
                        <td style="padding: 0.4rem 0;">${book.pages || 'N/A'} pages</td>
                    </tr>
                    <tr>
                        <td style="padding: 0.4rem 0; color: var(--text-muted); font-weight: 600;">Language:</td>
                        <td style="padding: 0.4rem 0;">${escapeHtml(book.language || 'Tiếng Việt')}</td>
                    </tr>
                    <tr>
                        <td style="padding: 0.4rem 0; color: var(--text-muted); font-weight: 600;">Stock Status:</td>
                        <td style="padding: 0.4rem 0;">${book.stockQuantity > 0 ? `<span style="color: #166534; font-weight: 700;">In Stock (${book.stockQuantity})</span>` : '<span style="color: #991b1b; font-weight: 700;">Out of Stock</span>'}</td>
                    </tr>
                </table>

                <button class="btn btn-primary btn-lg" style="padding: 0.85rem 2rem; font-weight: 700;" onclick="addToCartDirect(${book.id})">
                    <i class="bi bi-cart-plus" style="margin-right: 0.4rem;"></i> Add to Cart
                </button>
            </div>
        </div>

        <!-- Reviews Section -->
        <div style="margin-top: 3rem; border-top: 1px solid var(--border-color, #e2e8f0); padding-top: 2rem;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
                <h2 style="font-size: 1.4rem; font-weight: 800; margin: 0;">Customer Reviews &amp; Ratings</h2>
                <div style="display: flex; align-items: center; gap: 0.5rem;">
                    ${renderStarRating(avgRating)}
                    <span style="font-weight: 700;">${avgRating > 0 ? avgRating : '0'} / 5</span>
                </div>
            </div>

            <!-- Review Comments List -->
            <div>
                ${reviewsListHTML}
            </div>

            <!-- Submit Review Form -->
            ${reviewFormHTML}
        </div>
    `;
}

async function fetchCustomerBooks(params = {}) {
    return fetchBooks(params.q || '', params.categoryId || '');
}

window.initHomePage = initHomePage;
window.fetchHomeBooks = fetchHomeBooks;
window.fetchTopSellingHome = fetchTopSellingHome;
window.fetchBooks = fetchBooks;
window.fetchCustomerBooks = fetchCustomerBooks;
window.renderCustomerBookGrid = renderCustomerBookGrid;
window.addToCartDirect = addToCartDirect;
window.openStandaloneBookDetail = openStandaloneBookDetail;
window.loadStandaloneBookDetail = loadStandaloneBookDetail;
window.setReviewRating = setReviewRating;
window.submitBookReview = submitBookReview;
window.openBookPreviewModal = openBookPreviewModal;
