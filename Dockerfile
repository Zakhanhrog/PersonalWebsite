FROM tomcat:9.0-jdk17-temurin

ENV JAVA_OPTS="-Djava.awt.headless=true -Dfile.encoding=UTF-8"

RUN rm -rf /usr/local/tomcat/webapps/*

COPY build/libs/ROOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]