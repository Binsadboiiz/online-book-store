/**
 * OnlineBookstore Orders UI Helpers
 */
function closeModals() {
    document.querySelectorAll('.modal-backdrop, .modal-overlay').forEach(m => m.classList.remove('active'));
}

window.closeModals = closeModals;
