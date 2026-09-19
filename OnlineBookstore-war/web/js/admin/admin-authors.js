/**
 * Admin Authors UI Helpers
 */
function openAuthorModal() {
    const modal = document.getElementById('authorModal');
    if (modal) modal.classList.add('active');
}

function closeAuthorModal() {
    const modal = document.getElementById('authorModal');
    if (modal) modal.classList.remove('active');
}

window.openAuthorModal = openAuthorModal;
window.closeAuthorModal = closeAuthorModal;
