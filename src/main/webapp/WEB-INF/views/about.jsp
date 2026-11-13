<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp">
  <jsp:param name="title" value="About and contact"/>
  <jsp:param name="active" value="about"/>
</jsp:include>
<main id="main">
  <div class="page-head">
    <div class="container">
      <h1>About the College Event Registration System</h1>
      <p>The college's single place for event registration.</p>
    </div>
  </div>
  <section class="section">
    <div class="container two-col">
      <div>
        <h2>What it does</h2>
        <p>The College Event Registration System replaces paper sign-up sheets and scattered forms. Clubs and departments publish their events here, students register in a few clicks, and organizers see exactly who is coming.</p>
        <p>Seats are counted live, so an event closes the moment it is full or when its registration deadline passes. Nobody can book the same event twice.</p>
        <h2 style="margin-top:2rem">Questions students ask</h2>
        <div class="faq">
          <details><summary>Can I cancel a registration?</summary><p>Yes, from My registrations, until the registration deadline of the event. Your seat goes back to the pool straight away.</p></details>
          <details><summary>What if an event is full?</summary><p>The register button is disabled. If someone cancels, a seat opens up and you can register then.</p></details>
          <details><summary>How is attendance recorded?</summary><p>Organizers mark attendance on the day. You will see "Attended" next to the event in My registrations.</p></details>
          <details><summary>I forgot my password.</summary><p>Contact the events office using the details on this page and an administrator will help you recover access.</p></details>
        </div>
      </div>
      <div>
        <h2>Contact the events office</h2>
        <div class="panel">
          <p><strong>E-mail</strong><br><a href="mailto:events@college.edu">events@college.edu</a></p>
          <p><strong>Office</strong><br>Student Activities Centre, Ground floor</p>
          <p><strong>Hours</strong><br>Monday to Friday, 9:30 am to 4:30 pm</p>
          <a class="btn btn-primary" href="mailto:events@college.edu?subject=Event%20Registration%20question">Send an e-mail</a>
        </div>
        <p class="muted small">These contact details are sample text. Replace them in <code>about.jsp</code> with your college's own.</p>
      </div>
    </div>
  </section>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
