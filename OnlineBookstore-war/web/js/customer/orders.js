/**
 * OnlineBookstore Customer Orders & Tracking JS Module
 */

var customerOrdersList = [];

document.addEventListener('DOMContentLoaded', () => {
    if (typeof isLoggedIn === 'function' && !isLoggedIn()) {
        const context = typeof getContextPath === 'function' ? getContextPath() : '';
        window.location.href = context + '/pages/auth/login.xhtml?redirect=' + encodeURIComponent(window.location.pathname);
        return;
    }
    loadCustomerOrders();
});

async function loadCustomerOrders() {
    const container = document.getElementById('ordersContainer');
    if (!container) return;

    container.innerHTML = `<div class="empty-state"><div class="empty-title">Loading your order history...</div></div>`;

    try {
        if (!window.OrderApi || !OrderApi.getUserOrders) {
            container.innerHTML = `<div class="empty-state"><div class="empty-title">Order API not available</div></div>`;
            return;
        }

        const orders = await OrderApi.getUserOrders();
        customerOrdersList = Array.isArray(orders) ? orders : [];

        if (customerOrdersList.length === 0) {
            container.innerHTML = `
                <div class="empty-state" style="padding: 3rem 1.5rem; text-align: center;">
                    <div style="font-size: 3rem; color: var(--text-muted); margin-bottom: 1rem;"><i class="bi bi-box-seam"></i></div>
                    <div class="empty-title" style="font-size: 1.25rem; font-weight: 700; margin-bottom: 0.5rem;">No Orders Found</div>
                    <p class="empty-desc" style="color: var(--text-muted); margin-bottom: 1.5rem;">You haven't placed any book orders yet.</p>
                    <a href="${getContextPath()}/pages/customer/books.xhtml" class="btn btn-primary">
                        <i class="bi bi-grid" style="margin-right: 0.4rem;"></i> Browse Catalog
                    </a>
                </div>
            `;
            return;
        }

        container.innerHTML = customerOrdersList.map(order => renderOrderCard(order)).join('');

    } catch (e) {
        console.warn('Failed to load user orders:', e);
        container.innerHTML = `<div class="empty-state"><div class="empty-title">Unable to load orders: ${escapeHtml(e.message || '')}</div></div>`;
    }
}

