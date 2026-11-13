<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<aside class="sidebar">
  <div class="who">
    <strong><c:out value="${sessionScope.userName}"/></strong>
    <span>Administrator</span>
  </div>
  <nav aria-label="Admin navigation">
    <a href="${ctx}/admin/dashboard" class="${param.active == 'dashboard' ? 'active' : ''}">Dashboard</a>
    <a href="${ctx}/admin/events" class="${param.active == 'events' ? 'active' : ''}">Manage events</a>
    <a href="${ctx}/admin/events/form" class="${param.active == 'add' ? 'active' : ''}">Add event</a>
    <a href="${ctx}/admin/registrations" class="${param.active == 'registrations' ? 'active' : ''}">Registrations</a>
    <a href="${ctx}/events">View public site</a>
  </nav>
</aside>
