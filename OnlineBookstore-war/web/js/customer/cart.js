/**
 * OnlineBookstore Shopping Cart & Checkout JS Module
 */

var currentCartData = null;

document.addEventListener('DOMContentLoaded', () => {
    fetchShoppingCart();

    const checkoutForm = document.getElementById('checkoutForm');
    if (checkoutForm) {
        checkoutForm.addEventListener('submit', handleCheckoutSubmit);
    }
});

async function fetchShoppingCart() {
    const container = document.getElementById('cartContainer');
    if (!container) return;

    const userLoggedIn = typeof isLoggedIn === 'function' ? isLoggedIn() : !!(localStorage.getItem('sessionId') || localStorage.getItem('auth_token'));
    if (!userLoggedIn) {
        renderLoginPrompt(container);
        return;
    }

    container.innerHTML = `<div class="empty-state"><div class="empty-title">Loading shopping cart...</div></div>`;

    try {
        currentCartData = await CartApi.getCart();
        renderCartUI(currentCartData);
    } catch (e) {
        console.warn('Failed to load shopping cart:', e);
        container.innerHTML = `<div class="empty-state"><div class="empty-title">Unable to load cart</div><p style="color: var(--text-muted);">${escapeHtml(e.message || '')}</p></div>`;
    }
}

function renderLoginPrompt(container) {
    container.innerHTML = `
        <div class="card-detail-wrap" style="text-align: center; padding: 4rem 2rem;">
            <div style="font-size: 3.5rem; margin-bottom: 1rem; color: var(--text-muted);"><i class="bi bi-person-lock"></i></div>
            <h2 style="font-size: 1.5rem; font-weight: 800; margin-bottom: 0.5rem;">Authentication Required</h2>
            <p style="color: var(--text-muted); margin-bottom: 1.5rem;">Please sign in to access your persistent shopping cart and placed orders.</p>
            <a href="${getContextPath()}/pages/auth/login.xhtml?redirect=${encodeURIComponent(window.location.pathname)}" class="btn btn-primary">
                <i class="bi bi-box-arrow-in-right" style="margin-right: 0.4rem;"></i> Sign In
            </a>
        </div>
    `;
}

