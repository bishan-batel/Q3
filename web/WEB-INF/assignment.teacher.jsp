<%--@elvariable id="teacher" type="auth.account.Teacher"--%>
<%--@elvariable id="assignment" type="auth.account.Assignment"--%>

<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="err.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>Q3 Home</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="css/assignment.teacher.css"/>
    <script defer src="js/global.js"></script>
    <script defer src="js/libs/jquery/jquery.min.js"></script>
</head>
<body>
<%@include file="components/indicators.jsp" %>
<main>

    <h1 id="assignmentTitle" class="reveal">
        <c:out value="${assignment.type.toString()}"/>
    </h1>

    <form action="home-delete-assignment" method="post" id="delete-classroom">
        <input hidden name="classroomId" value="${assignment.classroomId}"/>
        <input hidden name="assignmentId" value="${assignment.id}"/>
        <input type="submit" value="Delete"/>
    </form>

    <table id="students">
        <tr>
            <th>Student</th>
            <th>Completed</th>

            <fmt:formatNumber var="num" value="${assignment.minimumAccuracy*100}"
                              maxFractionDigits="1"/>
            <th>Accuracy (>${num})</th>
            <th>Questions Done</th>
        </tr>
        <c:forEach var="student" items="${assignment.classroom.students}">
            <tr>
                <td>${student.account.fullName}</td>
                <c:set var="accuracy"
                       scope="page"
                       value="${assignment.getAccuracy(student)}"
                />
                <c:set
                        var="questionsDone"
                        scope="page"
                        value="${assignment.getQuestionsDone(student)}"
                />

                <td>
                        ${assignment.minimumAccuracy <= accuracy
                                &&assignment.minimumQuestions <= questionsDone ? "✓" : "✗"}
                </td>

                <fmt:formatNumber var="num" value="${accuracy*100}"
                                  maxFractionDigits="1"/>
                <td>${num}</td>
                <td>${questionsDone}/${assignment.minimumQuestions}</td>
            </tr>
        </c:forEach>
    </table>

</main>
<%@include file="components/accountBar.component.jsp" %>
</body>
</html>
