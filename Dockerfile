FROM openjdk:11

# Create dir for loging
RUN mkdir -p /var/log/myapp/logs

# Create dir /app if not exist and set as working directory
WORKDIR /app

COPY target/spring-boot-project-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java","-jar","spring-boot-project-0.0.1-SNAPSHOT.jar"]

# docker image build -t my-springboot-app:v1 -t my-springboot-app:latest .