function renderOrderCard(order) {
    const statusUpper = (order.status || 'PENDING').toUpperCase();
    
    // Status Badge & Stepper Config
    let badgeClass = 'background: #fef3c7; color: #b45309; border: 1px solid #fde68a;';
    if (statusUpper === 'CONFIRMED') badgeClass = 'background: #e0f2fe; color: #0369a1; border: 1px solid #bae6fd;';
    else if (statusUpper === 'SHIPPED') badgeClass = 'background: #f0fdf4; color: #15803d; border: 1px solid #bbf7d0;';
    else if (statusUpper === 'DELIVERED') badgeClass = 'background: #dcfce7; color: #166534; border: 1px solid #86efac;';
    else if (statusUpper === 'CANCELLED') badgeClass = 'background: #fee2e2; color: #b91c1c; border: 1px solid #fca5a5;';

    const items = order.items || [];
    const itemsHTML = items.map(item => `
        <div style="display: flex; justify-content: space-between; align-items: center; padding: 0.6rem 0; border-bottom: 1px dashed var(--border-color, #e2e8f0);">
            <div>
                <strong style="font-size: 0.95rem;">${escapeHtml(item.bookTitle || 'Book')}</strong>
                <span style="font-size: 0.85rem; color: var(--text-muted); margin-left: 0.5rem;">x ${item.quantity}</span>
            </div>
            <div style="font-weight: 600; font-size: 0.95rem;">
                ${formatCurrency(item.subtotal)}
            </div>
        </div>
    `).join('');

    const stepperHTML = renderOrderTimelineStepper(statusUpper);
    const dateFormatted = order.createdAt ? new Date(order.createdAt).toLocaleString() : '';

    return `
        <div class="card-detail-wrap" style="margin-bottom: 1.5rem; padding: 1.5rem; box-shadow: 0 2px 10px rgba(0,0,0,0.03);">
            <div style="display: flex; justify-content: space-between; align-items: flex-start; border-bottom: 1px solid var(--border-color, #e2e8f0); padding-bottom: 1rem; margin-bottom: 1rem; flex-wrap: wrap; gap: 0.75rem;">
                <div>
                    <div style="font-size: 0.8rem; text-transform: uppercase; letter-spacing: 0.5px; color: var(--text-muted); font-weight: 700;">Order Code</div>
                    <div style="font-size: 1.2rem; font-weight: 800; color: var(--text-main, #09090b);">${escapeHtml(order.orderCode)}</div>
                    <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 0.2rem;"><i class="bi bi-clock"></i> ${dateFormatted}</div>
                </div>
                <div style="text-align: right;">
                    <span style="display: inline-block; padding: 0.35rem 0.85rem; border-radius: 9999px; font-size: 0.8rem; font-weight: 800; letter-spacing: 0.5px; text-transform: UPPERCASE; ${badgeClass}">
                        ${escapeHtml(statusUpper)}
                    </span>
                    <div style="font-size: 0.85rem; color: var(--text-muted); margin-top: 0.35rem;">
                        Payment: <strong>${escapeHtml(order.paymentMethod || 'COD')}</strong> (${escapeHtml(order.paymentStatus || 'PENDING')})
                    </div>
                </div>
            </div>

            <!-- Tracking Stepper -->
            ${stepperHTML}

            <!-- Items list -->
            <div style="margin-bottom: 1rem;">
                ${itemsHTML}
            </div>

            <!-- Recipient & Financial Summary -->
            <div style="display: flex; justify-content: space-between; align-items: flex-end; flex-wrap: wrap; gap: 1rem; background: var(--bg-surface, #fafafa); padding: 1rem; border-radius: 6px; border: 1px solid var(--border-color, #f1f5f9);">
                <div>
                    <div style="font-size: 0.85rem; color: var(--text-muted);">Recipient: <strong style="color: var(--text-main);">${escapeHtml(order.recipientName || '')} (${escapeHtml(order.recipientPhone || '')})</strong></div>
                    <div style="font-size: 0.85rem; color: var(--text-muted); margin-top: 0.2rem;">Address: <span style="color: var(--text-main);">${escapeHtml(order.shippingAddress || '')}</span></div>
                </div>
                <div style="text-align: right;">
                    <div style="font-size: 0.85rem; color: var(--text-muted);">Shipping Fee: ${formatCurrency(order.shippingFee)}</div>
                    <div style="font-size: 1.25rem; font-weight: 800; color: var(--text-main); margin-top: 0.2rem;">
                        Total: ${formatCurrency(order.finalAmount || order.totalAmount)}
                    </div>
                </div>
            </div>

            <!-- Actions -->
            <div style="display: flex; gap: 0.75rem; justify-content: flex-end; margin-top: 1.25rem; flex-wrap: wrap;">
                ${(statusUpper === 'PENDING' || statusUpper === 'UNPAID') ? `
                    <button class="btn btn-secondary btn-sm" onclick="cancelCustomerOrder(${order.id})" style="color: #dc2626; border-color: #fca5a5;">
                        <i class="bi bi-x-circle"></i> Cancel Order
                    </button>
                ` : ''}
                <button class="btn btn-primary btn-sm" onclick="openInvoiceModal(${order.id})">
                    <i class="bi bi-receipt"></i> View &amp; Print Invoice
                </button>
            </div>
        </div>
    `;
}

