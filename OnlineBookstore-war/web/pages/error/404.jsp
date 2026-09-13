<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/includes/head.jsp">
        <jsp:param name="title" value="404 - Page Not Found | Online Bookstore"/>
    </jsp:include>
</head>
<body>

    <jsp:include page="/WEB-INF/includes/customer-header.jsp"/>

    <main class="main-content" style="display: flex; align-items: center; justify-content: center; min-height: calc(80vh - 120px); padding: 3rem 1.5rem;">
        <div class="card-detail-wrap" style="max-width: 640px; width: 100%; text-align: center; padding: 4rem 2.5rem; background: var(--bg-card, #ffffff); border: 1px solid var(--border-color, #e5e7eb); border-radius: 12px; box-shadow: 0 10px 25px rgba(0,0,0,0.05);">
            
            <div style="font-size: 4.5rem; line-height: 1; margin-bottom: 1rem; color: var(--text-muted, #6b7280);">
                <i class="bi bi-compass"></i>
            </div>
            
            <h1 style="font-size: 2.25rem; font-weight: 800; letter-spacing: -0.02em; margin-bottom: 0.5rem; color: var(--text-main, #111827);">
                404 - Page Not Found
            </h1>
            
            <p style="font-size: 1.05rem; color: var(--text-muted, #4b5563); margin-bottom: 1.5rem; line-height: 1.6;">
                The page or resource you are looking for does not exist or has been relocated.
            </p>

            <div style="background: var(--bg-neutral, #f9fafb); border: 1px solid var(--border-color, #e5e7eb); border-radius: 6px; padding: 0.75rem 1rem; margin-bottom: 2rem; font-family: monospace; font-size: 0.9rem; word-break: break-all; color: var(--text-secondary, #374151);">
                <i class="bi bi-link-45deg" style="margin-right: 0.4rem;"></i>
                <span id="targetUri">Requested URL: <%= request.getAttribute("jakarta.servlet.error.request_uri") != null ? request.getAttribute("jakarta.servlet.error.request_uri") : request.getRequestURI() %></span>
            </div>

            <div style="display: flex; gap: 0.75rem; justify-content: center; flex-wrap: wrap;">
                <a href="${pageContext.request.contextPath}/pages/customer/home.jsp" class="btn btn-primary" style="padding: 0.75rem 1.5rem; font-weight: 600;">
                    <i class="bi bi-house-door" style="margin-right: 0.4rem;"></i> Return to Home
                </a>
                <a href="${pageContext.request.contextPath}/pages/customer/books.jsp" class="btn btn-secondary" style="padding: 0.75rem 1.5rem; font-weight: 600;">
                    <i class="bi bi-journal-bookmark" style="margin-right: 0.4rem;"></i> Browse Books
                </a>
                <button onclick="window.history.back()" class="btn btn-secondary" style="padding: 0.75rem 1.5rem; font-weight: 600;">
                    <i class="bi bi-arrow-left" style="margin-right: 0.4rem;"></i> Go Back
                </button>
            </div>

        </div>
    </main>

    <jsp:include page="/WEB-INF/includes/footer.jsp"/>

    <script src="${pageContext.request.contextPath}/js/core.js"></script>
    <script src="${pageContext.request.contextPath}/js/error-handler.js"></script>
</body>
</html>
