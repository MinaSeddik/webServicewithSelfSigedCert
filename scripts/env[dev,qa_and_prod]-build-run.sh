#!/bin/bash

echo 'Maven profiles would provide a build-time solution, while SpringFramework profiles would provide a runtime alternative.'

#To Build with mvn profile
###########################

#Set Active profile in pom.xml
#        <profile>
#            <id>local</id>
#            <activation>
#                <activeByDefault>true</activeByDefault>
#            </activation>
#        </profile>

#From Command line to Activate certain profile
mvn package     # <--- equivalent to "mvn package -P local" (Remember: <activeByDefault>true</activeByDefault>)
mvn package -P local
mvn package -P dev
mvn package -P qa
mvn package -P prod

# Use Maven plugin "maven-help-plugin" to show profile when building the package


#To Run with spring profile
###########################

#Set Active profile in application.properties
#spring.profiles.active=@spring.profiles.active@
# then you should use
#        <resources>
#            <resource>
#                <directory>src/main/resources</directory>
#                <filtering>true</filtering>
#            </resource>
#        </resources>

#OR

# Set default profile in application.properties
spring.profiles.active=local
spring.profiles.active=dev
spring.profiles.active=qa
spring.profiles.active=prod


#From Command line
java -jar target/spring-boot-project-0.0.1-SNAPSHOT.jar
java -jar -Dspring.profiles.active=local target/spring-boot-project-0.0.1-SNAPSHOT.jar
java -jar -Dspring.profiles.active=dev target/spring-boot-project-0.0.1-SNAPSHOT.jar
java -jar -Dspring.profiles.active=qa target/spring-boot-project-0.0.1-SNAPSHOT.jar
java -jar -Dspring.profiles.active=prod target/spring-boot-project-0.0.1-SNAPSHOT.jar



# To build and Run production
#############################
mvn package -P prod

#java -jar -Dspring.profiles.active=prod target/spring-boot-project-0.0.1-SNAPSHOT.jar --spring.config.location=file:/home/mina/Desktop/application-prod.properties


#UPDATE: As the behaviour of spring.config.location now overrides the default instead of adding to it. You need to use spring.config.additional-location to keep the defaults. This is a change in behaviour from 1.x to 2.x
java -jar -Dspring.profiles.active=prod target/spring-boot-project-0.0.1-SNAPSHOT.jar --spring.config.additional-location=file:/home/mina/Desktop/application-prod.properties





