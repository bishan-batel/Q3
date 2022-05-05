<%--@elvariable id="teacher" type="auth.account.Teacher"--%>
<%--@elvariable id="account" type="auth.account.Account"--%>
<%--@elvariable id="classrooms" type="auth.account.Classroom[]"--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>TODO supply a title</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="css/home.css"/>
    <script defer src="js/global.js"></script>
    <script defer src="js/libs/jquery/jquery.min.js"></script>
</head>
<body>
<main>
    <%@ include file="components/accountBar.component.jsp" %>

    <ul id="classrooms">
        <c:forEach var="classroom" items="${classrooms}">
            ${classroom.name}
        </c:forEach>
    </ul>

    <

    <button onclick="$('#create-classroom')[0].showModal()">Create a new
        classroom
    </button>
    <dialog id="create-classroom">
        <h1>Create a new Classroom</h1>
        <form method="post" action="home-create-classroom">
            <label for="classroomName">Name</label>
            <input required id="classroomName" name="classroomName"/>

            <input type="submit" value="Create"/>

        </form>
        <button class="closeButton" onclick="$('#create-classroom')[0].close()">
            Back
        </button>
    </dialog>
</main>
</body>
</html>
