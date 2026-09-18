/**
 * OnlineBookstore Review REST API Client Wrapper
 */
var ReviewApi = window.ReviewApi || {
    async getByBookId(bookId) {
        try {
            const response = await ApiClient.get(`/reviews/book/${bookId}`);
            return response?.data || response || [];
        } catch (e) {
            console.warn(`ReviewApi.getByBookId(${bookId}) error:`, e.message);
            return [];
        }
    },

    async getSummaryByBookId(bookId) {
        try {
            const response = await ApiClient.get(`/reviews/book/${bookId}/summary`);
            return response?.data || response || { averageRating: 0, totalReviews: 0 };
        } catch (e) {
            console.warn(`ReviewApi.getSummaryByBookId(${bookId}) error:`, e.message);
            return { averageRating: 0, totalReviews: 0 };
        }
    },

    async canReview(bookId) {
        try {
            const token = localStorage.getItem('auth_token') || localStorage.getItem('sessionId');
            if (!token) return false;
            const response = await ApiClient.get(`/reviews/book/${bookId}/can-review`);
            return response?.data === true;
        } catch (e) {
            return false;
        }
    },

    async createReview(reviewData) {
        const response = await ApiClient.post('/reviews', reviewData);
        return response?.data || response;
    }
};

window.ReviewApi = ReviewApi;
