/**
 * OnlineBookstore Admin Books Module JS
 * Inventory Table Management & CRUD Modal Submissions
 */

var adminBooks = window.adminBooks || [];

document.addEventListener('DOMContentLoaded', () => {
    const addBookForm = document.getElementById('addBookForm');
    if (addBookForm) {
        addBookForm.addEventListener('submit', handleAddBookSubmit);
    }
});

async function fetchAdminBookTable() {
    const tbody = document.getElementById('adminBookTableBody');
    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; padding: 2rem;">Loading inventory data...</td></tr>`;

    try {
        adminBooks = await BookApi.getAll();
        renderAdminTable(adminBooks);
    } catch (e) {
        console.warn('Failed to fetch admin inventory:', e);
        renderAdminTable([]);
        if (window.Toast) Toast.error('Failed to load inventory data: ' + (e.message || ''));
    }
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

    const requestData = {
        title: form.title.value,
        isbn: form.isbn.value,
        price: parseFloat(form.price.value) || 0,
        discountPrice: form.discountPrice.value ? parseFloat(form.discountPrice.value) : null,
        stockQuantity: parseInt(form.stockQuantity.value) || 0,
        description: form.description.value,
        coverImage: form.coverImage.value,
        publishedYear: parseInt(form.publishedYear.value) || 2024,
        pages: form.pages.value ? parseInt(form.pages.value) : null,
        language: form.language.value || 'English',
        active: true
    };

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
