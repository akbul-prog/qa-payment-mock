# ---------- Stage 1: Build ----------
FROM eclipse-temurin:11-jdk as builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=builder /app/target/payment-mock-api.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
