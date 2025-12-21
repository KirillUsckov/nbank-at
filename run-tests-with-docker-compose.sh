#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

INFRA_DIR="$SCRIPT_DIR/infra/docker_compose"
RUN_DOCKER_FILE="$INFRA_DIR/restart_docker.sh"
RUN_TESTS_FILE="$SCRIPT_DIR/run-tests.sh"

echo ">>> Поднимается тестовое окружение"
bash "$RUN_DOCKER_FILE"

# Даем время на запуск
echo "Ожидание запуска сервисов..."
sleep 3

# Проверяем, что всё запустилось
echo "Проверка состояния сервисов..."
docker compose ps

echo "========================================"
echo "  ЗАПУСК ТЕСТОВ"
echo "========================================"
echo ""

#docker run --rm \
#  -e API_BASE_URL=http://localhost:4111 \
#  -e UI_BASE_URL=http://localhost:3000 \
#  -e SELENOID_URL=http://localhost:4444 \
#  -e SELENOID_UI_URL=http://localhost:8080 \
#  kirberia/nbank-at:latest

# Если хотите использовать ваш существующий скрипт
bash "$RUN_TESTS_FILE" "${1:-all}"

echo ">>> Останавливается тестовое окружение"
docker compose down


echo ""
echo "========================================"
echo "✅ ВСЕ ТЕСТЫ ЗАВЕРШЕНЫ!"
echo "========================================"
echo ""
echo "Результаты сохранены в: $(pwd)/test-results/"