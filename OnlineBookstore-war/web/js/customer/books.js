/**
 * OnlineBookstore Customer Interaction Module
 * Handles client-side UI interactions, modals, and rating UI events.
 */

function openBookModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
    }
}

function closeModals() {
    document.querySelectorAll('.modal-backdrop, .modal-overlay').forEach(modal => {
        modal.classList.remove('active');
    });
}

document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        closeModals();
    }
});

window.openBookModal = openBookModal;
window.closeModals = closeModals;