function renderOrderTimelineStepper(status) {
    if (status === 'CANCELLED') {
        return `
            <div style="padding: 0.85rem 1rem; border-radius: 6px; background: #fef2f2; border: 1px solid #fecaca; color: #991b1b; font-size: 0.875rem; font-weight: 600; margin-bottom: 1.25rem;">
                <i class="bi bi-x-circle-fill" style="margin-right: 0.4rem;"></i> This order has been cancelled.
            </div>
        `;
    }

    const steps = [
        { code: 'PENDING', label: 'Order Placed', icon: 'bi-clipboard-check' },
        { code: 'CONFIRMED', label: 'Confirmed', icon: 'bi-box-seam' },
        { code: 'SHIPPED', label: 'On The Way', icon: 'bi-truck' },
        { code: 'DELIVERED', label: 'Delivered', icon: 'bi-check-circle-fill' }
    ];

    const statusOrder = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED'];
    const currentIndex = statusOrder.indexOf(status);

    return `
        <div style="margin: 1.25rem 0 1.5rem 0; padding: 1rem 0.5rem; background: var(--bg-surface, #fafafa); border-radius: 8px;">
            <div style="display: flex; justify-content: space-between; position: relative;">
                ${steps.map((step, idx) => {
                    const isDone = idx <= currentIndex;
                    const isActive = idx === currentIndex;
                    const circleStyle = isDone
                        ? 'background: #09090b; color: #ffffff; border: 2px solid #09090b;'
                        : 'background: #ffffff; color: #a1a1aa; border: 2px solid #e4e4e7;';

                    return `
                        <div style="flex: 1; text-align: center; position: relative; z-index: 2;">
                            <div style="width: 38px; height: 38px; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 0.4rem auto; font-size: 1.1rem; transition: all 0.3s; ${circleStyle}">
                                <i class="bi ${step.icon}"></i>
                            </div>
                            <div style="font-size: 0.75rem; font-weight: ${isActive ? '800' : '600'}; color: ${isDone ? 'var(--text-main, #09090b)' : 'var(--text-muted, #a1a1aa)'}; text-transform: uppercase;">
                                ${step.label}
                            </div>
                        </div>
                    `;
                }).join('')}
            </div>
        </div>
    `;
}

async function cancelCustomerOrder(orderId) {
    if (!confirm('Are you sure you want to cancel this order?')) return;

    try {
        await OrderApi.cancelOrder(orderId);
        if (window.Toast) Toast.success('Order cancelled successfully.');
        else alert('Order cancelled successfully.');
        loadCustomerOrders();
    } catch (e) {
        if (window.Toast) Toast.error(e.message || 'Failed to cancel order.');
        else alert(e.message || 'Failed to cancel order.');
    }
}

async function openInvoiceModal(orderId) {
    const modal = document.getElementById('invoiceModal');
    const modalBody = document.getElementById('invoiceModalBody');
    if (!modal || !modalBody) return;

    modalBody.innerHTML = `<div class="empty-state"><div class="empty-title">Loading invoice data...</div></div>`;
    modal.classList.add('active');

    try {
        const order = await OrderApi.getOrderById(orderId);
        if (!order) {
            modalBody.innerHTML = `<div class="empty-state"><div class="empty-title">Invoice Not Found</div></div>`;
            return;
        }

        modalBody.innerHTML = generatePrintableInvoiceHTML(order);
    } catch (e) {
        console.warn('Failed to load invoice:', e);
        modalBody.innerHTML = `<div class="empty-state"><div class="empty-title">Failed to load invoice: ${escapeHtml(e.message || '')}</div></div>`;
    }
}

