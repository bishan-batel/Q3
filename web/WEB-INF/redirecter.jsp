<%--@elvariable id="redirectTo" type="java.lang.String"--%>

<%@page contentType="text/html" pageEncoding="UTF-8" errorPage="err.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Redirect</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="refresh" content="0;url='/Q3/<c:out value="${redirectTo}"/>'">
    <link rel="stylesheet" href="css/colors.css">
</head>
Redirecting. . .
</html>
