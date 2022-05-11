<%@ page import="question.ProblemType" %>
<%--@elvariable id="teacher" type="auth.account.Teacher"--%>
<%--@elvariable id="account" type="auth.account.Account"--%>
<%--@elvariable id="classroom" type="auth.account.Classroom"--%>
<%--@elvariable id="assignments" type="auth.account.Assignment[]"--%>

<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="err.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <title>Q3 Home</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="css/classroom.teacher.css"/>
    <script defer src="js/global.js"></script>
    <script defer src="js/libs/jquery/jquery.min.js"></script>
    <script defer src="js/classroom.teacher.js"></script>
</head>
<body>
<%@include file="components/indicators.jsp" %>
<main>

    <h1 id="classTitle" class="reveal">
        <c:out value="${classroom.name}"/> (${classroom.id})
    </h1>

    <form method="post" action="home-delete-classroom" id="delete-classroom">
        <input required hidden name="classroomId" value="${classroom.id}"/>
        <input type="submit" value="Delete">
    </form>

    <div id="students" class="reveal">
        <div id="confirmed-students">
            <h2>Students</h2>
            <c:forEach var="student" items="${classroom.students}">
                <div class="student">
                    <c:out value="${student.account.fullName}"/>
                </div>
            </c:forEach>
        </div>

        <div id="requesting-students">
            <h2>Join Requests</h2>
            <c:forEach var="request" items="${classroom.joinRequests}">
                <form class="request" method="post">
                    <input hidden name="studentId" value="${request.uid}"/>
                    <input type="submit"
                           value="✓"
                           formaction="home-accept-join-req"
                    />
                    <input type="submit"
                           value="✗"
                           formaction="home-reject-join-req"
                    />
                    <span class="request-name">${request.student.account.fullName}</span>
                </form>
            </c:forEach>
        </div>
    </div>
    <div id="assignments" class="reveal">
        <c:forEach items="${classroom.assignments}" var="assignment">
            <a href="assignment?id=${assignment.id}" class="assignment">
                    ${assignment.type.toString()}
                <span>
                    <c:out value="${assignment.minimumQuestions}"/> Questions
                </span>

                <span class="dueDate">
                        ${assignment.dateDue}
                </span>

                <form action="home-delete-assignment" method="post">
                    <input hidden name="classroomId" value="${classroom.id}"/>
                    <input hidden name="assignmentId" value="${assignment.id}"/>
                    <input type="submit" value="Delete"/>
                </form>
            </a>
        </c:forEach>
        <button
                onclick="document.querySelector('#create-assignment').showModal()"
        >
            Create a new assignment
        </button>
    </div>

    <dialog id="create-assignment">
        <form action="home-create-assignment" method="post">
            <input hidden name="classroomId" value="${classroom.id}"/>

            <label>
                Assignment Problems
                <select required name="type">
                    <c:forEach
                            var="problem"
                            items="<%=ProblemType.values()%>"
                    >
                        <option value="${problem.toString()}">
                            <c:out value="${problem.toString()}"/>
                        </option>
                    </c:forEach>
                </select>
            </label>

            <label>
                Minimum Questions
                <input required
                       name="minimumQuestions"
                       type="number"
                       min="0"
                       max="100"
                       step="1"
                       value="10"
                />
            </label>

            <label>
                Minimum Accuracy
                <input id="create-assignment-accuracy"
                       required
                       name="minimumAccuracy"
                       type="range"
                       value="0.9"
                       min="0"
                       step=".01"
                       max="1"
                />
                <span>90%</span>
            </label>
            <label>
                Due Date
                <input name="due" required type="date">
            </label>

            <input type="submit" value="Create"/>
        </form>
        <button
                onclick="document.querySelector('#create-assignment').close()"
        >
            Close
        </button>
    </dialog>
</main>
<%@include file="components/accountBar.component.jsp" %>
</body>
</html>