function renderCartUI(cart) {
    const container = document.getElementById('cartContainer');
    if (!container) return;

    const items = cart?.items || cart?.cartItems || [];
    if (!items || items.length === 0) {
        container.innerHTML = `
            <div class="card-detail-wrap" style="text-align: center; padding: 4rem 2rem;">
                <div style="font-size: 3.5rem; margin-bottom: 1rem; color: var(--text-muted);"><i class="bi bi-cart-x"></i></div>
                <h2 style="font-size: 1.5rem; font-weight: 800; margin-bottom: 0.5rem;">Your Cart is Currently Empty</h2>
                <p style="color: var(--text-muted); margin-bottom: 1.5rem;">Explore our catalog and add books to start building your order.</p>
                <a href="${getContextPath()}/pages/customer/books.xhtml" class="btn btn-primary">
                    <i class="bi bi-shop" style="margin-right: 0.4rem;"></i> Browse Catalog
                </a>
            </div>
        `;
        return;
    }

    const subtotal = cart.totalPrice || items.reduce((sum, item) => sum + (item.subtotal || ((item.price || 0) * item.quantity)), 0);
    const shippingFee = 3.00; // Fixed $3.00 USD shipping fee
    const grandTotal = subtotal + shippingFee;

    const itemsRowsHTML = items.map(item => {
        const coverHTML = item.coverImage
            ? `<img src="${escapeHtml(item.coverImage)}" alt="${escapeHtml(item.bookTitle)}" style="width: 50px; height: 70px; object-fit: cover; border-radius: 4px;" onerror="this.outerHTML='<div style=\\'width:50px;height:70px;background:#f1f5f9;display:flex;align-items:center;justify-content:center;border-radius:4px;\\'><i class=\\'bi bi-journal-text\\'></i></div>'">`
            : `<div style="width: 50px; height: 70px; background: #f1f5f9; display: flex; align-items: center; justify-content: center; border-radius: 4px;"><i class="bi bi-journal-text"></i></div>`;

        return `
            <tr style="border-bottom: 1px solid var(--border-color, #e2e8f0);">
                <td style="padding: 1rem; display: flex; align-items: center; gap: 1rem;">
                    ${coverHTML}
                    <div>
                        <strong style="font-size: 0.95rem; display: block;">${escapeHtml(item.bookTitle || item.title)}</strong>
                        <span style="font-size: 0.8rem; color: var(--text-muted);">${escapeHtml(item.authorName || '')}</span>
                    </div>
                </td>
                <td style="padding: 1rem; font-weight: 600;">${formatCurrency(item.price)}</td>
                <td style="padding: 1rem;">
                    <div style="display: inline-flex; align-items: center; border: 1px solid var(--border-color, #cbd5e1); border-radius: 6px; overflow: hidden;">
                        <button type="button" class="btn btn-secondary btn-sm" style="border-radius: 0; border: none; padding: 0.3rem 0.6rem;" onclick="updateCartItemQty(${item.id}, ${item.quantity - 1})">-</button>
                        <input type="number" value="${item.quantity}" min="1" max="${item.stockQuantity || 99}" style="width: 45px; text-align: center; border: none; font-weight: 700; outline: none;" onchange="updateCartItemQty(${item.id}, parseInt(this.value) || 1)" />
                        <button type="button" class="btn btn-secondary btn-sm" style="border-radius: 0; border: none; padding: 0.3rem 0.6rem;" onclick="updateCartItemQty(${item.id}, ${item.quantity + 1})">+</button>
                    </div>
                </td>
                <td style="padding: 1rem; font-weight: 700; color: var(--text-main);">${formatCurrency(item.subtotal || ((item.price || 0) * item.quantity))}</td>
                <td style="padding: 1rem; text-align: right;">
                    <button class="btn btn-danger btn-sm" onclick="removeCartItemDirect(${item.id})" title="Remove item">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    }).join('');

    container.innerHTML = `
        <div style="display: grid; grid-template-columns: 1fr 340px; gap: 2rem; align-items: start;">
            <!-- Cart Table Card -->
            <div class="card-detail-wrap" style="padding: 0; overflow-x: auto;">
                <div style="padding: 1.25rem 1.5rem; border-bottom: 1px solid var(--border-color, #e2e8f0); display: flex; justify-content: space-between; align-items: center;">
                    <h3 style="font-size: 1.1rem; font-weight: 800; margin: 0;">Cart Items (${items.length})</h3>
                    <button class="btn btn-secondary btn-sm" onclick="clearCartDirect()">
                        <i class="bi bi-trash"></i> Clear All
                    </button>
                </div>
                <table style="width: 100%; border-collapse: collapse; text-align: left;">
                    <thead>
                        <tr style="background: var(--bg-surface, #f8fafc); border-bottom: 1px solid var(--border-color, #e2e8f0); font-size: 0.85rem; color: var(--text-muted);">
                            <th style="padding: 0.75rem 1rem;">Book Product</th>
                            <th style="padding: 0.75rem 1rem;">Unit Price</th>
                            <th style="padding: 0.75rem 1rem;">Quantity</th>
                            <th style="padding: 0.75rem 1rem;">Subtotal</th>
                            <th style="padding: 0.75rem 1rem; text-align: right;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${itemsRowsHTML}
                    </tbody>
                </table>
            </div>

            <!-- Order Summary Card -->
            <div class="card-detail-wrap" style="padding: 1.5rem;">
                <h3 style="font-size: 1.1rem; font-weight: 800; margin-bottom: 1.25rem; border-bottom: 1px solid var(--border-color, #e2e8f0); padding-bottom: 0.75rem;">Order Summary</h3>
                <div style="display: flex; justify-content: space-between; margin-bottom: 0.75rem; font-size: 0.95rem;">
                    <span style="color: var(--text-muted);">Subtotal:</span>
                    <strong style="font-weight: 700;">${formatCurrency(subtotal)}</strong>
                </div>
                <div style="display: flex; justify-content: space-between; margin-bottom: 0.75rem; font-size: 0.95rem;">
                    <span style="color: var(--text-muted);">Standard Shipping:</span>
                    <span>${formatCurrency(shippingFee)}</span>
                </div>
                <div style="border-top: 1px solid var(--border-color, #e2e8f0); margin: 1rem 0; padding-top: 0.75rem; display: flex; justify-content: space-between; align-items: center;">
                    <span style="font-weight: 800; font-size: 1.1rem;">Grand Total:</span>
                    <strong style="font-weight: 800; font-size: 1.3rem; color: var(--text-main);">${formatCurrency(grandTotal)}</strong>
                </div>
                <button class="btn btn-primary" style="width: 100%; padding: 0.85rem; font-weight: 700; margin-top: 0.5rem;" onclick="openCheckoutModal()">
                    <i class="bi bi-credit-card" style="margin-right: 0.4rem;"></i> Proceed to Checkout
                </button>
            </div>
        </div>
    `;
}

async function updateCartItemQty(itemId, newQty) {
    if (newQty < 1) {
        removeCartItemDirect(itemId);
        return;
    }
    try {
        await CartApi.updateItem(itemId, newQty);
        fetchShoppingCart();
    } catch (e) {
        if (window.Toast) Toast.error('Failed to update quantity: ' + (e.message || ''));
    }
}

async function removeCartItemDirect(itemId) {
    if (!confirm('Are you sure you want to remove this item from your cart?')) return;
    try {
        await CartApi.removeItem(itemId);
        if (window.Toast) Toast.success('Item removed from cart.');
        fetchShoppingCart();
    } catch (e) {
        if (window.Toast) Toast.error('Failed to remove item: ' + (e.message || ''));
    }
}

async function clearCartDirect() {
    if (!confirm('Are you sure you want to clear your entire cart?')) return;
    try {
        await CartApi.clearCart();
        if (window.Toast) Toast.success('Cart cleared.');
        fetchShoppingCart();
    } catch (e) {
        if (window.Toast) Toast.error('Failed to clear cart: ' + (e.message || ''));
    }
}

function openCheckoutModal() {
    const user = getUserInfo();
    const modal = document.getElementById('checkoutModal');
    const form = document.getElementById('checkoutForm');
    if (!modal || !form) return;

    if (user) {
        if (form.recipientName) form.recipientName.value = user.fullName || '';
        if (form.recipientPhone && user.phone) form.recipientPhone.value = user.phone;
    }

    modal.classList.add('active');
}

async function handleCheckoutSubmit(e) {
    e.preventDefault();
    const form = e.target;

    const payload = {
        recipientName: form.recipientName.value.trim(),
        recipientPhone: form.recipientPhone.value.trim(),
        shippingAddress: form.shippingAddress.value.trim(),
        note: form.note.value.trim(),
        paymentMethod: form.paymentMethod.value,
        shippingFee: 3.00
    };

    if (!payload.recipientName || !payload.recipientPhone || !payload.shippingAddress) {
        if (window.Toast) Toast.warning('Please fill in recipient name, phone, and shipping address.');
        return;
    }

    try {
        const order = await OrderApi.createOrder(payload);
        if (window.Toast) Toast.success('Order placed successfully! Order Code: ' + (order.orderCode || ''));
        closeModals();
        setTimeout(() => {
            window.location.href = getContextPath() + '/pages/customer/orders.xhtml';
        }, 1200);
    } catch (err) {
        if (window.Toast) Toast.error('Failed to place order: ' + (err.message || ''));
    }
}

window.fetchShoppingCart = fetchShoppingCart;
window.updateCartItemQty = updateCartItemQty;
window.removeCartItemDirect = removeCartItemDirect;
window.clearCartDirect = clearCartDirect;
window.openCheckoutModal = openCheckoutModal;
