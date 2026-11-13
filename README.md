# College Event Registration System

Java Servlets + JSP + plain CSS/JavaScript, Maven WAR, Apache Tomcat 9, Gson, local JSON files. No database.

## 1. What you get
- **Students**: sign up / log in, browse and search events, filter by category, see seats left, register, cancel (until the registration deadline), view registrations, edit profile, change password, log out.
- **Admins** (separate login at `/admin/login`): dashboard (students, events, registrations), add/edit/delete events, view registrations per event, search, mark attendance, export CSV, see full events and seats left.
- Duplicate registrations, overbooking, late registration and registration for ended events are blocked (registration code is synchronized).
- Security: PBKDF2-HMAC-SHA256 password hashes, HttpSession login, new session on login, CSRF tokens on every POST form, role filters on `/student/*` and `/admin/*`, output escaping, login lock-out after 5 failures, CSV formula-injection protection. The registration form can never create an admin.

## 2. Requirements (Windows)
| Tool | Version |
|---|---|
| JDK | 8 or newer (11/17 work too) |
| Eclipse | IDE for Enterprise Java and Web Developers (any recent version) |
| Apache Tomcat | **9.0.x** (not 10 - this project uses `javax.servlet`) |
| Maven | built into Eclipse (m2e) - nothing to install |
An internet connection is needed once, so Maven can download Gson, JSTL and the servlet APIs.

## 3. Default admin account
| E-mail | Password |
|---|---|
| `admin@college.edu` | `Admin@123` |
The admin e-mail is **constant**: it is created (or its password repaired) on *every* start, and the admin login page shows it read-only. To use different details, add `-Dcollegeevent.admin.email=you@college.edu -Dcollegeevent.admin.password=YourStrongPass1` to the server VM arguments (section 8) and restart.

**Students** are signed in automatically right after registering, the login page remembers the last e-mail used, and a login lasts 8 hours of inactivity. Registered students are kept in `users.json` (section 5), so they are not asked to register again after a restart.

