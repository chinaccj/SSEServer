<%--
  Created by IntelliJ IDEA.
  User: yumo
  Date: 16/10/18
  Time: 下午7:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title></title>
</head>
<body>
<c:if test="${not empty successMsg}">
    <p><c:out value="${successMsg}"/></p>
</c:if>
<form action="${pageContext.request.contextPath}/events/broadcast.do" method=“post”>
    Mesaage:<input name="eventMsg" type="text" value=""/>
    <br><input type="submit" name="submit"/>
</form>
</body>
</html>