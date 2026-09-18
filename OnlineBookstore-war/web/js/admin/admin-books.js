/**
 * OnlineBookstore Admin Books Module JS
 * Inventory Table Management & CRUD Modal Submissions
 */

var adminBooks = window.adminBooks || [];
var adminAuthors = [];
var adminCategories = [];

document.addEventListener('DOMContentLoaded', () => {
    const addBookForm = document.getElementById('addBookForm');
    if (addBookForm) {
        addBookForm.addEventListener('submit', handleAddBookSubmit);
    }

    const searchInput = document.getElementById('adminBookSearch');
    if (searchInput) {
        let debounceTimer;
        searchInput.addEventListener('input', () => {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(applyAdminBookFilters, 300);
        });
    }

    const catFilter = document.getElementById('adminBookCategoryFilter');
    if (catFilter) {
        catFilter.addEventListener('change', applyAdminBookFilters);
    }

    const stockFilter = document.getElementById('adminBookStockFilter');
    if (stockFilter) {
        stockFilter.addEventListener('change', applyAdminBookFilters);
    }
});

async function fetchAdminBookTable() {
    const tbody = document.getElementById('adminBookTableBody');
    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; padding: 2rem;">Loading inventory data...</td></tr>`;

    try {
        adminBooks = await BookApi.getAll();
        await loadAuthorsAndCategories();
        applyAdminBookFilters();
    } catch (e) {
        console.warn('Failed to fetch admin inventory:', e);
        renderAdminTable([]);
        if (window.Toast) Toast.error('Failed to load inventory data: ' + (e.message || ''));
    }
}

async function loadAuthorsAndCategories() {
    try {
        if (window.AuthorApi) {
            adminAuthors = await AuthorApi.getAll();
            populateAuthorDropdown(adminAuthors);
        }
        if (window.BookApi && BookApi.getCategories) {
            adminCategories = await BookApi.getCategories();
            populateCategoryDropdowns(adminCategories);
        }
    } catch (e) {
        console.warn('Error loading authors or categories:', e);
    }
}

function populateAuthorDropdown(authors) {
    const select = document.getElementById('authorSelect');
    if (!select) return;

    let html = `<option value="">-- Select Author --</option>`;
    if (Array.isArray(authors)) {
        html += authors.map(a => `<option value="${a.id}">${escapeHtml(a.name)}</option>`).join('');
    }
    select.innerHTML = html;
}

function populateCategoryDropdowns(categories) {
    const filterSelect = document.getElementById('adminBookCategoryFilter');
    const formSelect = document.getElementById('bookCategorySelect');

    if (filterSelect) {
        let html = `<option value="">All Categories</option>`;
        if (Array.isArray(categories)) {
            html += categories.map(c => `<option value="${c.id}">${escapeHtml(c.name)}</option>`).join('');
        }
        filterSelect.innerHTML = html;
    }

    if (formSelect) {
        let html = `<option value="">-- Select Category --</option>`;
        if (Array.isArray(categories)) {
            html += categories.map(c => `<option value="${c.id}">${escapeHtml(c.name)}</option>`).join('');
        }
        formSelect.innerHTML = html;
    }
}

function applyAdminBookFilters() {
    const searchQuery = (document.getElementById('adminBookSearch')?.value || '').trim().toLowerCase();
    const catVal = document.getElementById('adminBookCategoryFilter')?.value || '';
    const stockVal = document.getElementById('adminBookStockFilter')?.value || '';

    let filtered = adminBooks.filter(b => {
        if (searchQuery) {
            const matchTitle = (b.title || '').toLowerCase().includes(searchQuery);
            const matchIsbn = (b.isbn || '').toLowerCase().includes(searchQuery);
            const matchAuthor = (b.authorName || '').toLowerCase().includes(searchQuery);
            if (!matchTitle && !matchIsbn && !matchAuthor) return false;
        }

        if (catVal) {
            if (String(b.categoryId) !== String(catVal)) return false;
        }

        if (stockVal === 'INSTOCK') {
            if (b.stockQuantity <= 0) return false;
        } else if (stockVal === 'OUTOFSTOCK') {
            if (b.stockQuantity > 0) return false;
        }

        return true;
    });

    renderAdminTable(filtered);
}

