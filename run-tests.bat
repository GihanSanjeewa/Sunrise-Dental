@echo off
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
set "PATH=C:\Users\gihan\tools\apache-maven-3.9.6\bin;%JAVA_HOME%\bin;%PATH%"
mvn test
