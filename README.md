Board Game Server
=================

Installation Requirements:
--------------------------
1) JDK 1.8 (Version 8.0.202 is used for this application)

   https://www.oracle.com/il-en/java/technologies/javase/javase8-archive-downloads.html

2) Glassfish 5.0.0

   http://download.oracle.com/glassfish/5.0.1/release/java_ee_sdk-8u1.zip	

3) PostgreSQL 14

   https://www.enterprisedb.com/downloads/postgres-postgresql-downloads

4) JDBC Driver (Postgresql JDBC 42.4.0 )

   https://jdbc.postgresql.org/download/

5)  InteliJ ultimate 

     https://www.jetbrains.com/idea/download/#section=windows

6) Launch4j - used for creating exe file to run the client, not from IntelliJ

   https://sourceforge.net/projects/launch4j/files/launch4j-3/3.14/

►This application was tested in 1920×1080 display resolution in windows. 



Installing Instructions:
------------------------
1) Install Postgres
	• set password to “password”
	• keep port to default (5432)

2) Unpack Glassfish to c drive

3) Copy JDBC driver jar file to c:\glassfish5\glassfish\domains\domain1\lib\

4) Overwrite domains.xml to c:\glassfish5\glassfish\domains\domain1\config\
   The file exists in final_project\domain.xml

5) Import project in InteliJ and build it.
   If you have an error "Application Server 'GlassFish 5.0.1' is not configured" in run/debug configurations: 
   press configure and then add your GlassFish Home directory.


6) Deploy server:
	• Deploy server from InteliJ by running “ServerApp” run configuration
	• Deploy server from cmd (only after it was deployed at least once by IntelliJ): 
		o Run “asadmin start-domain” in cmd from C:\glassfish5\glassfish\bin
		o Go to localhost:4848 in web browser, select Applications and then on BoardGamesServer-v1 row select Launch.

7) Deploy client:
	• Deploy client from InteliJ by running “ClientApp” run configuration
	• Deploy client from cmd: 
		o Build artifact clientJavaFXApp
		o You can use Launch4j to create exe file to run the client:
			1. Define Output file and Jar 
			2. Define Min and Max JRE version 
			3. Select the setting icon for creating the exe 
