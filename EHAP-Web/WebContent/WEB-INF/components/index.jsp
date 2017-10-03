<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ include file="head.jsp"%>
<body>
	<%@ include file="header.jsp"%>

	<div class="main">
		<c:choose>
			<c:when test="${ fn:length(requestScope['clientarray'])<1 }">
				<div class="devicemain">
					<div class="devicenone">
						<p>No EHAP Devices.</p>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<c:set var="count" value="${ 0 }" />
				<c:forEach items="${ requestScope['formattedclientarray'] }"
					var="d1">
					<c:set var="d"
						value="${fn:split(d1,requestScope['clientinfoseparator'])}" />
					<c:set var="draw"
						value="${fn:split(requestScope['clientarray'][count],requestScope['clientinfoseparator'])}" />
					<div class="devicemain">
						<div class="devicestate">
							<fmt:parseNumber var="clientState" type="number"
								value="${ d[requestScope['clientstateindex']] }" />
							<c:choose>
								<c:when test="${ clientState<=0 }">
									<c:choose>
										<c:when
											test="${ fn:contains(draw[requestScope['clientaddrindex']],'door://') }">
											Closed
										</c:when>
										<c:otherwise>
											Off
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:when test="${ clientState==1 }">
									<c:choose>
										<c:when
											test="${ fn:contains(draw[requestScope['clientaddrindex']],'door://') }">
											Open
										</c:when>
										<c:otherwise>
											On
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:when test="${ clientState==3 }">
									Transitioning
									<script type="text/javascript">
										refresh(1);
									</script>
								</c:when>
								<c:otherwise>
									<c:out value="${ clientState }" />
								</c:otherwise>
							</c:choose>
						</div>
						<div class="switch">
							<form method="POST" action="SendEHAPRequest.do" class="deviceoff">
								<input type="hidden" value="False" name="data"> <input
									type="hidden"
									value="${ draw[requestScope['clientaddrindex']] }"
									name="address"> <input type="hidden" value="SET"
									name="type"> <input type="submit"
									value="<c:choose><c:when	test="${ fn:contains(draw[requestScope['clientaddrindex']],'door://') }">Close</c:when><c:otherwise>Off</c:otherwise></c:choose>">
							</form>
							<form method="POST" action="SendEHAPRequest.do" class="deviceon">
								<input type="hidden" value="True" name="data"> <input
									type="hidden"
									value="${ draw[requestScope['clientaddrindex']] }"
									name="address"> <input type="hidden" value="SET"
									name="type"> <input type="submit"
									value="<c:choose><c:when	test="${ fn:contains(draw[requestScope['clientaddrindex']],'door://') }">Open</c:when><c:otherwise>On</c:otherwise></c:choose>">
							</form>
						</div>
						<div class="deviceaddr">${ d[requestScope['clientaddrindex']] }</div>
						<c:set var="count" value="${ count+1 }" />
					</div>
				</c:forEach>
				<br />
			</c:otherwise>
		</c:choose>
	</div>

	<%@ include file="footer.jsp"%>
</body>
</html>