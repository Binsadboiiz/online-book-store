<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Automatically forward root traffic to Customer storefront
    response.sendRedirect(request.getContextPath() + "/pages/customer/home.jsp");
%>
