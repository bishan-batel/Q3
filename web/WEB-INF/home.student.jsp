<%--@elvariable id="student" type="auth.account.Student"--%>
<%--@elvariable id="account" type="auth.account.Account"--%>
<%--@elvariable id="classroom" type="java.util.Optional<auth.account.Classroom>"--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <title>Q3 Home</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="css/home.student.css"/>
    <script defer src="js/global.js"></script>
</head>
<body>
<main id="studentMain">
    <%@include file="components/accountBar.component.jsp" %>
    <%@include file="components/indicators.jsp" %>

    <c:choose>
        <c:when test="${classroom.present}">
            Classroom ${classroom.get().name}
        </c:when>
        <c:otherwise>
            <div class="no-classroom">
                <h1>
                    You are not in a classroom
                </h1>
                <form>
                    <h2>Join a classroom</h2>

                    <label for="joinClassroom">Enter a Classroom Join Code</label>
                    <input id="joinClassroom" maxlength="6" required minlength="6"/>
                    <input type="submit" value="Join">
                </form>
            </div>
        </c:otherwise>
    </c:choose>
</main>
</body>
</html>
