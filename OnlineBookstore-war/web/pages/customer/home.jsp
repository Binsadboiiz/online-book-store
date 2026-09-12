<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/includes/head.jsp">
        <jsp:param name="title" value="Online Bookstore - Customer Storefront"/>
    </jsp:include>
</head>
<body>

    <jsp:include page="/WEB-INF/includes/customer-header.jsp">
        <jsp:param name="activePage" value="home"/>
    </jsp:include>

    <main class="container">
        <!-- Hero Section -->
        <section class="hero">
            <h1 class="hero-title">Minimalist Knowledge Space.</h1>
            <p class="hero-subtitle">Discover hand-picked books across technology, architecture, science, and literature in our clean monochrome catalog.</p>
            <div style="display: flex; gap: 1rem;">
                <a href="${pageContext.request.contextPath}/pages/customer/books.jsp" class="btn btn-primary">
                    <i class="bi bi-compass" style="margin-right: 0.4rem;"></i> Browse Catalog
                </a>
                <a href="${pageContext.request.contextPath}/pages/customer/cart.jsp" class="btn btn-secondary">
                    <i class="bi bi-cart3" style="margin-right: 0.4rem;"></i> View Cart
                </a>
            </div>
        </section>

        <!-- Featured Section Header -->
        <div class="section-header">
            <div>
                <h2 class="section-title">Featured Titles</h2>
                <p class="section-desc">Hand-selected recommendations for curious minds.</p>
            </div>
            <a href="${pageContext.request.contextPath}/pages/customer/books.jsp" class="btn btn-secondary btn-sm">
                Explore All <i class="bi bi-arrow-right" style="margin-left: 0.3rem;"></i>
            </a>
        </div>

        <!-- Book Grid -->
        <section id="bookGrid" class="book-grid">
            <!-- Rendered dynamically via books.js -->
        </section>
    </main>

    <jsp:include page="/WEB-INF/includes/footer.jsp"/>

    <!-- Book Detail Modal -->
    <div id="detailModal" class="modal-backdrop">
        <div class="modal-content">
            <button class="modal-close" onclick="closeModals()">&times;</button>
            <div id="detailModalBody"></div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/core.js"></script>
    <script src="${pageContext.request.contextPath}/js/books.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            fetchBooks('', 6);
        });
    </script>
</body>
</html>
