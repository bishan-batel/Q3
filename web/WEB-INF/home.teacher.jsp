<%--@elvariable id="teacher" type="auth.account.Teacher"--%>
<%--@elvariable id="account" type="auth.account.Account"--%>
<%--@elvariable id="classrooms" type="auth.account.Classroom[]"--%>

<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="err.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <title>Q3 Home</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="css/home.teacher.css"/>
    <script defer src="js/global.js"></script>
</head>
<body>
<%@include file="components/indicators.jsp" %>
<main>
    <ul id="classroomList">
        <c:forEach var="classroom" items="${classrooms}">
            <li
                    class="classroom"
                    onclick="window.location.href ='/Q3/classroom?id=${classroom.id}'"
            >
                <h2>
                    <c:out value="${classroom.name}"/>
                </h2>

                <span class="studentCount">
                    <c:catch var="studentCountFailure">
                        <c:out value="${classroom.studentCount}"/> Students
                    </c:catch>
                    <c:if test="${studentCountFailure != null}">
                        Failed to get student count
                        ${studentCountFailure.printStackTrace()}
                    </c:if>
                </span>
            </li>
        </c:forEach>

        <c:if test="${fn:length(classrooms) == 0}">
            <span class="error">You have no classes.</span>
        </c:if>
    </ul>

    <div id="classControlPanel">
        <button onclick="document.querySelector('#create-classroom').showModal()">
            Create a new classroom
        </button>


        <button
                onclick="document.querySelector('#delete-classroom').showModal()"
                <c:if test="${fn:length(classrooms) == 0}">disabled</c:if>
        >
            Delete a Classroom
        </button>
    </div>

    <dialog id="create-classroom">
        <h1>Create a new Classroom</h1>
        <form method="post" action="home-create-classroom">
            <label for="classroomName">Name</label>
            <input required id="classroomName" name="classroomName"/>

            <input type="submit" value="Create"/>

        </form>
        <button class="closeButton"
                onclick="document.querySelector('#create-classroom').close()"
        >
            Back
        </button>
    </dialog>

    <c:if test="${fn:length(classrooms) > 0}">
        <dialog id="delete-classroom">
            <h1>Delete a Classroom</h1>
            <form method="post" action="home-delete-classroom">
                <label for="classroomToDelete">Name</label>
                <select id="classroomToDelete" name="classroomId">
                    <c:forEach var="classroom" items="${classrooms}">
                        <option value="${classroom.id}">
                            <c:out value="${classroom.name}"/>
                        </option>
                    </c:forEach>
                </select>
                <input type="submit" value="Delete"/>
            </form>
            <button class="closeButton"
                    onclick="document.querySelector('#delete-classroom').close()"
            >
                Close
            </button>

        </dialog>
    </c:if>
</main>
<%@include file="components/accountBar.component.jsp" %>
</body>
</html>
