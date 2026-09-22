/**
 * Monochrome Design System - Toast Notification Module
 * OnlineBookstore (Jakarta EE JSF & REST API Integration)
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
    },

    confirm(opts = {}) {
        const title = opts.title || 'Confirm Action';
        const message = opts.message || 'Are you sure you want to proceed?';
        const confirmText = opts.confirmText || 'Confirm';
        const cancelText = opts.cancelText || 'Cancel';
        let type = opts.type || 'danger';
        const onConfirm = opts.onConfirm;
        const onCancel = opts.onCancel;

        let backdrop = document.getElementById('toastConfirmModal');
        if (!backdrop) {
            backdrop = document.createElement('div');
            backdrop.id = 'toastConfirmModal';
            backdrop.className = 'modal-backdrop';
            backdrop.style.cssText = 'position: fixed; inset: 0; display: flex; align-items: center; justify-content: center; z-index: 10000; background: rgba(0,0,0,0.45); backdrop-filter: blur(2px); transition: all 0.2s ease;';
            document.body.appendChild(backdrop);
        }

        let iconHeader = '<i class="bi bi-exclamation-triangle-fill" style="color: #ef4444; font-size: 2.5rem;"></i>';
        let btnStyle = 'background: #dc2626; color: #ffffff;';
        if (type === 'warning') {
            iconHeader = '<i class="bi bi-exclamation-circle-fill" style="color: #f59e0b; font-size: 2.5rem;"></i>';
            btnStyle = 'background: #d97706; color: #ffffff;';
        } else if (type === 'primary') {
            iconHeader = '<i class="bi bi-question-circle-fill" style="color: #09090b; font-size: 2.5rem;"></i>';
            btnStyle = 'background: #09090b; color: #ffffff;';
        }

        backdrop.innerHTML = `
            <div class="modal-content card-detail-wrap" style="max-width: 420px; width: 90%; padding: 2rem 1.5rem; text-align: center; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.2);">
                <div style="margin-bottom: 0.75rem;">
                    ${iconHeader}
                </div>
                <h3 style="font-size: 1.25rem; font-weight: 800; margin-bottom: 0.5rem; color: var(--text-main, #09090b);">${escapeHtml(title)}</h3>
                <p style="font-size: 0.9rem; color: var(--text-muted, #71717a); margin-bottom: 1.5rem; line-height: 1.5;">${escapeHtml(message)}</p>
                <div style="display: flex; gap: 0.75rem; justify-content: center;">
                    <button id="toastConfirmCancelBtn" class="btn btn-secondary" style="flex: 1; padding: 0.65rem; font-weight: 600;">${escapeHtml(cancelText)}</button>
                    <button id="toastConfirmOkBtn" class="btn" style="flex: 1; padding: 0.65rem; font-weight: 600; border: none; ${btnStyle}">${escapeHtml(confirmText)}</button>
                </div>
            </div>
        `;

        backdrop.classList.add('active');

        const closeDialog = () => {
            backdrop.classList.remove('active');
            setTimeout(() => { backdrop.innerHTML = ''; }, 200);
        };

        const okBtn = document.getElementById('toastConfirmOkBtn');
        const cancelBtn = document.getElementById('toastConfirmCancelBtn');

        if (okBtn) {
            okBtn.onclick = () => {
                closeDialog();
                if (typeof onConfirm === 'function') onConfirm();
            };
        }

        if (cancelBtn) {
            cancelBtn.onclick = () => {
                closeDialog();
                if (typeof onCancel === 'function') onCancel();
            };
        }
    }
};

function triggerFacesToasts() {
    const selectors = [
        '#globalMessagesPanel li',
        '#globalMessagesPanel span',
        '#globalMessagesPanel div',
        '#globalMessages li',
        '#globalMessages span',
        '#globalMessages div',
        '.alert-container li',
        '.alert-container span',
        '.alert-container div',
        '.alert-container .alert',
        '.alert',
        '.ui-growl-item',
        '.ui-messages-info',
        '.ui-messages-error',
        '.ui-messages-warn'
    ];

    const alerts = document.querySelectorAll(selectors.join(', '));
    alerts.forEach(alertEl => {
        const text = alertEl.textContent ? alertEl.textContent.trim() : '';
        if (text && !alertEl.dataset.toastShown) {
            alertEl.dataset.toastShown = 'true';

            let type = 'info';
            const classList = alertEl.className || '';
            const parentClass = alertEl.parentElement ? alertEl.parentElement.className : '';
            const combinedClass = (classList + ' ' + parentClass).toLowerCase();

            if (combinedClass.includes('danger') || combinedClass.includes('error')) {
                type = 'error';
            } else if (combinedClass.includes('success')) {
                type = 'success';
            } else if (combinedClass.includes('warn') || combinedClass.includes('warning')) {
                type = 'warning';
            } else if (combinedClass.includes('info')) {
                type = 'info';
            }

            if (type === 'error') {
                Toast.error(text);
            } else if (type === 'success') {
                Toast.success(text);
            } else if (type === 'warning') {
                Toast.warning(text);
            } else {
                Toast.info(text);
            }

            alertEl.style.display = 'none';
        }
    });
}

/**
 * Global Add To Cart Action with Toast Notification
 */
async function addToCartDirect(bookId, quantity = 1) {
    if (typeof isLoggedIn === 'function' && !isLoggedIn()) {
        Toast.warning('Please sign in to add items to your shopping cart.');
        return;
    }
    const role = typeof getUserRole === 'function' ? getUserRole() : '';
    if (role === 'admin' || role === 'manager') {
        Toast.warning('Managers and Admins are restricted from shopping cart operations.');
        return;
    }

    try {
        if (window.CartApi && typeof CartApi.addItem === 'function') {
            await CartApi.addItem(bookId, quantity);
            Toast.success('Item added to Shopping Cart!');
        } else {
            Toast.success('Item added to Shopping Cart!');
        }
    } catch (err) {
        const msg = (err && err.message) ? err.message : 'Failed to add item to shopping cart.';
        Toast.error(msg);
    }
}

// Set up MutationObserver to catch any dynamically inserted alerts/messages instantly
if (window.MutationObserver) {
    const observer = new MutationObserver(() => {
        triggerFacesToasts();
    });

    document.addEventListener('DOMContentLoaded', () => {
        triggerFacesToasts();
        observer.observe(document.body, { childList: true, subtree: true });
    });
} else {
    document.addEventListener('DOMContentLoaded', () => {
        triggerFacesToasts();
    });
}

if (window.jsf && window.jsf.ajax) {
    jsf.ajax.addOnEvent(function(data) {
        if (data.status === 'success') {
            setTimeout(triggerFacesToasts, 50);
            setTimeout(triggerFacesToasts, 200);
        }
    });
}

window.Toast = Toast;
window.showToast = function(msg, type, title) {
    Toast.show(msg, type, title);
};
window.triggerFacesToasts = triggerFacesToasts;
window.addToCartDirect = addToCartDirect;
