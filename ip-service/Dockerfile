#FROM fabric8/s2i-java
#EXPOSE 8080 8778 9779
#COPY maven /app/

FROM fabric8/java-jboss-openjdk8-jdk:1.3.1
ENV JAVA_APP_DIR=/deployments
EXPOSE 8080 8778 9779
COPY target/ip-service.jar /deployments/