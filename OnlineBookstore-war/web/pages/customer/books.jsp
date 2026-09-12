<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/includes/head.jsp">
        <jsp:param name="title" value="Book Catalog - Customer Storefront"/>
    </jsp:include>
</head>
<body>

    <jsp:include page="/WEB-INF/includes/customer-header.jsp">
        <jsp:param name="activePage" value="books"/>
    </jsp:include>

    <main class="container">
        <div class="page-header">
            <h1 class="page-title">Book Catalog</h1>
            <p class="page-subtitle">Search, filter, and discover books across all categories.</p>
        </div>

        <!-- Search & Filter Bar -->
        <section class="filter-bar">
            <div class="search-box">
                <input type="text" id="searchInput" class="input-field" placeholder="Search by title, author, or ISBN...">
            </div>
            <div style="display: flex; gap: 0.75rem;">
                <select id="categoryFilter" class="select-field" onchange="fetchBooks()">
                    <option value="">All Categories</option>
                </select>
                <button class="btn btn-secondary" onclick="fetchBooks()">
                    <i class="bi bi-arrow-clockwise"></i> Refresh
                </button>
            </div>
        </section>

        <!-- Book Grid -->
        <section id="bookGrid" class="book-grid">
            <!-- Rendered dynamically by books.js -->
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
            fetchBooks();
        });
    </script>
</body>
</html>
