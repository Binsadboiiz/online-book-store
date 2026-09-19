/**
 * OnlineBookstore Cart UI Helpers
 */
function openCheckoutModal() {
    const modal = document.getElementById('checkoutModal');
    if (modal) modal.classList.add('active');
}

function closeModals() {
    document.querySelectorAll('.modal-backdrop, .modal-overlay').forEach(m => m.classList.remove('active'));
}

window.openCheckoutModal = openCheckoutModal;
window.closeModals = closeModals;
