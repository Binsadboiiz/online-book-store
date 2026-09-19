/**
 * Admin Users UI Helpers
 */
function openUserModal() {
    const modal = document.getElementById('userModal');
    if (modal) modal.classList.add('active');
}

function closeUserModal() {
    const modal = document.getElementById('userModal');
    if (modal) modal.classList.remove('active');
}

window.openUserModal = openUserModal;
window.closeUserModal = closeUserModal;
