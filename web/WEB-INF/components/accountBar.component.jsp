<%--@elvariable id="account" type="auth.account.Account"--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" type="text/css" href="css/components/accountBar.component.css"/>

<nav id="accountBar" class="reveal">
    <span id="accountBar-name">
        <c:out value="${account.firstName}"/> <c:out value="${account.lastName}"/>
    </span>
    <form id="accountBar-logout" method="post" action="logout">
        <input type="submit" value="Logout"/>
    </form>
</nav>