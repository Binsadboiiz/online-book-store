<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/includes/head.jsp">
        <jsp:param name="title" value="Admin Dashboard - Online Bookstore"/>
    </jsp:include>
</head>
<body>

    <jsp:include page="/WEB-INF/includes/admin-header.jsp">
        <jsp:param name="activePage" value="dashboard"/>
    </jsp:include>

    <main class="container">
        <!-- RBAC Guard container -->
        <div id="adminAccessGuard">
            <div class="page-header">
                <h1 class="page-title">Admin Dashboard</h1>
                <p class="page-subtitle">System performance overview and quick access to management tools.</p>
            </div>

            <!-- Stats Cards Grid -->
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1.5rem; margin-bottom: 2.5rem;">
                <div class="book-card" style="padding: 1.5rem; border-left: 4px solid #09090b;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600; text-transform: uppercase;">Total Books</span>
                        <i class="bi bi-journals" style="font-size: 1.25rem; color: var(--text-muted);"></i>
                    </div>
                    <h2 style="font-size: 2rem; font-weight: 800; margin-top: 0.2rem;" id="statTotalBooks">3</h2>
                </div>
                <div class="book-card" style="padding: 1.5rem; border-left: 4px solid #09090b;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600; text-transform: uppercase;">Active Users</span>
                        <i class="bi bi-people" style="font-size: 1.25rem; color: var(--text-muted);"></i>
                    </div>
                    <h2 style="font-size: 2rem; font-weight: 800; margin-top: 0.2rem;">12</h2>
                </div>
                <div class="book-card" style="padding: 1.5rem; border-left: 4px solid #09090b;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600; text-transform: uppercase;">Pending Orders</span>
                        <i class="bi bi-clock-history" style="font-size: 1.25rem; color: var(--text-muted);"></i>
                    </div>
                    <h2 style="font-size: 2rem; font-weight: 800; margin-top: 0.2rem;">5</h2>
                </div>
                <div class="book-card" style="padding: 1.5rem; border-left: 4px solid #09090b;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span style="font-size: 0.85rem; color: var(--text-muted); font-weight: 600; text-transform: uppercase;">Total Revenue</span>
                        <i class="bi bi-cash-stack" style="font-size: 1.25rem; color: var(--text-muted);"></i>
                    </div>
                    <h2 style="font-size: 2rem; font-weight: 800; margin-top: 0.2rem;">$1,240.00</h2>
                </div>
            </div>

            <!-- Quick Management Shortcuts -->
            <div class="section-header">
                <div>
                    <h2 class="section-title">Quick Actions</h2>
                    <p class="section-desc">Jump straight to core management modules.</p>
                </div>
            </div>

            <div style="display: flex; gap: 1rem;">
                <a href="${pageContext.request.contextPath}/pages/admin/books.jsp" class="btn btn-primary">
                    <i class="bi bi-journals" style="margin-right: 0.4rem;"></i> Book Management &rarr;
                </a>
                <a href="${pageContext.request.contextPath}/pages/admin/users.jsp" class="btn btn-secondary">
                    <i class="bi bi-people" style="margin-right: 0.4rem;"></i> User Management &rarr;
                </a>
            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/includes/footer.jsp"/>

    <script src="${pageContext.request.contextPath}/js/app.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            checkAdminAccessGuard();
        });
    </script>
</body>
</html>
