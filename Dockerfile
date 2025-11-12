# Используем Java 11
FROM openjdk:11-jre-slim

# Указываем рабочую директорию
WORKDIR /app

# Копируем jar из target
COPY target/payment-mock-api.jar app.jar

# Открываем порт 8080
EXPOSE 8080

# Команда запуска
ENTRYPOINT ["java","-jar","/app/app.jar"]
