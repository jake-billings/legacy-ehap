<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="header">
	<div class="logoarea">
		<a href="${pageContext.request.contextPath}">
			<img class="icon" src="logo.png" />
		</a>
		<a class="title" href="${pageContext.request.contextPath}">EHAP</a>
	</div>
	<div class="pageName">
		<h1>Control Panel</h1>
	</div>
	<div class="nav">
		<ul class="nav">
			<li class="item"><c:choose>
					<c:when test="${ sessionScope['loggedIn'] }">
						<a href="${pageContext.request.contextPath}/Logout.do"
							class="navbarlink">Logout</a>
					</c:when>
					<c:otherwise>
						<a href="${pageContext.request.contextPath}/login.jsp"
							class="navbarlink">Login</a>
					</c:otherwise>
				</c:choose></li>
			<li class="item"><a href="${pageContext.request.contextPath}"
				class="navbarlink">Home</a></li>
		</ul>
	</div>
</div>