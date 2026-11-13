<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="view" required="true" type="com.collegeevent.model.EventView" %>
<article class="event-card">
  <div class="poster ${view.categoryClass}" aria-hidden="true">
    <span class="poster-cat"><c:out value="${view.event.category}"/></span>
    <div class="poster-date"><b><c:out value="${view.day}"/></b><span><c:out value="${view.month}"/></span></div>
  </div>
  <div class="body">
    <h3><a href="${ctx}/event?id=<c:out value='${view.event.id}'/>"><c:out value="${view.event.title}"/></a></h3>
    <p class="line"><c:out value="${view.dateDisplay}"/>, <c:out value="${view.timeDisplay}"/></p>
    <p class="line"><c:out value="${view.event.venue}"/></p>
    <div class="seats ${view.full ? 'is-full' : ''}">
      <div class="seat-bar" role="presentation"><span style="width:${view.percentFilled}%"></span></div>
      <small>
        <c:choose>
          <c:when test="${view.full}">No seats left</c:when>
          <c:otherwise>${view.remaining} of ${view.event.capacity} seats left</c:otherwise>
        </c:choose>
      </small>
    </div>
    <div class="foot">
      <c:choose>
        <c:when test="${view.userRegistered}"><span class="badge badge-violet">You are registered</span></c:when>
        <c:otherwise><span class="badge ${view.statusClass}"><c:out value="${view.statusLabel}"/></span></c:otherwise>
      </c:choose>
      <a class="btn btn-outline btn-sm" href="${ctx}/event?id=<c:out value='${view.event.id}'/>">View details</a>
    </div>
  </div>
</article>
