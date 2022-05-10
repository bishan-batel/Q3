<%@ page
        import="auth.account.AssignmentLog" %><%--@elvariable id="student" type="auth.account.Student"--%>
<%--@elvariable id="classroom" type="auth.account.Classroom"--%>
<%--@elvariable id="assignment" type="auth.account.Assignment"--%>
<%--@elvariable id="problem" type="question.Problem"--%>

<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="err.jsp" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>Q3 Assignment</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="css/assignment.css"/>
    <script defer src="js/global.js"></script>
</head>
<body>
<%@include file="components/indicators.jsp" %>
<main>
    <h1>${assignment.type.toString()}</h1>

    <div id="stats">
        <span id="accuracy">
            <fmt:formatNumber
                    var="num"
                    value="${assignment.getAccuracy(student)*100}"
                    maxFractionDigits="1"
            />
            ${num}% Correct
        </span>
        <span id="questions-done">
            ${assignment.getQuestionsDone(student)} out of ${assignment.minimumQuestions}
            Questions Completed
        </span>
        <%--        ${problem.correctAnswer}--%>
    </div>


    <form id="response-form" method="post" action="assignment-submit">
        <h2>${problem.questionText}</h2>
        <input hidden name="id" value="${assignment.id}"/>

        <c:if test="${problem.graphCount > 0}">
            <div id="graphs">
                <c:forEach var="graph" items="${problem.graphsHTML}">
                    ${graph}
                </c:forEach>
            </div>
        </c:if>

        <span>
        <c:choose>
            <c:when test="${problem.answerPrompt.freeText}">
                ${problem.answerPrompt.mainPrompt.replace("{input}","<input required type=\"text\" name=\"response\" />")}
            </c:when>
            <c:otherwise>
                ${problem.answerPrompt.mainPrompt}
                <select required name="response">
                    <c:forEach var="option" items="${problem.answerPrompt.options}">
                        <option value="${option}">
                                ${option}
                        </option>
                    </c:forEach>
                </select>
            </c:otherwise>
        </c:choose>
        </span>
        <input type="submit" value="Submit Answer"/>
    </form>
</main>
<%@include file="components/accountBar.component.jsp" %>
</body>
</html>
