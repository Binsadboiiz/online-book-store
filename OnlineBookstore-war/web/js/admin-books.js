/**
 * OnlineBookstore Admin Books Module JS
 * Inventory Table Management & CRUD Modal Submissions
 */

let adminBooks = [];

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
        const baseUrl = typeof getApiBaseUrl === 'function' ? getApiBaseUrl() : (typeof API_BASE_URL !== 'undefined' ? API_BASE_URL : '/OnlineBookstore-war/api/books');
        let response = await fetch(baseUrl);

        if (response.ok) {
            const data = await response.json();
            adminBooks = data.data || [];
            renderAdminTable(adminBooks);
            return;
        }
    } catch (e) {
        console.warn('API offline, rendering demo admin table:', e);
    }

    adminBooks = getDemoBooks();
    renderAdminTable(adminBooks);
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
    if (getUserRole() !== 'admin') {
        alert('Access Restricted: Only Admin users can delete books.');
        return;
    }

    if (!confirm('Are you sure you want to delete book #' + id + '?')) return;

    try {
        const baseUrl = typeof getApiBaseUrl === 'function' ? getApiBaseUrl() : (typeof API_BASE_URL !== 'undefined' ? API_BASE_URL : '/OnlineBookstore-war/api/books');
        let response = await fetch(`${baseUrl}/${id}`, { method: 'DELETE' });
        if (response.ok) {
            alert('Book deleted successfully!');
            fetchAdminBookTable();
            return;
        }
    } catch (err) {
        console.error('Delete failed:', err);
    }

    adminBooks = adminBooks.filter(b => b.id !== id);
    renderAdminTable(adminBooks);
    alert('Book removed from local inventory view!');
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
    const baseUrl = typeof getApiBaseUrl === 'function' ? getApiBaseUrl() : (typeof API_BASE_URL !== 'undefined' ? API_BASE_URL : '/OnlineBookstore-war/api/books');
    const endpoint = isEdit ? `${baseUrl}/${bookId}` : baseUrl;
    const method = isEdit ? 'PUT' : 'POST';

    try {
        let response = await fetch(endpoint, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestData)
        });

        if (response.ok) {
            alert(isEdit ? 'Book updated successfully!' : 'Book created successfully!');
            form.reset();
            closeModals();
            fetchAdminBookTable();
            return;
        }
    } catch (err) {
        console.error('API Error:', err);
    }

    if (isEdit) {
        const index = adminBooks.findIndex(b => b.id == bookId);
        if (index !== -1) {
            adminBooks[index] = { ...adminBooks[index], ...requestData };
        }
    } else {
        const newId = adminBooks.length > 0 ? Math.max(...adminBooks.map(b => b.id)) + 1 : 1;
        adminBooks.unshift({ id: newId, ...requestData, categoryName: 'General', authorName: 'Various Authors' });
    }

    alert(isEdit ? 'Book updated successfully (Demo)!' : 'Book added successfully (Demo)!');
    closeModals();
    renderAdminTable(adminBooks);
}
