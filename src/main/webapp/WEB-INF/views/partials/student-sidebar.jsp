<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<aside class="sidebar">
  <div class="who">
    <strong><c:out value="${sessionScope.userName}"/></strong>
    <span>Student</span>
  </div>
  <nav aria-label="Student navigation">
    <a href="${ctx}/student/dashboard" class="${param.active == 'dashboard' ? 'active' : ''}">Dashboard</a>
    <a href="${ctx}/student/registrations" class="${param.active == 'registrations' ? 'active' : ''}">My registrations</a>
    <a href="${ctx}/events">Browse events</a>
    <a href="${ctx}/student/profile" class="${param.active == 'profile' ? 'active' : ''}">My profile</a>
  </nav>
</aside>
