<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.Calendar,java.text.SimpleDateFormat" %>
<%
Calendar cal = Calendar.getInstance();
SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
String time = sdf.format(cal.getTime());
%>
<div class="footer">
	<hr />
	<p>© 2014 <a href="http://www.eakjb.com">Eakjb Productions</a>. All Rights Reserved.
	<br />
	Server Time: <%= time %></p>
</div>