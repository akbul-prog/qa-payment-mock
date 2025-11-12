# ---------- Stage 1: Build ----------
FROM maven:3.9.2-eclipse-temurin-11 AS builder

WORKDIR /app

# Копируем pom и исходники отдельно для кеширования Docker слоёв
COPY pom.xml .
COPY src ./src

# Собираем jar (без тестов)
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:11-jre

WORKDIR /app

# Копируем готовый jar из стадии сборки
COPY --from=builder /app/target/qa-payment-mock-*.jar app.jar

# Открываем порт
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java","-jar","app.jar"]
