#!/bin/bash

IMAGE_NAME=nbank-at
TEST_PROFILE=${1:-ui}
TIMESTAMP=$(date +"%Y%m%d_%H%M")


# Для Windows: используем правильные пути для bash
#if [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]]; then
    # Git Bash на Windows
 #   TEST_RESULTS_OUTPUT_DIR="$(pwd)/test-results/$TIMESTAMP"
#else
    # Linux/Mac
 #   TEST_RESULTS_OUTPUT_DIR="$(pwd)/test-results/$TIMESTAMP"
#fi
TEST_RESULTS_OUTPUT_DIR="$(pwd)/test-results/$TIMESTAMP"


echo ">>> Результаты тестов в папке: $TEST_RESULTS_OUTPUT_DIR"

# Создаем папки с правильными разделителями
mkdir -p "$TEST_RESULTS_OUTPUT_DIR/logs"
mkdir -p "$TEST_RESULTS_OUTPUT_DIR/results"
mkdir -p "$TEST_RESULTS_OUTPUT_DIR/report"

echo ">>> Сборка образа"
docker build -t $IMAGE_NAME .

echo ">>> Запуск тестов"

# Для Docker на Windows нужно конвертировать пути
if [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]]; then
    # Конвертируем путь для Docker Desktop
    WIN_PATH="$(cygpath -w "$TEST_RESULTS_OUTPUT_DIR" 2>/dev/null || echo "$TEST_RESULTS_OUTPUT_DIR")"

    docker run --rm \
      -v "${WIN_PATH}/logs:/app/logs" \
      -v "${WIN_PATH}/results:/app/target/surefire-reports" \
      -v "${WIN_PATH}/report:/app/target/site" \
      -e "TEST_PROFILE=$TEST_PROFILE" \
      -e "API_BASE_URL=http://192.168.2.110:4111" \
      -e "UI_BASE_URL=http://192.168.2.110:3000" \
      $IMAGE_NAME
else
    docker run --rm \
      -v "$TEST_RESULTS_OUTPUT_DIR/logs:/app/logs" \
      -v "$TEST_RESULTS_OUTPUT_DIR/results:/app/target/surefire-reports" \
      -v "$TEST_RESULTS_OUTPUT_DIR/report:/app/target/site" \
      -e "TEST_PROFILE=$TEST_PROFILE" \
      -e "API_BASE_URL=http://192.168.2.110:4111" \
      -e "UI_BASE_URL=http://192.168.2.110:3000" \
      $IMAGE_NAME
fi

echo ">>> Тесты завершены"
echo "Лог файл: $TEST_RESULTS_OUTPUT_DIR/logs/run.log"
echo "Результаты тестов: $TEST_RESULTS_OUTPUT_DIR/results"
echo "Репорт: $TEST_RESULTS_OUTPUT_DIR/report"