## 4. Eclipse setup, step by step
1. **Download Tomcat 9**: go to https://tomcat.apache.org/download-90.cgi and download the *64-bit Windows zip*. Extract it to e.g. `C:\apache-tomcat-9.0.xx`.
2. **Open Eclipse** and choose a workspace folder (e.g. `C:\eclipse-workspace`).
3. **Check the JDK**: *Window > Preferences > Java > Installed JREs*. If no JDK (not a plain JRE) is listed, click *Add > Standard VM*, point to your JDK folder (e.g. `C:\Program Files\Java\jdk-17`) and tick it as default.
4. **Extract** `CollegeEventRegistrationSystem.zip` somewhere (e.g. `C:\Projects\`). You should have `C:\Projects\CollegeEventRegistrationSystem\pom.xml`. The folder, the Maven artifact and the Eclipse project are all called `CollegeEventRegistrationSystem` (no spaces, because spaces in project names can break Tomcat context paths). The visible site name "College Event Registration System" is set in the pom's `<name>` and in the JSP pages.
5. **Import**: *File > Import > Maven > Existing Maven Projects > Next*. Browse to the extracted `CollegeEventRegistrationSystem` folder, make sure `pom.xml` is ticked, click *Finish*.
6. **Download dependencies**: wait for the progress bar (bottom right) to finish. If you see red marks: right-click the project > *Maven > Update Project...* > tick *Force Update of Snapshots/Releases* > OK.
7. **Add Tomcat**: *Window > Preferences > Server > Runtime Environments > Add > Apache Tomcat v9.0 > Next*. Set *Tomcat installation directory* to the folder from step 1, pick your JDK under *JRE*, *Finish*, *Apply and Close*.
8. **Set the project facets** (only if Eclipse complains): right-click project > *Properties > Project Facets*: Java 1.8 (or higher), Dynamic Web Module 4.0 (or 3.1+), JavaScript optional. *Apply*.
9. **Check the context root** (before running): right-click the project > *Properties > Web Project Settings*: *Context root* must be `CollegeEventRegistrationSystem`. Fix it there if not.
10. **Run**: right-click the project > *Run As > Run on Server*. Choose *Tomcat v9.0 Server*, click *Next*, make sure **CollegeEventRegistrationSystem** is in the *Configured* list, *Finish*.
11. **Open the app**: if the browser tab doesn't open, go to **http://localhost:8080/CollegeEventRegistrationSystem/**. (If Eclipse set a different context path, check *Servers* view > double-click server > *Modules*; set Path to `/CollegeEventRegistrationSystem`.)
12. **Student account**: click *Register*, fill in the form, then *Login*. Browse *Events*, open one and press *Register for this event*. See *Dashboard > My registrations*.
13. **Admin**: open http://localhost:8080/CollegeEventRegistrationSystem/admin/login (or the *Admin login* link in the footer) and sign in with the account in section 3.
14. **Verify persistence**: in the *Servers* tab, stop the server (red square), start it again, log in with the same account - your registrations are still there. You can also open `C:\Users\<you>\CollegeEventRegistrationSystem\data\*.json` in Notepad.

## 5. Where data is stored
Default folder: `C:\Users\<your user name>\CollegeEventRegistrationSystem\data` containing `users.json`, `events.json`, `registrations.json`. Files are created automatically; six sample events (dated relative to the first start) and the admin account are added on first run only. Edits are written atomically (temp file, then rename). A malformed file is renamed to `*.corrupt-<time>` and the app starts with an empty list instead of crashing. The path is printed in the Console at start-up.

## 6. Project layout
```
CollegeEventRegistrationSystem/
  pom.xml
  data/                       sample empty JSON files (format reference)
  src/main/java/com/collegeevent/
    model/   User, Event, Registration, EventView, RegistrationView
    dao/     DataStore, JsonRepository, UserDao, EventDao, RegistrationDao, ViewBuilder, DataSeeder
    servlet/ one servlet per page/action (login, register, events, admin ...)
    filter/  EncodingFilter, CsrfFilter, StudentAuthFilter, AdminAuthFilter
    listener/AppInitListener  (opens JSON files at start-up)
    util/    PasswordUtil, Validator, CsrfUtil, Flash, DateUtil, CsvUtil, AppConfig ...
  src/main/webapp/
    index.jsp                  forwards to /home
    assets/css/style.css, assets/js/app.js, validation.js
    WEB-INF/web.xml, WEB-INF/tags/eventCard.tag
    WEB-INF/views/             all JSP pages (reachable only through servlets)
```

## 7. URLs
Public: `/home`, `/events`, `/event?id=`, `/about`, `/register`, `/login`, `/admin/login`.
Student: `/student/dashboard`, `/student/registrations`, `/student/profile`.
Admin: `/admin/dashboard`, `/admin/events`, `/admin/events/form`, `/admin/registrations`, `/admin/registrations/export`.

## 8. Changing the data folder or admin account in Eclipse
Double-click the server in the *Servers* view > *Open launch configuration* > *Arguments* tab > append to *VM arguments* (use forward slashes):
```
-Dcollegeevent.data.dir=C:/CollegeEventRegistrationSystemData
```
Order of lookup: JVM property, environment variable `COLLEGE_EVENT_DATA_DIR`, `context-param` in `web.xml` (commented example), default folder.

## 9. Build a WAR for a standalone Tomcat
`mvn clean package` (or *Run As > Maven build...* with goals `clean package`) creates `target/CollegeEventRegistrationSystem.war`; copy it to Tomcat's `webapps` folder.

## 10. Troubleshooting
- **HTTP 404 on first open**: use the URL ending in `/CollegeEventRegistrationSystem/`; check the project is listed under the server's *Modules*.
- **`ClassNotFound` / `jakarta` errors**: you are using Tomcat 10+. Use Tomcat 9.
- **Red errors on `javax.servlet`**: Maven dependencies not downloaded - *Maven > Update Project*.
- **App fails to start with "data folder" message**: choose a writable folder (section 8).
- **Locked out of login**: 5 wrong passwords lock that e-mail for 5 minutes (restart Tomcat to clear).
- **Event times** use the server computer's clock and time zone.
- Sample contact details in `about.jsp` are placeholders - edit them.
- Fonts load from Google Fonts; offline, the site falls back to system fonts.
