<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/includes/head.jsp">
        <jsp:param name="title" value="Error Details & Diagnostics | Online Bookstore"/>
    </jsp:include>
</head>
<body>

    <jsp:include page="/WEB-INF/includes/customer-header.jsp"/>

    <main class="main-content" style="padding: 3rem 1.5rem; max-width: 960px; margin: 0 auto;">
        
        <!-- Header Banner -->
        <div style="background: #fff0f0; border: 1px solid #fecaca; border-radius: 12px; padding: 2rem; margin-bottom: 2rem; display: flex; gap: 1.5rem; align-items: flex-start;">
            <div style="font-size: 2.75rem; color: #dc2626; line-height: 1;">
                <i class="bi bi-exclamation-triangle-fill"></i>
            </div>
            <div style="flex: 1;">
                <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.5rem; flex-wrap: wrap;">
                    <span id="errBadge" class="book-category-badge" style="background: #dc2626; color: #ffffff; font-weight: 700; padding: 0.25rem 0.75rem; font-size: 0.85rem;">
                        <%= pageContext.getErrorData() != null && pageContext.getErrorData().getStatusCode() > 0 ? pageContext.getErrorData().getStatusCode() : "ERROR" %>
                    </span>
                    <h1 id="errTitle" style="font-size: 1.5rem; font-weight: 800; margin: 0; color: #991b1b;">
                        Application Error Encountered
                    </h1>
                </div>
                <p id="errMessage" style="color: #7f1d1d; font-size: 1.05rem; margin: 0; line-height: 1.5;">
                    <%= exception != null ? exception.getMessage() : (pageContext.getErrorData() != null ? "Server Error (Status " + pageContext.getErrorData().getStatusCode() + ")" : "An unexpected execution error occurred.") %>
                </p>
            </div>
        </div>

        <!-- Details Card -->
        <div class="card-detail-wrap" style="background: var(--bg-card, #ffffff); border: 1px solid var(--border-color, #e5e7eb); border-radius: 12px; padding: 2rem; box-shadow: 0 4px 15px rgba(0,0,0,0.03); margin-bottom: 2rem;">
            
            <!-- 1. Error Location Section (Chỗ bị lỗi) -->
            <div style="margin-bottom: 2rem;">
                <h3 style="font-size: 1.1rem; font-weight: 700; border-bottom: 2px solid var(--text-main, #111827); padding-bottom: 0.5rem; margin-bottom: 1rem; display: flex; align-items: center; gap: 0.5rem;">
                    <i class="bi bi-geo-alt-fill" style="color: #dc2626;"></i> Error Location (Chỗ bị lỗi)
                </h3>
                <div style="background: var(--bg-neutral, #f9fafb); border: 1px solid var(--border-color, #e5e7eb); border-radius: 8px; padding: 1rem 1.25rem;">
                    <div style="display: grid; grid-template-columns: 140px 1fr; gap: 0.5rem 1rem; font-size: 0.95rem;">
                        <strong style="color: var(--text-muted, #6b7280);">Source / File:</strong>
                        <span id="errLocation" style="font-family: monospace; font-weight: 600; color: #1e40af; word-break: break-all;">
                            <%= pageContext.getErrorData() != null ? pageContext.getErrorData().getRequestURI() : "Client Script / Dynamic Context" %>
                        </span>

                        <strong style="color: var(--text-muted, #6b7280);">Page URL:</strong>
                        <span id="errPageUrl" style="font-family: monospace; color: var(--text-secondary, #374151); word-break: break-all;">
                            <%= request.getRequestURI() %>
                        </span>

                        <strong style="color: var(--text-muted, #6b7280);">Timestamp:</strong>
                        <span id="errTimestamp" style="color: var(--text-secondary, #374151);">
                            <%= new java.util.Date() %>
                        </span>
                    </div>
                </div>
            </div>

            <!-- 2. Stack Trace & Technical Diagnostic Details -->
            <div style="margin-bottom: 2rem;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.75rem;">
                    <h3 style="font-size: 1.1rem; font-weight: 700; margin: 0; display: flex; align-items: center; gap: 0.5rem;">
                        <i class="bi bi-code-slash"></i> Diagnostic Stack Trace & Logs
                    </h3>
                    <button id="copyLogBtn" onclick="copyStackTrace()" class="btn btn-secondary btn-sm" style="font-size: 0.85rem; padding: 0.35rem 0.75rem;">
                        <i class="bi bi-clipboard" style="margin-right: 0.3rem;"></i> Copy Log
                    </button>
                </div>
                
                <pre id="errStackTrace" style="background: #0f172a; color: #f8fafc; border-radius: 8px; padding: 1.25rem; font-family: 'Consolas', 'Fira Code', Monaco, monospace; font-size: 0.85rem; line-height: 1.5; overflow-x: auto; max-height: 380px; white-space: pre-wrap; word-break: break-all; margin: 0;"><%
                    if (exception != null) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        exception.printStackTrace(pw);
                        out.print(sw.toString());
                    } else if (pageContext.getErrorData() != null && pageContext.getErrorData().getThrowable() != null) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        pageContext.getErrorData().getThrowable().printStackTrace(pw);
                        out.print(sw.toString());
                    } else {
                        out.print("No server-side exception stack trace available. Checking client error storage...");
                    }
                %></pre>
            </div>

            <!-- Action Buttons -->
            <div style="display: flex; gap: 0.75rem; justify-content: flex-end; flex-wrap: wrap;">
                <button onclick="window.location.reload()" class="btn btn-primary" style="padding: 0.6rem 1.25rem;">
                    <i class="bi bi-arrow-clockwise" style="margin-right: 0.4rem;"></i> Try Again
                </button>
                <a href="${pageContext.request.contextPath}/pages/customer/home.jsp" class="btn btn-secondary" style="padding: 0.6rem 1.25rem;">
                    <i class="bi bi-house" style="margin-right: 0.4rem;"></i> Return to Home
                </a>
                <button onclick="alert('Diagnostic report copied! You can forward this to support.')" class="btn btn-secondary" style="padding: 0.6rem 1.25rem;">
                    <i class="bi bi-bug" style="margin-right: 0.4rem;"></i> Report Issue
                </button>
            </div>

        </div>
    </main>

    <jsp:include page="/WEB-INF/includes/footer.jsp"/>

    <script src="${pageContext.request.contextPath}/js/core.js"></script>
    <script src="${pageContext.request.contextPath}/js/error-handler.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            // Check if error payload was stored in sessionStorage by ErrorHandler.js
            try {
                const storedErrorJson = sessionStorage.getItem('last_client_error');
                if (storedErrorJson) {
                    const data = JSON.parse(storedErrorJson);
                    
                    if (data.title) document.getElementById('errTitle').textContent = data.title;
                    if (data.statusCode) document.getElementById('errBadge').textContent = data.statusCode;
                    if (data.message) document.getElementById('errMessage').textContent = data.message;
                    if (data.location) document.getElementById('errLocation').textContent = data.location;
                    if (data.url) document.getElementById('errPageUrl').textContent = data.url;
                    if (data.timestamp) document.getElementById('errTimestamp').textContent = new Date(data.timestamp).toLocaleString();
                    if (data.stack) document.getElementById('errStackTrace').textContent = data.stack;
                }
            } catch (e) {
                console.warn('Failed to parse client error payload:', e);
            }
        });

        function copyStackTrace() {
            const stackText = document.getElementById('errStackTrace').textContent;
            navigator.clipboard.writeText(stackText).then(() => {
                const btn = document.getElementById('copyLogBtn');
                btn.innerHTML = '<i class="bi bi-check2" style="margin-right: 0.3rem;"></i> Copied!';
                setTimeout(() => {
                    btn.innerHTML = '<i class="bi bi-clipboard" style="margin-right: 0.3rem;"></i> Copy Log';
                }, 2000);
            }).catch(err => {
                alert('Could not copy to clipboard.');
            });
        }
    </script>
</body>
</html>
