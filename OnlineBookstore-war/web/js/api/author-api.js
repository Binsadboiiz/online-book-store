/**
 * Author Management REST API Client Wrapper
 */
var AuthorApi = window.AuthorApi || {
    async getAll(searchQuery = '') {
        const response = await ApiClient.get('/authors', { q: searchQuery });
        return response?.data || [];
    },

    async getById(id) {
        const response = await ApiClient.get(`/authors/${id}`);
        return response?.data;
    },

    async create(authorData) {
        const response = await ApiClient.post('/authors', authorData);
        return response?.data;
    },

    async update(id, authorData) {
        const response = await ApiClient.put(`/authors/${id}`, authorData);
        return response?.data;
    },

    async delete(id) {
        const response = await ApiClient.delete(`/authors/${id}`);
        return response;
    }
};

window.AuthorApi = AuthorApi;
