/**
 * OnlineBookstore Book API Service Layer
 * Abstracts REST endpoints for Book operations with transparent offline demo fallbacks.
 */

const BookApi = {
    /**
     * Fetch list of books with optional search query or category filters.
     * @param {Object} filters - { q: '', categoryId: null, authorId: null }
     * @returns {Promise<Array>} List of books
     */
    async getAll(filters = {}) {
        try {
            const response = await ApiClient.get('/books', filters);
            if (response && response.data) {
                return response.data;
            }
            return Array.isArray(response) ? response : [];
        } catch (error) {
            console.warn('[BookApi] Backend API offline/error. Using demo dataset:', error.message);
            const demoBooks = typeof getDemoBooks === 'function' ? getDemoBooks() : [];
            if (filters.q) {
                const keyword = filters.q.toLowerCase();
                return demoBooks.filter(b => 
                    (b.title && b.title.toLowerCase().includes(keyword)) ||
                    (b.authorName && b.authorName.toLowerCase().includes(keyword)) ||
                    (b.isbn && b.isbn.toLowerCase().includes(keyword))
                );
            }
            return demoBooks;
        }
    },

    /**
     * Fetch single book details by ID.
     * @param {number|string} id 
     * @returns {Promise<Object>} Book object
     */
    async getById(id) {
        try {
            const response = await ApiClient.get(`/books/${id}`);
            return response?.data || response;
        } catch (error) {
            console.warn(`[BookApi] Failed to fetch book #${id}, returning demo data:`, error.message);
            const demoBooks = typeof getDemoBooks === 'function' ? getDemoBooks() : [];
            return demoBooks.find(b => b.id == id) || demoBooks[0] || null;
        }
    },

    /**
     * Create a new book record (Admin only).
     * @param {Object} bookData 
     * @returns {Promise<Object>} Created book response
     */
    async create(bookData) {
        try {
            const response = await ApiClient.post('/books', bookData);
            return { success: true, data: response?.data || response };
        } catch (error) {
            console.warn('[BookApi] Create API request failed, falling back to local simulation:', error.message);
            return { success: false, isDemoFallback: true, error: error.message };
        }
    },

    /**
     * Update existing book record (Admin only).
     * @param {number|string} id 
     * @param {Object} bookData 
     * @returns {Promise<Object>} Updated book response
     */
    async update(id, bookData) {
        try {
            const response = await ApiClient.put(`/books/${id}`, bookData);
            return { success: true, data: response?.data || response };
        } catch (error) {
            console.warn(`[BookApi] Update API request failed for #${id}:`, error.message);
            return { success: false, isDemoFallback: true, error: error.message };
        }
    },

    /**
     * Delete a book record by ID (Admin only).
     * @param {number|string} id 
     * @returns {Promise<Object>} Response status
     */
    async delete(id) {
        try {
            const response = await ApiClient.delete(`/books/${id}`);
            return { success: true, data: response };
        } catch (error) {
            console.warn(`[BookApi] Delete API request failed for #${id}:`, error.message);
            return { success: false, isDemoFallback: true, error: error.message };
        }
    }
};

window.BookApi = BookApi;
