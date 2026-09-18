/**
 * OnlineBookstore Admin User Management JS Module
 */

var adminUsers = window.adminUsers || [];

document.addEventListener('DOMContentLoaded', () => {
    checkAdminAccessGuard();

    const userForm = document.getElementById('userForm');
    if (userForm) {
        userForm.addEventListener('submit', handleUserFormSubmit);
    }

    const searchInput = document.getElementById('userSearchInput');
    if (searchInput) {
        let debounceTimeout;
        searchInput.addEventListener('input', (e) => {
            clearTimeout(debounceTimeout);
            debounceTimeout = setTimeout(() => {
                const roleFilter = document.getElementById('userRoleFilter')?.value || '';
                fetchAdminUserTable(e.target.value, roleFilter);
            }, 300);
        });
    }

    const roleFilterEl = document.getElementById('userRoleFilter');
    if (roleFilterEl) {
        roleFilterEl.addEventListener('change', (e) => {
            const searchQuery = document.getElementById('userSearchInput')?.value || '';
            fetchAdminUserTable(searchQuery, e.target.value);
        });
    }

    fetchAdminUserTable();
});

async function fetchAdminUserTable(searchQuery = '', roleFilter = '') {
    const tbody = document.getElementById('adminUserTableBody');
    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; padding: 2rem;">Loading user database...</td></tr>`;

    try {
        adminUsers = await UserApi.getAll(searchQuery, roleFilter);
        renderAdminUserTable(adminUsers);
        updateUserStats(adminUsers);
    } catch (e) {
        console.warn('Failed to fetch user database:', e);
        tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; padding: 2rem; color: var(--danger-color, #ef4444);">
            Failed to load user records. ${escapeHtml(e.message || '')}
        </td></tr>`;
        if (window.Toast) Toast.error('Failed to load user records: ' + (e.message || ''));
    }
}

function renderAdminUserTable(users) {
    const tbody = document.getElementById('adminUserTableBody');
    if (!tbody) return;

    if (!users || users.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; padding: 2rem;">No user accounts found matching the criteria.</td></tr>`;
        return;
    }

    tbody.innerHTML = users.map(u => {
        const role = (u.role || 'CUSTOMER').toUpperCase();
        let roleBadgeColor = 'background: #e4e4e7; color: #18181b;';
        if (role === 'ADMIN') roleBadgeColor = 'background: #18181b; color: #ffffff;';
        else if (role === 'MANAGER') roleBadgeColor = 'background: #2563eb; color: #ffffff;';

        const isActive = u.active !== false;
        const statusBadge = isActive
            ? `<span class="badge" style="background: #dcfce7; color: #166534; padding: 0.25rem 0.5rem; border-radius: 4px; font-weight: 600; font-size: 0.8rem;"><i class="bi bi-check-circle-fill"></i> Active</span>`
            : `<span class="badge" style="background: #fee2e2; color: #991b1b; padding: 0.25rem 0.5rem; border-radius: 4px; font-weight: 600; font-size: 0.8rem;"><i class="bi bi-x-circle-fill"></i> Disabled</span>`;

        return `
            <tr>
                <td>#${u.id}</td>
                <td>
                    <strong>${escapeHtml(u.fullName || u.username)}</strong><br>
                    <span style="font-size: 0.8rem; color: var(--text-muted);">@${escapeHtml(u.username)}</span>
                </td>
                <td>${escapeHtml(u.email || 'N/A')}</td>
                <td>
                    <span style="${roleBadgeColor} padding: 0.2rem 0.55rem; border-radius: 4px; font-size: 0.75rem; font-weight: 700;">
                        ${escapeHtml(role)}
                    </span>
                </td>
                <td>${statusBadge}</td>
                <td>
                    <div class="action-btns">
                        <button class="btn btn-secondary btn-sm" onclick="openUserModal(${u.id})">
                            <i class="bi bi-pencil"></i> Edit
                        </button>
                        <button class="btn btn-secondary btn-sm" onclick="toggleUserStatusAdmin(${u.id}, ${!isActive}, '${escapeHtml(u.username)}')">
                            <i class="bi bi-power"></i> ${isActive ? 'Deactivate' : 'Activate'}
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="deleteUserAdmin(${u.id}, '${escapeHtml(u.username)}')">
                            <i class="bi bi-trash"></i> Delete
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

function updateUserStats(users) {
    const totalEl = document.getElementById('statTotalUsers');
    const activeEl = document.getElementById('statActiveUsers');
    const managersEl = document.getElementById('statManagerUsers');

    if (!users) return;

    if (totalEl) totalEl.textContent = users.length;
    if (activeEl) activeEl.textContent = users.filter(u => u.active !== false).length;
    if (managersEl) managersEl.textContent = users.filter(u => u.role === 'ADMIN' || u.role === 'MANAGER').length;
}

function openUserModal(userId = null) {
    const currentRole = getUserRole();
    if (currentRole !== 'manager' && currentRole !== 'admin') {
        if (window.Toast) Toast.warning('Access Restricted: Only Manager or Admin users can manage accounts.');
        return;
    }

    const modal = document.getElementById('userModal');
    const form = document.getElementById('userForm');
    const titleEl = document.getElementById('userModalTitle');
    const usernameInput = document.getElementById('userUsernameInput');
    const passHelpText = document.getElementById('userPasswordHelp');

    if (!modal || !form) return;

    form.reset();
    document.getElementById('userIdInput').value = '';

    if (userId) {
        const user = adminUsers.find(u => u.id === userId);
        if (user) {
            if (titleEl) titleEl.textContent = 'Edit User #' + user.id;
            document.getElementById('userIdInput').value = user.id;
            usernameInput.value = user.username || '';
            usernameInput.disabled = true;
            form.email.value = user.email || '';
            form.fullName.value = user.fullName || '';
            form.role.value = (user.role || 'CUSTOMER').toUpperCase();
            form.active.value = user.active !== false ? 'true' : 'false';
            form.password.value = '';
            form.password.required = false;
            if (passHelpText) passHelpText.style.display = 'block';
        }
    } else {
        if (titleEl) titleEl.textContent = 'Add New Account';
        usernameInput.disabled = false;
        usernameInput.required = true;
        form.password.required = true;
        if (passHelpText) passHelpText.style.display = 'none';
    }

    modal.classList.add('active');
}

function closeUserModal() {
    const modal = document.getElementById('userModal');
    if (modal) {
        modal.classList.remove('active');
    }
}

async function handleUserFormSubmit(e) {
    e.preventDefault();
    const form = e.target;
    const userId = document.getElementById('userIdInput').value;

    const payload = {
        username: form.username.value.trim(),
        email: form.email.value.trim(),
        fullName: form.fullName.value.trim(),
        role: form.role.value,
        active: form.active.value === 'true'
    };

    if (/\d/.test(payload.fullName)) {
        if (window.Toast) Toast.warning('Full name cannot contain numbers.');
        return;
    }

    if (payload.password) {
        if (payload.password.length < 8 || !/[A-Z]/.test(payload.password)) {
            if (window.Toast) Toast.warning('Password must be at least 8 characters long and contain at least 1 uppercase letter.');
            return;
        }
    }

    try {
        if (userId) {
            await UserApi.update(userId, payload);
            if (window.Toast) Toast.success('User account updated successfully!');
        } else {
            if (!payload.username || !payload.email || !payload.password) {
                if (window.Toast) Toast.warning('Username, email, and password are required for new accounts.');
                return;
            }
            await UserApi.create(payload);
            if (window.Toast) Toast.success('User account created successfully!');
        }

        closeUserModal();
        const searchQuery = document.getElementById('userSearchInput')?.value || '';
        const roleFilter = document.getElementById('userRoleFilter')?.value || '';
        fetchAdminUserTable(searchQuery, roleFilter);
    } catch (err) {
        if (window.Toast) Toast.error('Operation failed: ' + (err.message || 'Unknown error'));
    }
}

async function toggleUserStatusAdmin(userId, targetStatus, username) {
    const actionText = targetStatus ? 'activate' : 'deactivate';
    if (!confirm(`Are you sure you want to ${actionText} user account "@${username}"?`)) {
        return;
    }

    try {
        await UserApi.toggleStatus(userId, targetStatus);
        if (window.Toast) Toast.success(`User account "@${username}" ${actionText}d successfully.`);
        const searchQuery = document.getElementById('userSearchInput')?.value || '';
        const roleFilter = document.getElementById('userRoleFilter')?.value || '';
        fetchAdminUserTable(searchQuery, roleFilter);
    } catch (err) {
        if (window.Toast) Toast.error('Failed to update user status: ' + (err.message || 'Unknown error'));
    }
}

async function deleteUserAdmin(userId, username) {
    if (!confirm(`CAUTION: Are you sure you want to permanently delete user account "@${username}" (ID: ${userId})?`)) {
        return;
    }

    try {
        await UserApi.delete(userId);
        if (window.Toast) Toast.success(`User account "@${username}" was deleted successfully.`);
        const searchQuery = document.getElementById('userSearchInput')?.value || '';
        const roleFilter = document.getElementById('userRoleFilter')?.value || '';
        fetchAdminUserTable(searchQuery, roleFilter);
    } catch (err) {
        if (window.Toast) Toast.error('Failed to delete user account: ' + (err.message || 'Unknown error'));
    }
}

window.openUserModal = openUserModal;
window.closeUserModal = closeUserModal;
window.toggleUserStatusAdmin = toggleUserStatusAdmin;
window.deleteUserAdmin = deleteUserAdmin;
