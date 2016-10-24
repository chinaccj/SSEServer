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
    function init(){
      document.getElementById('foo').innerHTML = "init data";
    }

    var notificationBaseUrl = "http://localhost:8080";
   // var source = new EventSource(notificationBaseUrl + "/events/connect.do");
    //var source = new EventSource(notificationBaseUrl + "/asyncservlet2");
    var source = new EventSource(notificationBaseUrl + "/eventconnect");


//    var source = new EventSource(notificationBaseUrl + "/connect");



    source.onopen = function (e) {
      console.log("connection opened");
    }

    source.onmessage = function (e) {
      //收到消息后
      console.log("received message ...");
      document.getElementById('foo').innerHTML = e.data;
    }

  </script>
</head>
<body onload="init()">
Message: <span id="foo"></span>
</body>
</html>