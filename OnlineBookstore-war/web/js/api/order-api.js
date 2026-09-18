/**
 * OnlineBookstore Order REST API Client Wrapper
 */
var OrderApi = window.OrderApi || {
    async createOrder(orderData) {
        try {
            const response = await ApiClient.post('/orders', orderData);
            return response?.data || response;
        } catch (e) {
            console.warn('OrderApi.createOrder error:', e.message);
            throw e;
        }
    },

    async getUserOrders() {
        try {
            const response = await ApiClient.get('/orders');
            return response?.data || response || [];
        } catch (e) {
            console.warn('OrderApi.getUserOrders error:', e.message);
            return [];
        }
    },

    async getOrderById(id) {
        try {
            const response = await ApiClient.get(`/orders/${id}`);
            return response?.data || response;
        } catch (e) {
            console.warn(`OrderApi.getOrderById(${id}) error:`, e.message);
            throw e;
        }
    },

    async getOrderByCode(code) {
        try {
            const response = await ApiClient.get(`/orders/code/${code}`);
            return response?.data || response;
        } catch (e) {
            console.warn(`OrderApi.getOrderByCode(${code}) error:`, e.message);
            throw e;
        }
    },

    async cancelOrder(id) {
        try {
            const response = await ApiClient.put(`/orders/${id}/cancel`);
            return response?.data || response;
        } catch (e) {
            console.warn(`OrderApi.cancelOrder(${id}) error:`, e.message);
            throw e;
        }
    }
};

window.OrderApi = OrderApi;
