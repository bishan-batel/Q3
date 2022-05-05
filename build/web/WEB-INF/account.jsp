<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>TODO supply a title</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="css/account.css"/>
    <script defer src="js/global.js"></script>
    <script defer src="js/account.js"></script>

</head>
<body>
<main>

    <%--@elvariable id="error" type="java.lang.String"--%>
    <c:if test="${error != null}">
        <dialog id="error" class="reveal">
            <c:out value="${error}"/>
            <button onclick="closeDialog()">Close</button>
        </dialog>
        <%-- Clears --%>
        <c:set var="error" scope="application" value="${null}"/>
    </c:if>
    <form id="signup" method="post" action="register" class="reveal">
        <h1>Sign Up</h1>
        <label for="email-signup">Email</label>
        <input required id="email-signup" type="email" name="email" maxlength="254"/>

        <label for="firstName">First Name</label>
        <input required id="firstName" name="firstName" maxlength="254"/>
        <label for="lastName">Last Name</label>
        <input required id="lastName" name="lastName" maxlength="254"/>

        <label for="signup-account-type">Account Type</label>
        <select id="signup-account-type" name="accountType" required>
            <option value="student">Student</option>
            <option value="teacher">Teacher</option>
        </select>

        <label for="password">Password</label>
        <input required id="password" type="password" name="password" maxlength="254"/>

        <input type="submit" value="Sign Up"/>
    </form>

    <form id="login" method="post" action="login" class="reveal">
        <h1>Login</h1>
        <label for="email-login">Email</label>
        <input required id="email-login" type="email" name="email" maxlength="254"/>
        <label for="password-login">Password</label>
        <input required id="password-login" type="password" name="password" maxlength="254"/>
        <input type="submit" value="Log in"/>
    </form>
</main>
</body>
</html>
