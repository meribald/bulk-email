FROM openjdk:8-jre-alpine
# copy application JAR (with libraries inside)
COPY target/bulk-email-*.jar /app.jar
# specify default command
CMD ["/usr/bin/java", "-jar", "-Dspring.profiles.active=test", "/app.jar"]