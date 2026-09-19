/**
 * Admin Books UI Helpers
 */
function openAddBookModal() {
    const modal = document.getElementById('addBookModal');
    if (modal) modal.classList.add('active');
}

function closeModals() {
    document.querySelectorAll('.modal-backdrop, .modal-overlay').forEach(m => m.classList.remove('active'));
}

window.openAddBookModal = openAddBookModal;
window.closeModals = closeModals;
