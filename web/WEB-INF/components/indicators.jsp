<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link
        rel="stylesheet"
        type="text/css"
        href="css/components/indicators.component.css"
/>
<script defer src="js/indicators.js"></script>

<%--@elvariable id="error" type="java.lang.String"--%>
<c:if test="${error != null}">
    <dialog id="error" class="reveal">
        <c:out value="${error}"/>
        <button>Close</button>
    </dialog>
    <%-- Clears Error --%>
    <c:set var="error" scope="application" value="${null}"/>
</c:if>

<c:if test="${cookie.get(\"acceptedCookies\") == null}">
    <dialog id="cookiePrompt">
        <h2>
            From this point on past the home page, this site requires cookies for use
            <br>
        </h2>
        <button
                id="cookiePrompt-yes"
                onclick="document.querySelector('#cookiePrompt').close()"
        >
            I understand and agree
        </button>
    </dialog>
</c:if>

