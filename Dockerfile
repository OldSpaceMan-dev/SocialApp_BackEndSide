# Шаг 1: Используем официальный образ Gradle для сборки приложения
FROM gradle:7.6-jdk17 AS build
WORKDIR /app

# Копируем все файлы проекта в контейнер
COPY . .

# Выполняем сборку с использованием Gradle
RUN gradle build -x test

# Шаг 2: Используем минимальный образ Java для запуска приложения
FROM eclipse-temurin:17-jre
WORKDIR /app

# Копируем собранный jar-файл из первого этапа
COPY --from=build /app/build/libs/*.jar app.jar

# Указываем команду запуска
ENTRYPOINT ["java", "-jar", "app.jar"]