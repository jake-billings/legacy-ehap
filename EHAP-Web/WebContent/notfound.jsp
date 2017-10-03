<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<c:set var="loggedIn" value="${ true }" scope="session"/>
<%@ include file="/WEB-INF/components/head.jsp" %>
<body>
<%@ include file="/WEB-INF/components/header.jsp" %>

<div class="devicemain">

<h1>Page Not Found</h1>
<p>The requested resource could not be located.</p>
<p><a href="${ pageContext.request.contextPath }/">&#60;&#60; Home</a></p>
</div>

<%@ include file="/WEB-INF/components/footer.jsp" %>
</body>
</html>