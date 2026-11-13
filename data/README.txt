These three files show the JSON format used by the College Event Registration System (each is an empty array).
At runtime the app does NOT read this folder by default. It creates and uses its own data folder:
    C:\Users\<your user name>\CollegeEventRegistrationSystem\data
(outside Tomcat's deployment directory, so redeploying never erases data).
To use THIS folder instead, add this JVM argument in Eclipse (see README.md, section 8):
    -Dcollegeevent.data.dir=C:/full/path/to/CollegeEventRegistrationSystem/data
