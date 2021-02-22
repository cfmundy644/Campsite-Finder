<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
    <title>Trip Information</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<ul>
    <c:forEach var="facInRad" items="${facsInRad}">
        <li><c:out value="${facInRad.rgFacilityName}"/></li>
    </c:forEach>
</ul>
</body>
</html>