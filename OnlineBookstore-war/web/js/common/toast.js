/**
 * Monochrome Design System - Toast Notification Module
 * OnlineBookstore (Jakarta EE)
 */

var Toast = window.Toast || {
    container: null,

    initContainer() {
        if (!this.container) {
            let el = document.getElementById('toastContainer');
            if (!el) {
                el = document.createElement('div');
                el.id = 'toastContainer';
                el.className = 'toast-container';
                document.body.appendChild(el);
            }
            this.container = el;
        }
    },

    show(message, type = 'info', title = '', duration = 3500) {
        this.initContainer();

        const toast = document.createElement('div');
        toast.className = `toast-item toast-${type}`;

        let iconClass = 'bi-info-circle-fill';
        let defaultTitle = 'Notification';

        if (type === 'success') {
            iconClass = 'bi-check-circle-fill';
            defaultTitle = 'Success';
        } else if (type === 'error' || type === 'danger') {
            iconClass = 'bi-x-circle-fill';
            defaultTitle = 'Error';
            type = 'error';
        } else if (type === 'warning') {
            iconClass = 'bi-exclamation-triangle-fill';
            defaultTitle = 'Warning';
        }

        const displayTitle = title || defaultTitle;
        const safeMessage = typeof message === 'string' ? message : (message?.message || String(message));

        toast.innerHTML = `
            <div class="toast-icon"><i class="bi ${iconClass}"></i></div>
            <div class="toast-content">
                <div class="toast-title">${escapeHtml(displayTitle)}</div>
                <div class="toast-message">${escapeHtml(safeMessage)}</div>
            </div>
            <button class="toast-close" onclick="this.parentElement.remove()">&times;</button>
            <div class="toast-progress" style="animation-duration: ${duration}ms;"></div>
        `;

        this.container.appendChild(toast);

        // Auto remove after duration
        const timer = setTimeout(() => {
            toast.classList.add('toast-hiding');
            setTimeout(() => {
                if (toast.parentElement) {
                    toast.remove();
                }
            }, 300);
        }, duration);

        toast.addEventListener('mouseenter', () => clearTimeout(timer));
    },

    success(message, title = '') {
        this.show(message, 'success', title, 3500);
    },

    error(message, title = '') {
        this.show(message, 'error', title, 4500);
    },

    warning(message, title = '') {
        this.show(message, 'warning', title, 4000);
    },

    info(message, title = '') {
        this.show(message, 'info', title, 3500);
    }
};

window.Toast = Toast;
window.showToast = function(msg, type, title) {
    Toast.show(msg, type, title);
};
