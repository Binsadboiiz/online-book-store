<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/includes/head.jsp">
        <jsp:param name="title" value="Book Management - Admin Portal"/>
    </jsp:include>
</head>
<body>

    <jsp:include page="/WEB-INF/includes/admin-header.jsp">
        <jsp:param name="activePage" value="books"/>
    </jsp:include>

    <main class="container">
        <!-- RBAC Guard container -->
        <div id="adminAccessGuard">
            <div class="page-header" style="display: flex; justify-content: space-between; align-items: flex-end;">
                <div>
                    <h1 class="page-title">Book Management</h1>
                    <p class="page-subtitle">Restricted to Admin role. Manage book catalog, inventory stock, and pricing.</p>
                </div>
                <button class="btn btn-primary" onclick="openAddBookModal()">
                    <i class="bi bi-plus-lg" style="margin-right: 0.4rem;"></i> Add New Book
                </button>
            </div>

            <!-- Book Inventory Table -->
            <section class="card-table-wrap">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Book Title & Author</th>
                            <th>ISBN Code</th>
                            <th>Price</th>
                            <th>Stock</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="adminBookTableBody">
                        <!-- Rendered dynamically via admin-books.js -->
                    </tbody>
                </table>
            </section>
        </div>
    </main>

    <jsp:include page="/WEB-INF/includes/footer.jsp"/>

    <!-- Add/Edit Book Modal -->
    <div id="addBookModal" class="modal-backdrop">
        <div class="modal-content">
            <button class="modal-close" onclick="closeModals()">&times;</button>
            <h2 id="modalFormTitle" style="font-size: 1.35rem; font-weight: 800; margin-bottom: 1.25rem;">Add New Book</h2>
            
            <form id="addBookForm">
                <input type="hidden" name="bookId" id="bookIdInput">

                <div class="form-group">
                    <label class="form-label">Book Title *</label>
                    <input type="text" name="title" class="input-field" required placeholder="Enter book title">
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">ISBN Code</label>
                        <input type="text" name="isbn" class="input-field" placeholder="e.g. 978-0132350884">
                    </div>
                    <div class="form-group">
                        <label class="form-label">List Price ($/VND) *</label>
                        <input type="number" name="price" class="input-field" required min="0" step="1000" placeholder="250000">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Discount Price</label>
                        <input type="number" name="discountPrice" class="input-field" min="0" step="1000" placeholder="200000">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Stock Quantity *</label>
                        <input type="number" name="stockQuantity" class="input-field" required min="0" value="10">
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label">Description</label>
                    <textarea name="description" class="input-field" rows="3" placeholder="Enter book summary or description..."></textarea>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Cover Image URL</label>
                        <input type="url" name="coverImage" class="input-field" placeholder="https://example.com/cover.jpg">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Published Year</label>
                        <input type="number" name="publishedYear" class="input-field" min="1000" max="2099" value="2024">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Pages</label>
                        <input type="number" name="pages" class="input-field" min="1" placeholder="350">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Language</label>
                        <input type="text" name="language" class="input-field" value="English">
                    </div>
                </div>

                <div style="display: flex; gap: 0.75rem; justify-content: flex-end; margin-top: 1.5rem;">
                    <button type="button" class="btn btn-secondary" onclick="closeModals()">Cancel</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-check2-circle" style="margin-right: 0.3rem;"></i> Save Book
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/core.js"></script>
    <script src="${pageContext.request.contextPath}/js/admin-books.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            if (checkAdminAccessGuard()) {
                fetchAdminBookTable();
            }
        });
    </script>
</body>
</html>
