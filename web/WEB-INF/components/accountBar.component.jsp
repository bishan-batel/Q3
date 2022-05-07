<%--@elvariable id="account" type="auth.account.Account"--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" type="text/css" href="css/components/accountBar.component.css"/>

<nav id="accountBar">
    <span id="accountBar-name">
        <c:out value="${account.firstName}"/> <c:out value="${account.lastName}"/>
    </span>

    <%--@elvariable id="teacher" type="auth.account.Teacher"--%>
    <c:if test="${teacher != null}">
        <a class="accountBar-redirect" href="/Q3/home">Classroom List</a>
    </c:if>

    <form id="accountBar-logout" method="post" action="logout">
        <input type="submit" value="Logout"/>
    </form>
</nav>