function generatePrintableInvoiceHTML(order) {
    const dateStr = order.createdAt ? new Date(order.createdAt).toLocaleDateString() : '';
    const items = order.items || [];
    const itemsTableHTML = items.map((item, idx) => `
        <tr>
            <td style="padding: 0.6rem; border-bottom: 1px solid #e4e4e7; text-align: center;">${idx + 1}</td>
            <td style="padding: 0.6rem; border-bottom: 1px solid #e4e4e7;"><strong>${escapeHtml(item.bookTitle || 'Book')}</strong></td>
            <td style="padding: 0.6rem; border-bottom: 1px solid #e4e4e7; text-align: right;">${formatCurrency(item.price)}</td>
            <td style="padding: 0.6rem; border-bottom: 1px solid #e4e4e7; text-align: center;">${item.quantity}</td>
            <td style="padding: 0.6rem; border-bottom: 1px solid #e4e4e7; text-align: right; font-weight: 700;">${formatCurrency(item.subtotal)}</td>
        </tr>
    `).join('');

    return `
        <div id="printableInvoiceArea" style="padding: 1.5rem; background: #ffffff; color: #09090b; font-family: system-ui, -apple-system, sans-serif;">
            <!-- Header -->
            <div style="display: flex; justify-content: space-between; border-bottom: 2px solid #09090b; padding-bottom: 1.25rem; margin-bottom: 1.5rem;">
                <div>
                    <h1 style="font-size: 1.6rem; font-weight: 900; margin: 0; letter-spacing: 0.5px;">ONLINE BOOKSTORE</h1>
                    <div style="font-size: 0.85rem; color: #71717a; margin-top: 0.25rem;">INVOICE</div>
                </div>
                <div style="text-align: right;">
                    <div style="font-size: 1.1rem; font-weight: 800;">${escapeHtml(order.orderCode)}</div>
                    <div style="font-size: 0.85rem; color: #71717a; margin-top: 0.25rem;">Date: ${dateStr}</div>
                </div>
            </div>

            <!-- Customer & Shipping details -->
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 1.5rem; font-size: 0.9rem; background: #f4f4f5; padding: 1rem; border-radius: 6px;">
                <div>
                    <strong style="text-transform: uppercase; font-size: 0.75rem; color: #71717a; display: block; margin-bottom: 0.3rem;">Customer Info</strong>
                    <div><strong>${escapeHtml(order.recipientName || 'Customer')}</strong></div>
                    <div>Phone: ${escapeHtml(order.recipientPhone || 'N/A')}</div>
                </div>
                <div>
                    <strong style="text-transform: uppercase; font-size: 0.75rem; color: #71717a; display: block; margin-bottom: 0.3rem;">Shipping Address</strong>
                    <div>${escapeHtml(order.shippingAddress || 'N/A')}</div>
                    <div>Payment Method: <strong>${escapeHtml(order.paymentMethod || 'COD')}</strong></div>
                </div>
            </div>

            <!-- Items Table -->
            <table style="width: 100%; border-collapse: collapse; margin-bottom: 1.5rem; font-size: 0.875rem;">
                <thead>
                    <tr style="background: #09090b; color: #ffffff;">
                        <th style="padding: 0.6rem; text-align: center; width: 40px;">#</th>
                        <th style="padding: 0.6rem; text-align: left;">Book Item</th>
                        <th style="padding: 0.6rem; text-align: right; width: 100px;">Price</th>
                        <th style="padding: 0.6rem; text-align: center; width: 60px;">Qty</th>
                        <th style="padding: 0.6rem; text-align: right; width: 110px;">Subtotal</th>
                    </tr>
                </thead>
                <tbody>
                    ${itemsTableHTML}
                </tbody>
            </table>

            <!-- Summary -->
            <div style="display: flex; justify-content: flex-end; margin-bottom: 2rem;">
                <div style="width: 260px; font-size: 0.9rem;">
                    <div style="display: flex; justify-content: space-between; padding: 0.3rem 0; color: #71717a;">
                        <span>Subtotal:</span>
                        <span>${formatCurrency(order.totalAmount)}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between; padding: 0.3rem 0; color: #71717a;">
                        <span>Shipping Fee:</span>
                        <span>${formatCurrency(order.shippingFee)}</span>
                    </div>
                    <div style="display: flex; justify-content: space-between; padding: 0.6rem 0; border-top: 2px solid #09090b; font-size: 1.1rem; font-weight: 900; margin-top: 0.3rem;">
                        <span>Total Paid:</span>
                        <span>${formatCurrency(order.finalAmount || order.totalAmount)}</span>
                    </div>
                </div>
            </div>

            <div style="text-align: center; font-size: 0.8rem; color: #71717a; border-top: 1px solid #e4e4e7; padding-top: 1rem;">
                Thank you for purchasing at Online Bookstore! If you have questions, please contact support.
            </div>

            <!-- Print Trigger -->
            <div style="margin-top: 1.5rem; text-align: right;" class="no-print">
                <button class="btn btn-primary" onclick="window.print()">
                    <i class="bi bi-printer" style="margin-right: 0.4rem;"></i> Print Invoice
                </button>
            </div>
        </div>
    `;
}

window.loadCustomerOrders = loadCustomerOrders;
window.cancelCustomerOrder = cancelCustomerOrder;
window.openInvoiceModal = openInvoiceModal;
