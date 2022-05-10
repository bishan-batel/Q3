<%--@elvariable id="account" type="auth.account.Account"--%>
<%--@elvariable id="student" type="auth.account.Teacher"--%>
<%--@elvariable id="teacher" type="auth.account.Teacher"--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" type="text/css" href="css/components/accountBar.component.css"/>

<nav id="accountBar">
        <span id="accountBar-name">
            <c:out value="${account.firstName}"/> <c:out value="${account.lastName}"/>
        </span>

    <c:choose>
        <c:when test="${teacher != null}">
            <a class="accountBar-redirect" href="/Q3/home">Classroom List</a>
        </c:when>
        <c:when test="${student != null}">
            <a class="accountBar-redirect" href="/Q3/home">Back to Classroom</a>
        </c:when>
        <c:otherwise>
            Failed to load account bar
        </c:otherwise>
    </c:choose>

    <form id="accountBar-logout" method="post" action="logout">
        <input type="submit" value="Logout"/>
    </form>
    <img id="accountBar-spacer" alt="" src="images/waves/transition2.svg"/>
</nav>
