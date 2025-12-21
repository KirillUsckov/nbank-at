#!/bin/bash

IMAGE_NAME=kirberia/nbank-at:latest
BUILD_NEEDED=false
TEST_PROFILE=${1:-ui}
TIMESTAMP=$(date +"%Y%m%d_%H%M")

TEST_RESULTS_OUTPUT_DIR="$(pwd)/test-results/$TIMESTAMP"

API_BASE_URL="http://localhost:4111"
UI_BASE_URL="http://localhost:3000"
SELENOID_URL="http://localhost:4444"
SELENOID_UI_URL="http://localhost:8080"

# Определяем сеть
NETWORK_NAME="nbank-network"
if ! docker network inspect "$NETWORK_NAME" &> /dev/null; then
    echo "⚠️  Сеть не найдена, используем host.docker.internal"
    API_BASE_URL="http://host.docker.internal:4111"
    UI_BASE_URL="http://host.docker.internal:3000"
    SELENOID_URL="http://host.docker.internal:4444"
    SELENOID_UI_URL="http://host.docker.internal:8080"
    NETWORK_ARG="--network $NETWORK_NAME"
else
    API_BASE_URL="http://backend:4111"
    UI_BASE_URL="http://nginx:80"
    SELENOID_URL="http://selenoid:4444"
    SELENOID_UI_URL="http://selenoid-ui:8080"
    NETWORK_ARG="--network $NETWORK_NAME"
fi

echo ">>> Результаты тестов в папке: $TEST_RESULTS_OUTPUT_DIR"

# Создаем папки с правильными разделителями
mkdir -p "$TEST_RESULTS_OUTPUT_DIR/logs"
mkdir -p "$TEST_RESULTS_OUTPUT_DIR/results"
mkdir -p "$TEST_RESULTS_OUTPUT_DIR/report"

if BUILD_NEEDED
then
  echo ">>> Сборка образа"
  docker build -t $IMAGE_NAME .
fi

echo ">>> Запуск тестов"

# Для Docker на Windows нужно конвертировать пути
if [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]]; then
    # Конвертируем путь для Docker Desktop
    WIN_PATH="$(cygpath -w "$TEST_RESULTS_OUTPUT_DIR" 2>/dev/null || echo "$TEST_RESULTS_OUTPUT_DIR")"

    docker run --rm \
      $NETWORK_ARG \
      -v "${WIN_PATH}/logs:/app/logs" \
      -v "${WIN_PATH}/results:/app/target/surefire-reports" \
      -v "${WIN_PATH}/report:/app/target/site" \
      -e "TEST_PROFILE=$TEST_PROFILE" \
      -e "API_BASE_URL=$API_BASE_URL" \
      -e "UI_BASE_URL=$UI_BASE_URL" \
      -e "SELENOID_URL=$SELENOID_URL" \
      -e "SELENOID_UI_URL=$SELENOID_UI_URL" \
      $IMAGE_NAME
else
    docker run --rm \
      $NETWORK_ARG \
      -v "$TEST_RESULTS_OUTPUT_DIR/logs:/app/logs" \
      -v "$TEST_RESULTS_OUTPUT_DIR/results:/app/target/surefire-reports" \
      -v "$TEST_RESULTS_OUTPUT_DIR/report:/app/target/site" \
      -e "TEST_PROFILE=$TEST_PROFILE" \
      -e "API_BASE_URL=$API_BASE_URL" \
      -e "UI_BASE_URL=$UI_BASE_URL" \
      -e "SELENOID_URL=$SELENOID_URL" \
      -e "SELENOID_UI_URL=$SELENOID_UI_URL" \
      $IMAGE_NAME
fi

echo ">>> Тесты завершены"
echo "Лог файл: $TEST_RESULTS_OUTPUT_DIR/logs/run.log"
echo "Результаты тестов: $TEST_RESULTS_OUTPUT_DIR/results"
echo "Репорт: $TEST_RESULTS_OUTPUT_DIR/report"