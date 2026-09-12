<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<header class="navbar">
    <div class="nav-container">
        <a href="${pageContext.request.contextPath}/pages/customer/home.jsp" class="brand">
            <span class="brand-icon"><i class="bi bi-journal-bookmark-fill"></i></span>
            <span>BOOKSTORE</span>
        </a>
        
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/pages/customer/home.jsp" class="nav-link ${param.activePage == 'home' ? 'active' : ''}">
                <i class="bi bi-house-door"></i> Home
            </a>
            <a href="${pageContext.request.contextPath}/pages/customer/books.jsp" class="nav-link ${param.activePage == 'books' ? 'active' : ''}">
                <i class="bi bi-grid"></i> Catalog
            </a>
            <a href="${pageContext.request.contextPath}/pages/customer/cart.jsp" class="nav-link ${param.activePage == 'cart' ? 'active' : ''}">
                <i class="bi bi-cart3"></i> Cart
            </a>
        </nav>

        <div class="nav-actions">
            <!-- Role Toggle Helper -->
            <button class="btn btn-secondary btn-sm" onclick="toggleUserRole()" title="Switch Role for Testing">
                <i class="bi bi-person-badge"></i> Role: <strong id="currentRoleBadge">Customer</strong> <i class="bi bi-arrow-repeat"></i>
            </button>
            <button class="btn btn-primary btn-sm" onclick="alert('Sign In modal coming soon!')">
                <i class="bi bi-box-arrow-in-right"></i> Sign In
            </button>
        </div>
    </div>
</header>
