#!/bin/bash

echo ">>> Остановить docker-compose"
docker compose -f infra/docker_compose/docker-compose.yml down -v

echo ">>> Выгрузить образы из infra/config/browsers.json"

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JSON_FILE="infra/config/browsers.json"
COMPOSE_FILE="$SCRIPT_DIR/docker-compose.yml"

# Проверки
[ -f "$JSON_FILE" ] || { echo "Ошибка: файл не найден: $JSON_FILE" >&2; exit 1; }
command -v docker &> /dev/null || { echo "Ошибка: Docker не найден" >&2; exit 1; }

echo "Получение образов из $JSON_FILE..."

# Извлекаем все image значения
IMAGES=$(grep -o '"image"[[:space:]]*:[[:space:]]*"[^"]*"' "$JSON_FILE" | \
         sed 's/.*"image"[[:space:]]*:[[:space:]]*"//' | \
         sed 's/"//g' | \
         sort -u)

[ -n "$IMAGES" ] || { echo "Ошибка: Образы не найдены в JSON" >&2; exit 1; }

echo "Найдено $(echo "$IMAGES" | wc -l) образов"
echo ""

# Загружаем
while IFS= read -r IMG; do
    echo "Pulling: $IMG"
    docker pull "$IMG" || {
        echo "Ошибка: Не удалось сделать pull $IMG" >&2
        exit 1
    }
done <<< "$IMAGES"
echo ">>> Загрузка образов завершена!"

echo ">>> Запуск docker-compose"
docker compose -f "$COMPOSE_FILE" up -d
 # Запуск контейнеров в фоновом режиме (консоль не блочится)