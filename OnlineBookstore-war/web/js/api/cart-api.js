/**
 * OnlineBookstore Cart REST API Client Wrapper
 */
var CartApi = window.CartApi || {
    async getCart() {
        try {
            const response = await ApiClient.get('/cart');
            return response?.data || response;
        } catch (e) {
            console.warn('CartApi.getCart error:', e.message);
            throw e;
        }
    },

    async addItem(bookId, quantity = 1) {
        try {
            const response = await ApiClient.post('/cart/items', {
                bookId: parseInt(bookId),
                quantity: parseInt(quantity)
            });
            return response?.data || response;
        } catch (e) {
            console.warn('CartApi.addItem error:', e.message);
            throw e;
        }
    },

    async updateItem(itemId, quantity) {
        try {
            const response = await ApiClient.put(`/cart/items/${itemId}`, {
                quantity: parseInt(quantity)
            });
            return response?.data || response;
        } catch (e) {
            console.warn(`CartApi.updateItem(${itemId}) error:`, e.message);
            throw e;
        }
    },

    async removeItem(itemId) {
        try {
            const response = await ApiClient.delete(`/cart/items/${itemId}`);
            return response?.data || response;
        } catch (e) {
            console.warn(`CartApi.removeItem(${itemId}) error:`, e.message);
            throw e;
        }
    },

    async clearCart() {
        try {
            const response = await ApiClient.delete('/cart');
            return response;
        } catch (e) {
            console.warn('CartApi.clearCart error:', e.message);
            throw e;
        }
    }
};

window.CartApi = CartApi;
