<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${ sessionScope['loggedIn'] }">
	<c:redirect url="/Logout.do"/>
</c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ include file="WEB-INF/components/head.jsp" %>
<body>
<%@ include file="WEB-INF/components/header.jsp" %>

<div class="devicemain">

<div class="deviceclass"><h2>Login Form</h2></div>
<p>Only registered users can use this system.</p>
<c:if test="${not empty param.error }">
<p class="loginError"><strong>Login Error.</strong>  Check your credentials.</p>
</c:if>
<form action="j_security_check" method=post>
    <p><strong>Username: </strong>
    <input type="text" name="j_username" size="25">
    <p><p><strong>Password: </strong>
    <input type="password" size="25" name="j_password">
    <p><p>
    <input type="submit" value="Submit">
    <input type="reset" value="Reset">
</form>
</div>

<%@ include file="WEB-INF/components/footer.jsp" %>
</body>
</html>