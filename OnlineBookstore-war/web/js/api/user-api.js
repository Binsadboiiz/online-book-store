/**
 * User Management REST API Client Wrapper
 */
var UserApi = window.UserApi || {
    async getAll(searchQuery = '', roleFilter = '') {
        const response = await ApiClient.get('/users', { q: searchQuery, role: roleFilter });
        return response?.data || [];
    },

    async getById(id) {
        const response = await ApiClient.get(`/users/${id}`);
        return response?.data;
    },

    async create(userData) {
        const response = await ApiClient.post('/users', userData);
        return response?.data;
    },

    async update(id, userData) {
        const response = await ApiClient.put(`/users/${id}`, userData);
        return response?.data;
    },

    async toggleStatus(id, active) {
        const response = await ApiClient.put(`/users/${id}/status`, null, {
            params: { active: active }
        });
        return response?.data;
    },

    async delete(id) {
        const response = await ApiClient.delete(`/users/${id}`);
        return response;
    }
};

window.UserApi = UserApi;
