# Используем официальный образ Java 11 от Eclipse Temurin
FROM eclipse-temurin:11-jre

# Указываем рабочую директорию
WORKDIR /app

# Копируем jar из target
COPY target/payment-mock-api.jar app.jar

# Открываем порт 8080
EXPOSE 8080

# Команда запуска
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
