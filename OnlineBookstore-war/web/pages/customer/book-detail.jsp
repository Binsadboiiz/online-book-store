<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/includes/head.jsp">
        <jsp:param name="title" value="Book Details - Online Bookstore"/>
    </jsp:include>
</head>
<body>

    <jsp:include page="/WEB-INF/includes/customer-header.jsp">
        <jsp:param name="activePage" value="books"/>
    </jsp:include>

    <main class="container">
        <div style="margin-bottom: 1.5rem;">
            <a href="${pageContext.request.contextPath}/pages/customer/books.jsp" class="btn btn-secondary btn-sm">
                <i class="bi bi-arrow-left"></i> Back to Catalog
            </a>
        </div>

        <div id="standaloneBookDetail" class="card-detail-wrap">
            <!-- Rendered dynamically by books.js -->
        </div>
    </main>

    <jsp:include page="/WEB-INF/includes/footer.jsp"/>

    <script src="${pageContext.request.contextPath}/js/core.js"></script>
    <script src="${pageContext.request.contextPath}/js/books.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            const urlParams = new URLSearchParams(window.location.search);
            const bookId = urlParams.get('id');
            if (bookId) {
                loadStandaloneBookDetail(bookId);
            } else {
                document.getElementById('standaloneBookDetail').innerHTML = '<div class="empty-state"><div class="empty-title">Book ID Missing</div></div>';
            }
        });
    </script>
</body>
</html>
