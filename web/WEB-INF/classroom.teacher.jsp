<%--@elvariable id="teacher" type="auth.account.Teacher"--%>
<%--@elvariable id="account" type="auth.account.Account"--%>
<%--@elvariable id="classroom" type="auth.account.Classroom"--%>
<%--@elvariable id="assignments" type="auth.account.Assignment[]"--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <title>Q3 Home</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="css/classroom.css"/>
    <script defer src="js/global.js"></script>
    <script defer src="js/libs/jquery/jquery.min.js"></script>
</head>
<body>
<main>
    <%@include file="components/accountBar.component.jsp" %>
    <%@include file="components/indicators.jsp" %>

    <h1 id="classTitle" class="reveal">
        <c:out value="${classroom.name}"/>
    </h1>

    <div id="assignments">
        <c:forEach items="${assignments}" var="assignment">
        <a href="assignment?id=${assignment.id}" class="assignment">
                <c:out value="${assignment.type.toString()}"/>

            <c:if test="${assignment.dateDue.present}">

            </c:if>
            <span class="dueDate">
                    ${assignment.dateDue.get()}
            </span>
                <%--            </a>--%>
            </c:forEach>
    </div>
</main>
</body>
</html>
