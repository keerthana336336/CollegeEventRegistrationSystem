<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${param.title}"/> - College Event Registration System</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Figtree:wght@400;500;600;700&family=Sora:wght@500;600;700&display=swap">
  <link rel="stylesheet" href="${ctx}/assets/css/style.css">
  <script src="${ctx}/assets/js/app.js" defer></script>
  <script src="${ctx}/assets/js/validation.js" defer></script>
</head>
<body>
<a class="skip-link" href="#main">Skip to content</a>
<header class="site-header">
  <nav class="nav container" aria-label="Main">
    <a class="brand" href="${ctx}/home"><span class="brand-mark" aria-hidden="true">CE</span><span class="brand-text">College Event Registration System</span></a>
    <button class="nav-toggle" type="button" aria-expanded="false" aria-controls="nav-menu">Menu</button>
    <div class="nav-menu" id="nav-menu">
      <ul class="nav-links">
        <li><a href="${ctx}/home" class="${param.active == 'home' ? 'active' : ''}">Home</a></li>
        <li><a href="${ctx}/events" class="${param.active == 'events' ? 'active' : ''}">Events</a></li>
        <li><a href="${ctx}/about" class="${param.active == 'about' ? 'active' : ''}">About</a></li>
      </ul>
      <div class="nav-actions">
        <c:choose>
          <c:when test="${not empty sessionScope.userId}">
            <a class="btn btn-ghost btn-sm" href="${ctx}${sessionScope.userRole == 'ADMIN' ? '/admin/dashboard' : '/student/dashboard'}">Dashboard</a>
            <form class="inline-form" method="post" action="${ctx}/logout">
              <input type="hidden" name="_csrf" value="${csrfToken}">
              <button class="btn btn-outline btn-sm" type="submit">Log out</button>
            </form>
          </c:when>
          <c:otherwise>
            <a class="btn btn-ghost btn-sm" href="${ctx}/login">Login</a>
            <a class="btn btn-violet btn-sm" href="${ctx}/register">Register</a>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </nav>
</header>
<jsp:include page="/WEB-INF/views/partials/flash.jsp"/>
