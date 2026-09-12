<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/includes/head.jsp">
        <jsp:param name="title" value="Shopping Cart - Online Bookstore"/>
    </jsp:include>
</head>
<body>

    <jsp:include page="/WEB-INF/includes/customer-header.jsp">
        <jsp:param name="activePage" value="cart"/>
    </jsp:include>

    <main class="container">
        <div class="page-header">
            <h1 class="page-title">Shopping Cart</h1>
            <p class="page-subtitle">Review items in your cart before proceeding to checkout.</p>
        </div>

        <div class="card-detail-wrap" style="text-align: center; padding: 4rem 2rem;">
            <div style="font-size: 3.5rem; margin-bottom: 1rem; color: var(--text-muted);"><i class="bi bi-cart-x"></i></div>
            <h2 style="font-size: 1.5rem; font-weight: 800; margin-bottom: 0.5rem;">Your Cart is Currently Empty</h2>
            <p style="color: var(--text-muted); margin-bottom: 1.5rem;">Explore our catalog and add books to start building your order.</p>
            <a href="${pageContext.request.contextPath}/pages/customer/books.jsp" class="btn btn-primary">
                <i class="bi bi-shop" style="margin-right: 0.4rem;"></i> Browse Catalog
            </a>
        </div>
    </main>

    <jsp:include page="/WEB-INF/includes/footer.jsp"/>

    <script src="${pageContext.request.contextPath}/js/core.js"></script>
</body>
</html>
