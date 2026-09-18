/**
 * OnlineBookstore Admin Author Management JS Module
 */

var adminAuthors = window.adminAuthors || [];

document.addEventListener('DOMContentLoaded', () => {
    checkAdminAccessGuard();

    const authorForm = document.getElementById('authorForm');
    if (authorForm) {
        authorForm.addEventListener('submit', handleAuthorFormSubmit);
    }

    const searchInput = document.getElementById('authorSearchInput');
    if (searchInput) {
        let debounceTimeout;
        searchInput.addEventListener('input', (e) => {
            clearTimeout(debounceTimeout);
            debounceTimeout = setTimeout(() => {
                fetchAdminAuthorTable(e.target.value);
            }, 300);
        });
    }

    fetchAdminAuthorTable();
});

async function fetchAdminAuthorTable(searchQuery = '') {
    const tbody = document.getElementById('adminAuthorTableBody');
    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; padding: 2rem;">Loading authors data...</td></tr>`;

    try {
        adminAuthors = await AuthorApi.getAll(searchQuery);
        renderAdminAuthorTable(adminAuthors);
        updateAuthorStats(adminAuthors);
    } catch (e) {
        console.warn('Failed to fetch author directory:', e);
        tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; padding: 2rem; color: var(--danger-color, #ef4444);">
            Failed to load author data. ${escapeHtml(e.message || '')}
        </td></tr>`;
        if (window.Toast) Toast.error('Failed to load author data: ' + (e.message || ''));
    }
}

function renderAdminAuthorTable(authors) {
    const tbody = document.getElementById('adminAuthorTableBody');
    if (!tbody) return;

    if (!authors || authors.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" style="text-align: center; padding: 2rem;">No authors found. Click "+ Add New Author" to create one.</td></tr>`;
        return;
    }

    tbody.innerHTML = authors.map(a => `
        <tr>
            <td>#${a.id}</td>
            <td>
                <strong>${escapeHtml(a.name)}</strong>
            </td>
            <td>
                <span style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; max-width: 400px;" title="${escapeHtml(a.bio || '')}">
                    ${escapeHtml(a.bio || 'No biography available')}
                </span>
            </td>
            <td>
                <span class="user-badge" style="background: #f4f4f5; padding: 0.25rem 0.5rem; border-radius: 4px; font-weight: 600;">
                    <i class="bi bi-journals" style="margin-right: 0.2rem;"></i> ${a.bookCount || 0} books
                </span>
            </td>
            <td>
                <div class="action-btns">
                    <button class="btn btn-secondary btn-sm" onclick="openAuthorModal(${a.id})">
                        <i class="bi bi-pencil"></i> Edit
                    </button>
                    <button class="btn btn-danger btn-sm" onclick="deleteAuthorAdmin(${a.id}, '${escapeHtml(a.name.replace(/'/g, "\\'"))}')">
                        <i class="bi bi-trash"></i> Delete
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

function updateAuthorStats(authors) {
    const statCountEl = document.getElementById('statTotalAuthors');
    if (statCountEl) {
        statCountEl.textContent = authors ? authors.length : 0;
    }
}

function openAuthorModal(authorId = null) {
    const role = getUserRole();
    if (role !== 'manager' && role !== 'admin') {
        if (window.Toast) Toast.warning('Access Restricted: Only Manager or Admin users can perform author management.');
        return;
    }

    const modal = document.getElementById('authorModal');
    const form = document.getElementById('authorForm');
    const titleEl = document.getElementById('authorModalTitle');

    if (!modal || !form) return;

    form.reset();
    document.getElementById('authorIdInput').value = '';

    if (authorId) {
        const author = adminAuthors.find(a => a.id === authorId);
        if (author) {
            if (titleEl) titleEl.textContent = 'Edit Author #' + author.id;
            document.getElementById('authorIdInput').value = author.id;
            form.name.value = author.name || '';
            form.bio.value = author.bio || '';
        }
    } else {
        if (titleEl) titleEl.textContent = 'Add New Author';
    }

    modal.classList.add('active');
}

function closeAuthorModal() {
    const modal = document.getElementById('authorModal');
    if (modal) {
        modal.classList.remove('active');
    }
}

async function handleAuthorFormSubmit(e) {
    e.preventDefault();
    const form = e.target;
    const authorId = document.getElementById('authorIdInput').value;

    const payload = {
        name: form.name.value.trim(),
        bio: form.bio.value.trim()
    };

    if (!payload.name) {
        if (window.Toast) Toast.warning('Author name is required.');
        return;
    }

    try {
        if (authorId) {
            await AuthorApi.update(authorId, payload);
            if (window.Toast) Toast.success('Author updated successfully!');
        } else {
            await AuthorApi.create(payload);
            if (window.Toast) Toast.success('Author created successfully!');
        }

        closeAuthorModal();
        fetchAdminAuthorTable();
    } catch (err) {
        if (window.Toast) Toast.error('Operation failed: ' + (err.message || 'Unknown error'));
    }
}

async function deleteAuthorAdmin(authorId, authorName) {
    const role = getUserRole();
    if (role !== 'manager' && role !== 'admin') {
        if (window.Toast) Toast.warning('Access Restricted: Only Manager or Admin users can delete authors.');
        return;
    }

    if (!confirm(`Are you sure you want to delete author "${authorName}" (ID: ${authorId})?`)) {
        return;
    }

    try {
        await AuthorApi.delete(authorId);
        if (window.Toast) Toast.success(`Author "${authorName}" was deleted successfully.`);
        fetchAdminAuthorTable();
    } catch (err) {
        if (window.Toast) Toast.error('Failed to delete author: ' + (err.message || 'Unknown error'));
    }
}

window.openAuthorModal = openAuthorModal;
window.closeAuthorModal = closeAuthorModal;
window.deleteAuthorAdmin = deleteAuthorAdmin;
