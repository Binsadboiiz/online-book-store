/**
 * Global Client-Side Error Handler Module
 * OnlineBookstore - Minimalist Monochrome Architecture
 */

var ErrorHandler = window.ErrorHandler || {
    showErrorDetail({ title = 'Application Error', statusCode = 500, message = 'An unexpected error occurred.', location = 'Unknown Source', stack = '' } = {}) {
        const payload = {
            title,
            statusCode,
            message,
            location,
            stack: stack || (new Error().stack || 'No stack trace available'),
            timestamp: new Date().toISOString(),
            url: window.location.href
        };

        try {
            sessionStorage.setItem('last_client_error', JSON.stringify(payload));
        } catch (e) {
            console.warn('SessionStorage unavailable for error payload', e);
        }

        const contextPath = typeof getContextPath === 'function' ? getContextPath() : '/OnlineBookstore-war';
        window.location.href = `${contextPath}/pages/error/error-detail.xhtml`;
    },

    async handleApiResponseError(response, contextMessage = 'API Request Failed') {
        let errorData = null;
        let errorMessage = `HTTP Error ${response.status}: ${response.statusText}`;

        try {
            errorData = await response.clone().json();
            if (errorData && errorData.message) {
                errorMessage = errorData.message;
            }
        } catch (e) {
            try {
                errorMessage = await response.clone().text();
            } catch (err) {
                // Keep default message
            }
        }

        const location = `API Endpoint: ${response.url}`;
        const stack = JSON.stringify(errorData || { status: response.status, statusText: response.statusText }, null, 2);

        this.showErrorDetail({
            title: `Backend API Error (${response.status})`,
            statusCode: response.status,
            message: `${contextMessage} - ${errorMessage}`,
            location,
            stack
        });
    },

    async fetchWithCatch(url, options = {}) {
        try {
            const response = await fetch(url, options);
            if (!response.ok) {
                await this.handleApiResponseError(response, `Fetch failed for [${options.method || 'GET'}] ${url}`);
                return null;
            }
            return response;
        } catch (error) {
            this.showErrorDetail({
                title: 'Network / Connection Error',
                statusCode: 'NETWORK_ERROR',
                message: error.message || 'Failed to establish connection to backend server.',
                location: `URL: ${url}`,
                stack: error.stack
            });
            return null;
        }
    }
};
window.ErrorHandler = ErrorHandler;

window.addEventListener('error', (event) => {
    console.error('[GlobalErrorHandler] Uncaught Exception:', event);
    const sourceFile = event.filename || 'Inline Script / Web Page';
    const locationStr = `${sourceFile} (Line ${event.lineno}, Col ${event.colno})`;

    if (window.location.pathname.includes('/pages/error/error-detail.xhtml')) {
        return;
    }

    ErrorHandler.showErrorDetail({
        title: 'Uncaught Client JavaScript Exception',
        statusCode: 'JS_RUNTIME_ERROR',
        message: event.message || 'A script execution error occurred on the page.',
        location: locationStr,
        stack: event.error ? event.error.stack : `At ${locationStr}`
    });
});

window.addEventListener('unhandledrejection', (event) => {
    console.error('[GlobalErrorHandler] Unhandled Promise Rejection:', event.reason);

    if (window.location.pathname.includes('/pages/error/error-detail.xhtml')) {
        return;
    }

    const reason = event.reason;
    const message = (reason && reason.message) ? reason.message : String(reason);
    const stack = (reason && reason.stack) ? reason.stack : JSON.stringify(reason, null, 2);

    ErrorHandler.showErrorDetail({
        title: 'Unhandled Async Promise Rejection',
        statusCode: 'ASYNC_REJECTION',
        message: `Unhandled rejection: ${message}`,
        location: 'Asynchronous Promise Execution',
        stack: stack
    });
});
