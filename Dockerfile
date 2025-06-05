# Stage 1: Build aplication using Gradle
# Sử dụng một image chứa JDK và Gradle để build project
FROM gradle:8.5.0-jdk17 AS builder
# Tạo thư mục làm việc
WORKDIR /app
# Copy toàn bộ source code của project vào thư mục /app trong image builder
COPY . .
# Chạy lệnh Gradle để build project và tạo file ROOT.war
# Cấp quyền thực thi cho gradlew nếu cần (thường không cần nếu đã commit đúng)
RUN chmod +x ./gradlew && ./gradlew clean build war

# Stage 2: Create the final Tomcat image
# Sử dụng base image Tomcat 9 với JDK 17
FROM tomcat:9.0-jdk17-temurin
# Đặt các biến môi trường cần thiết
ENV JAVA_OPTS="-Djava.awt.headless=true -Dfile.encoding=UTF-8"
# Xóa các webapps mặc định của Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*
# Copy file ROOT.war từ image builder (stage 1) vào thư mục webapps của Tomcat
# Đường dẫn đến file WAR trong image builder sẽ là /app/build/libs/ROOT.war
COPY --from=builder /app/build/libs/ROOT.war /usr/local/tomcat/webapps/ROOT.war
# Expose port mà Tomcat lắng nghe
EXPOSE 8080
# Lệnh để khởi chạy Tomcat khi container bắt đầu
CMD ["catalina.sh", "run"]