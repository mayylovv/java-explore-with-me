FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar ewm-main-service.jar
ENTRYPOINT ["java","-jar","/ewm-main-service.jar"]