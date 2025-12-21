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