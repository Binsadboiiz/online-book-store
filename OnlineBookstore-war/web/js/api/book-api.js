/**
 * OnlineBookstore Book API Service Layer
 * Abstracts REST endpoints for Book operations.
 */

var BookApi = window.BookApi || {
    async getAll(filters = {}) {
        try {
            const response = await ApiClient.get('/books', filters);
            return response?.data || response || [];
        } catch (e) {
            console.warn('BookApi.getAll network error:', e.message);
            throw e;
        }
    },

    async getById(id) {
        try {
            const response = await ApiClient.get(`/books/${id}`);
            return response?.data || response;
        } catch (e) {
            console.warn(`BookApi.getById(${id}) network error:`, e.message);
            throw e;
        }
    },

    async create(bookData) {
        const response = await ApiClient.post('/books', bookData);
        return response?.data || response;
    },

    async update(id, bookData) {
        const response = await ApiClient.put(`/books/${id}`, bookData);
        return response?.data || response;
    },

    async getTopSelling(limit = 10) {
        try {
            const response = await ApiClient.get('/books/top-selling', { limit });
            return response?.data || response || [];
        } catch (e) {
            console.warn('BookApi.getTopSelling error:', e.message);
            return [];
        }
    },

    async getCategories() {
        try {
            const response = await ApiClient.get('/books/categories');
            return response?.data || response || [];
        } catch (e) {
            console.warn('BookApi.getCategories error:', e.message);
            return [];
        }
    },

    async delete(id) {
        const response = await ApiClient.delete(`/books/${id}`);
        return response;
    }
};

window.BookApi = BookApi;
