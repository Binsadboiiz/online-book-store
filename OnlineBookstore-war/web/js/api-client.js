/**
 * OnlineBookstore Centralized Client HTTP API Module
 * Encapsulates fetch logic, base URL handling, headers, and error catching.
 */

const ApiClient = {
    /**
     * Resolve the base API endpoint root context.
     * @returns {string} e.g. "/OnlineBookstore-war/api" or "/api"
     */
    getBaseUrl() {
        const contextPath = typeof getContextPath === 'function' ? getContextPath() : '';
        return contextPath + '/api';
    },

    /**
     * Core request wrapper
     * @param {string} endpoint - Path relative to base API (e.g. '/books' or '/auth/login')
     * @param {Object} options - Fetch options (method, headers, body)
     * @returns {Promise<any>} Parsed response data
     */
    async request(endpoint, options = {}) {
        const url = this.getBaseUrl() + (endpoint.startsWith('/') ? endpoint : '/' + endpoint);
        
        const defaultHeaders = {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        };

        const sessionId = localStorage.getItem('sessionId');
        if (sessionId) {
            defaultHeaders['Authorization'] = `Bearer ${sessionId}`;
            defaultHeaders['X-Session-ID'] = sessionId;
        }

        const config = {
            ...options,
            headers: {
                ...defaultHeaders,
                ...options.headers
            }
        };

        if (config.body && typeof config.body === 'object' && !(config.body instanceof FormData)) {
            config.body = JSON.stringify(config.body);
        }

        try {
            const response = await fetch(url, config);

            if (!response.ok) {
                let errorDetails = null;
                try {
                    errorDetails = await response.json();
                } catch (e) {
                    errorDetails = { message: await response.text() };
                }
                
                // If 401 Unauthorized, session token is invalid or expired -> clear session
                if (response.status === 401) {
                    console.warn('[ApiClient] Session invalid or expired (401 Unauthorized). Clearing session.');
                    if (typeof clearSession === 'function') {
                        clearSession();
                    } else {
                        localStorage.removeItem('sessionId');
                        localStorage.removeItem('user_info');
                        localStorage.removeItem('user_role');
                    }
                    if (window.location.pathname.includes('/pages/admin/')) {
                        const context = typeof getContextPath === 'function' ? getContextPath() : '';
                        window.location.href = context + '/pages/auth/login.xhtml?redirect=' + encodeURIComponent(window.location.pathname);
                    }
                }

                const error = new Error(errorDetails?.message || `HTTP ${response.status}: ${response.statusText}`);
                error.status = response.status;
                error.details = errorDetails;
                throw error;
            }

            // Parse response JSON if present
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            }
            return await response.text();

        } catch (error) {
            console.error(`[ApiClient Error] ${config.method || 'GET'} ${url}:`, error.message);
            throw error;
        }
    },

    /**
     * HTTP GET method
     */
    get(endpoint, params = {}) {
        const queryString = new URLSearchParams();
        Object.keys(params).forEach(key => {
            if (params[key] !== null && params[key] !== undefined && params[key] !== '') {
                queryString.append(key, params[key]);
            }
        });
        
        const qs = queryString.toString();
        const fullEndpoint = qs ? `${endpoint}?${qs}` : endpoint;
        return this.request(fullEndpoint, { method: 'GET' });
    },

    /**
     * HTTP POST method
     */
    post(endpoint, body = {}) {
        return this.request(endpoint, { method: 'POST', body });
    },

    /**
     * HTTP PUT method
     */
    put(endpoint, body = {}) {
        return this.request(endpoint, { method: 'PUT', body });
    },

    /**
     * HTTP DELETE method
     */
    delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }
};

window.ApiClient = ApiClient;