function renderAdminTable(books) {
    const tbody = document.getElementById('adminBookTableBody');
    if (!tbody) return;

    if (!books || books.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; padding: 2rem;">No books in inventory matching filter criteria.</td></tr>`;
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
                <div class="action-btns">
                    <button class="btn btn-secondary btn-sm" onclick="editBookAdmin(${b.id})">
                        <i class="bi bi-pencil"></i> Edit
                    </button>
                    <button class="btn btn-danger btn-sm" onclick="deleteBookAdmin(${b.id})">
                        <i class="bi bi-trash"></i> Delete
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

function toggleAuthorMode(mode) {
    const existingSec = document.getElementById('existingAuthorSection');
    const newSec = document.getElementById('newAuthorSection');
    const newNameInput = document.getElementById('newAuthorNameInput');

    if (mode === 'NEW') {
        if (existingSec) existingSec.style.display = 'none';
        if (newSec) newSec.style.display = 'block';
        if (newNameInput) newNameInput.required = true;
    } else {
        if (existingSec) existingSec.style.display = 'block';
        if (newSec) newSec.style.display = 'none';
        if (newNameInput) {
            newNameInput.required = false;
            newNameInput.value = '';
        }
    }
}

function openAddBookModal(editBookId = null) {
    const role = getUserRole();
    if (role !== 'manager' && role !== 'admin') {
        if (window.Toast) Toast.warning('Access Restricted: Only Manager/Admin users can perform book management.');
        return;
    }

    const modal = document.getElementById('addBookModal');
    const form = document.getElementById('addBookForm');
    const titleEl = document.getElementById('modalFormTitle');
    
    if (!modal || !form) return;

    form.reset();
    document.getElementById('bookIdInput').value = '';

    // Reset author mode radios
    const radios = form.querySelectorAll('input[name="authorMode"]');
    radios.forEach(r => {
        if (r.value === 'EXISTING') r.checked = true;
    });
    toggleAuthorMode('EXISTING');

    if (editBookId) {
        const book = adminBooks.find(b => b.id === editBookId);
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
            form.language.value = book.language || 'Tiếng Việt';

            if (book.authorId && document.getElementById('authorSelect')) {
                document.getElementById('authorSelect').value = book.authorId;
            }
            if (book.categoryId && document.getElementById('bookCategorySelect')) {
                document.getElementById('bookCategorySelect').value = book.categoryId;
            }
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
    const role = getUserRole();
    if (role !== 'manager' && role !== 'admin') {
        if (window.Toast) Toast.warning('Access Restricted: Only Manager or Admin users can delete books.');
        return;
    }

    if (!confirm(`Are you sure you want to delete Book ID #${id}?`)) {
        return;
    }

    try {
        await BookApi.delete(id);
        if (window.Toast) Toast.success('Book deleted successfully!');
        fetchAdminBookTable();
    } catch (e) {
        if (window.Toast) Toast.error('Failed to delete book: ' + (e.message || ''));
    }
}

async function handleAddBookSubmit(e) {
    e.preventDefault();
    const role = getUserRole();
    if (role !== 'manager' && role !== 'admin') {
        if (window.Toast) Toast.warning('Access Restricted: Only Manager or Admin users can create or modify books.');
        return;
    }

    const form = e.target;
    const bookId = document.getElementById('bookIdInput').value;

    const authorMode = form.querySelector('input[name="authorMode"]:checked')?.value || 'EXISTING';

    const requestData = {
        title: form.title.value.trim(),
        isbn: form.isbn.value.trim(),
        price: parseFloat(form.price.value) || 0,
        discountPrice: form.discountPrice.value ? parseFloat(form.discountPrice.value) : null,
        stockQuantity: parseInt(form.stockQuantity.value) || 0,
        description: form.description.value.trim(),
        coverImage: form.coverImage.value.trim(),
        publishedYear: parseInt(form.publishedYear.value) || 2024,
        pages: form.pages.value ? parseInt(form.pages.value) : null,
        language: form.language.value.trim() || 'Tiếng Việt',
        active: true
    };

    if (form.categoryId.value) {
        requestData.categoryId = parseInt(form.categoryId.value);
    }

    if (authorMode === 'NEW') {
        const newName = document.getElementById('newAuthorNameInput')?.value.trim();
        if (!newName) {
            if (window.Toast) Toast.warning('Please enter the new author full name.');
            return;
        }
        requestData.newAuthorName = newName;
        requestData.newAuthorBio = document.getElementById('newAuthorBioInput')?.value.trim() || '';
    } else {
        if (form.authorId.value) {
            requestData.authorId = parseInt(form.authorId.value);
        }
    }

    try {
        const isEdit = !!bookId;
        if (isEdit) {
            await BookApi.update(bookId, requestData);
        } else {
            await BookApi.create(requestData);
        }
        if (window.Toast) Toast.success(isEdit ? 'Book updated successfully!' : 'Book created successfully!');
        closeModals();
        fetchAdminBookTable();
    } catch (err) {
        if (window.Toast) Toast.error('Save failed: ' + (err.message || 'Unknown error'));
    }
}

window.toggleAuthorMode = toggleAuthorMode;
window.fetchAdminBookTable = fetchAdminBookTable;
window.openAddBookModal = openAddBookModal;
window.editBookAdmin = editBookAdmin;
window.deleteBookAdmin = deleteBookAdmin;
