<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>TODO List JSP</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>TODO List JSP</h1>
<c:if test="${not empty errorMessage}">
    <hr>
        <div>${errorMessage}</div>
    <hr>
</c:if>
<form action="${pageContext.request.contextPath}/" method="POST">
    <input name="text" type="text">
    <button name="action" value="create" type="submit">Создать</button>
    <c:if test="${not empty createErrorMessage}">
        <div>${createErrorMessage}</div>
    </c:if>
</form>
<ul>
    <c:forEach var="item" items="${todoItems}">
        <li>
            <form action="${pageContext.request.contextPath}/" method="POST">
                <c:choose>
                    <c:when test="${item.id == editingItemId}">
                        <c:if test="${empty editingItemErrorText}">
                            <input type="text" name="text" value="${item.text}">
                        </c:if>
                        <c:if test="${not empty editingItemErrorText}">
                            <input type="text" name="text" value="">
                        </c:if>
                        <button name="action" value="cancel" type="submit">Отменить</button>
                        <button name="action" value="save" type="submit">Сохранить</button>
                        <input type="hidden" name="id" value="${item.id}">
                        <c:if test="${not empty editingItemErrorText}">
                            <div>${editingItemErrorText}</div>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <span>${item.text}</span>
                        <button name="action" value="delete" type="submit">Удалить</button>
                        <button name="action" value="edit" type="submit">Редактировать</button>
                        <input type="hidden" name="id" value="${item.id}">
                    </c:otherwise>
                </c:choose>
            </form>
        </li>
    </c:forEach>
</ul>
</body>
</html>
