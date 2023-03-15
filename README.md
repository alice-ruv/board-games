Board Games Server - Battleship & Connect 4
===========================================
## System options for logged in user display
![image](https://user-images.githubusercontent.com/124344785/225001584-4d178307-0983-479a-800f-5b5d397b5adf.png)

## Battleship game display <br />
![image](https://user-images.githubusercontent.com/124344785/224998117-66f9753f-0967-41c8-9bda-0535541fd330.png)

## Connect 4 game display <br />
![image](https://user-images.githubusercontent.com/124344785/224998858-836b9407-976e-4fe2-a23a-cdb9dafd5e1e.png)

## Game results tracking display </br>
![image](https://user-images.githubusercontent.com/124344785/225000456-6416d246-9094-4fdc-8c0f-3aaa32235776.png)

******************************************************************************************************************************************

Installation Requirements:
--------------------------
1) JDK 1.8 (Version 8.0.202 is used for this application) <br />https://www.oracle.com/il-en/java/technologies/javase/javase8-archive-downloads.html

2) Glassfish 5.0.0 <br />http://download.oracle.com/glassfish/5.0.1/release/java_ee_sdk-8u1.zip	

3) PostgreSQL 14 <br />https://www.enterprisedb.com/downloads/postgres-postgresql-downloads

4) JDBC Driver (Postgresql JDBC 42.4.0) <br />https://jdbc.postgresql.org/download/

5)  InteliJ ultimate <br />https://www.jetbrains.com/idea/download/#section=windows

6) Launch4j - used for creating exe file to run the client, not from IntelliJ <br />https://sourceforge.net/projects/launch4j/files/launch4j-3/3.14/

►This application was tested in 1920×1080 display resolution in windows. 



Installing Instructions:
------------------------
1) Install Postgres
	- set password to “password”
	- keep port to default (5432)

2) Unpack Glassfish to c drive

3) Copy JDBC driver jar file to c:\glassfish5\glassfish\domains\domain1\lib\

4) Overwrite domains.xml to c:\glassfish5\glassfish\domains\domain1\config\
   The file exists in final_project\domain.xml

5) Import project in InteliJ and build it. </br >
   If you have an error "Application Server 'GlassFish 5.0.1' is not configured" in run/debug configurations: 
   press configure and then add your GlassFish Home directory.


6) Deploy server:
	- Deploy server from InteliJ by running “ServerApp” run configuration
	- Deploy server from cmd (only after it was deployed at least once by IntelliJ): 
		- Run “asadmin start-domain” in cmd from C:\glassfish5\glassfish\bin
		- Go to localhost:4848 in web browser, select Applications and then on BoardGamesServer-v1 row select Launch.

7) Deploy client:
	- Deploy client from InteliJ by running “ClientApp” run configuration
	- Deploy client from cmd: 
		- Build artifact clientJavaFXApp
		- You can use Launch4j to create exe file to run the client:
			* Define Output file and Jar 
			* Define Min and Max JRE version 
			* Select the setting icon for creating the exe 
