<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ include file="WEB-INF/components/head.jsp" %>
<body>
<%@ include file="WEB-INF/components/header.jsp" %>

<div>
<h2>Error Processing Request</h2>
<p>Calm down.  Don't rage quit.  Go get a coffee or a donut.
<br />
Whatever you screwed up has been caught by an error handler,
<br />
so you can still fix it.  Relax with this quote.
</p>

<div>
<script type="text/javascript" src="http://www.brainyquote.com/link/quotebr.js"></script>
</div>

<div>
<h2>Error Info</h2>
<p><b>Error Code:</b> ${ requestScope['errcode'] }</p>
<p><b>Message:</b> ${ requestScope['msg'] }</p>
<p><b>Exception:</b> ${ requestScope['error'] }</p>
</div>

<div>
<h2>Exception Info</h2>
<p><b>URI:</b> ${ pageContext.errorData.requestURI }</p>
<p><b>HTTP Code:</b> ${ pageContext.errorData.statusCode}</p>
<p><b>Exception:</b> ${ pageContext.exception }</p>
<p><b>Stack Trace:</b></p>
<code>
<c:forEach var="trace" items="${ pageContext.exception.stackTrace }">
${ trace }
</c:forEach>
</code>
</div>

<p><a href="${ pageContext.request.contextPath }/">&#60;&#60; Home</a></p>

</div>

<%@ include file="WEB-INF/components/footer.jsp" %>
</body>
</html>