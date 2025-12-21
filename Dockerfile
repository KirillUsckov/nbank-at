# Базовый image
# Чтобы не создавать каждый раз образ с нуля с зависимостями 4
# типа gradle ,git,java
FROM maven:3.9.12-eclipse-temurin-21-alpine

# Дефолтные значения аргументов, передаваемых в докер
ARG TEST_PROFILE=api
ARG API_BASE_URL=http://localhost:4111
ARG UI_BASE_URL=http://localhost:3000

# Переменные окружения внутри контейнера
ENV TEST_PROFILE=${TEST_PROFILE}
ENV API_BASE_URL=${API_BASE_URL}
ENV UI_BASE_URL=${UI_BASE_URL}


# Рабочая директория
WORKDIR /app

# Копирование файла в рабочую директорию
COPY pom.xml .

# Загрузка и кэширование зависимостей
RUN mvn dependency:go-offline

# Копирование всего проекта
COPY . .


# 2>&1 - 2>&1 — перенаправление потоков:
#2 — stderr (стандартный поток ошибок)
#> — перенаправление
#&1 — в stdout (поток №1)
#| tee /app/logs/run.log — запись в файл и консоль:
 #| — pipe, передает вывод следующей команде
 #tee — команда, которая:
 #Пишет всё в файл /app/logs/run.log
 #Одновременно выводит в консоль (stdout)
 #tee особенно полезен в Docker, чтобы видеть логи в docker logs
USER root
CMD /bin/sh -c " \
    mkdir -p /app/logs ; \
    { \
        echo '>>> Running test with profile: ${TEST_PROFILE}' ; \
        mvn test -P ${TEST_PROFILE}; \
        \
        echo '>>> Running surfire-report:report' ; \
        mvn -DskipTests=true surefire-report:report ; \
    } 2>&1 | tee /app/logs/run.log"

