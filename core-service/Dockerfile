# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml và tải dependencies trước (tăng tốc build cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ source code và build
COPY src ./src
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# Stage 2: Run
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy file jar từ stage build
COPY --from=builder /app/target/*.jar app.jar

# Mở cổng 8080 (WebSocket sẽ dùng chung port này)
EXPOSE 8080

# Biến môi trường cho Render
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Lệnh chạy
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
