<%--@elvariable id="student" type="auth.account.Student"--%>
<%--@elvariable id="account" type="auth.account.Account"--%>
<%--@elvariable id="classroom" type="auth.account.Classroom"--%>

<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="err.jsp" %>
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
<%@include file="components/indicators.jsp" %>
<main id="studentMain">

    <c:choose>
        <c:when test="${classroom != null}">
            <h1>
                Classroom <c:out value="${classroom.name}"/>
            </h1>
            <div id="assignments">
                <h2>
                    Assignments
                </h2>
                <div class="scroll">
                    <c:forEach var="assignment" items="${classroom.assignments}">
                        <a href="assignment?id=${assignment.id}" class="assignment">
                            <c:out value="${assignment.type.toString()}"/>
                            <span class="requirements">
                                <b>${assignment.minimumQuestions}</b>
                                Questions with accuracy of at least
                                    <b>${assignment.minimumAccuracy*100}%</b>
                            </span>

                            <span class="dueDate">
                                Due ${assignment.dateDue}
                            </span>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <%--     When student is not in classroom       --%>
            <div class="no-classroom">
                <h1>
                    You are not in a classroom
                </h1>

                    <%--When has a pending join request request--%>
                <c:if test="${student.joinRequest.present}">
                    <h2>
                        Pending to join classroom with code
                        <span class="class-code">${student.joinRequest.get().classroomId}</span>
                    </h2>
                    <form method="post" action="home-cancel-join-req">
                        <input type="submit" value="Cancel request"/>
                    </form>
                </c:if>

                    <%--No Pending join request--%>
                <c:if test="${!student.joinRequest.present}">
                    <form method="post" action="home-send-join-req">
                        <h2>Join a classroom</h2>

                        <label for="joinClassroom">Enter a Classroom Join Code</label>
                        <input id="joinClassroom"
                               name="classroomId"
                               maxlength="6"
                               required
                               minlength="6"
                        />
                        <input type="submit" value="Join">
                    </form>
                </c:if>
            </div>
        </c:otherwise>
    </c:choose>
</main>
<%@include file="components/accountBar.component.jsp" %>
</body>
</html>
