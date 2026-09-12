<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/includes/head.jsp">
        <jsp:param name="title" value="User Management - Admin Portal"/>
    </jsp:include>
</head>
<body>

    <jsp:include page="/WEB-INF/includes/admin-header.jsp">
        <jsp:param name="activePage" value="users"/>
    </jsp:include>

    <main class="container">
        <!-- RBAC Guard container -->
        <div id="adminAccessGuard">
            <div class="page-header">
                <h1 class="page-title">User Management</h1>
                <p class="page-subtitle">Restricted to Admin role. Manage customer accounts, roles, and status.</p>
            </div>

            <div class="card-detail-wrap" style="text-align: center; padding: 4rem 2rem;">
                <div style="font-size: 3.5rem; margin-bottom: 1rem; color: var(--text-muted);"><i class="bi bi-people-fill"></i></div>
                <h2 style="font-size: 1.5rem; font-weight: 800; margin-bottom: 0.5rem;">User Management Module</h2>
                <p style="color: var(--text-muted); margin-bottom: 1.5rem;">Manage registered customers and administrative access.</p>
                <a href="${pageContext.request.contextPath}/pages/admin/books.jsp" class="btn btn-primary">
                    <i class="bi bi-journals" style="margin-right: 0.4rem;"></i> Go to Book Management
                </a>
            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/includes/footer.jsp"/>

    <script src="${pageContext.request.contextPath}/js/core.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            checkAdminAccessGuard();
        });
    </script>
</body>
</html>
