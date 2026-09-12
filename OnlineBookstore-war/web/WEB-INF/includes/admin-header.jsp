<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<header class="navbar navbar-admin">
    <div class="nav-container">
        <a href="${pageContext.request.contextPath}/pages/admin/dashboard.jsp" class="brand">
            <span class="brand-icon brand-icon-admin"><i class="bi bi-shield-lock-fill"></i></span>
            <span>ADMIN PORTAL</span>
        </a>
        
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/pages/admin/dashboard.jsp" class="nav-link ${param.activePage == 'dashboard' ? 'active' : ''}">
                <i class="bi bi-speedometer2"></i> Dashboard
            </a>
            <a href="${pageContext.request.contextPath}/pages/admin/books.jsp" class="nav-link ${param.activePage == 'books' ? 'active' : ''}">
                <i class="bi bi-journals"></i> Book Management
            </a>
            <a href="${pageContext.request.contextPath}/pages/admin/users.jsp" class="nav-link ${param.activePage == 'users' ? 'active' : ''}">
                <i class="bi bi-people"></i> Users
            </a>
        </nav>

        <div class="nav-actions">
            <button class="btn btn-secondary btn-sm" onclick="toggleUserRole()" title="Switch Role for Testing">
                <i class="bi bi-person-badge"></i> Role: <strong id="currentRoleBadge">Admin</strong> <i class="bi bi-arrow-repeat"></i>
            </button>
            <a href="${pageContext.request.contextPath}/pages/customer/home.jsp" class="btn btn-primary btn-sm">
                <i class="bi bi-shop"></i> Customer Store
            </a>
        </div>
    </div>
</header>
