<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<header class="navbar">
    <div class="nav-container">
        <a href="${pageContext.request.contextPath}/index.jsp" class="brand">
            <span class="brand-icon">B</span>
            <span>BOOKSTORE</span>
        </a>
        
        <nav class="nav-menu">
            <a href="${pageContext.request.contextPath}/index.jsp" class="nav-link ${param.activePage == 'home' ? 'active' : ''}">Home</a>
            <a href="${pageContext.request.contextPath}/pages/books.jsp" class="nav-link ${param.activePage == 'books' ? 'active' : ''}">Catalog</a>
            <a href="${pageContext.request.contextPath}/pages/admin-books.jsp" class="nav-link ${param.activePage == 'admin' ? 'active' : ''}">Management</a>
        </nav>

        <div class="nav-actions">
            <a href="${pageContext.request.contextPath}/pages/books.jsp" class="btn btn-secondary btn-sm">Search Books</a>
            <button class="btn btn-primary btn-sm" onclick="alert('Sign In feature coming soon!')">Sign In</button>
        </div>
    </div>
</header>
