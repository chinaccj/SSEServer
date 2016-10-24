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
  <script type="text/javascript">
    var notificationBaseUrl = "http://localhost:8085/loan/sse";
    var source = new EventSource(notificationBaseUrl + "/events/connect");
    source.onopen = function (e) {
      console.log("connection opened");
    }
    source.onmessage = function (e) {
      console.log("received message ...");
      document.body.innerHTML += e.data + '<br>';
    }

  </script>
</head>
<body>

</body>
</html>