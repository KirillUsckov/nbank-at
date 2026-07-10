#!/usr/bin/env bash

set -Eeuo pipefail

CONTAINER_NAME="backend"
EXPECTED_LOG="Started BankApplication"
TIMEOUT_SECONDS=180
CHECK_INTERVAL=2

echo "Ожидаем запуска контейнера ${CONTAINER_NAME}..."

deadline=$((SECONDS + TIMEOUT_SECONDS))

while (( SECONDS < deadline )); do
  container_status="$(
    docker inspect \
      --format='{{.State.Status}}' \
      "$CONTAINER_NAME" 2>/dev/null || true
  )"

  if [[ "$container_status" == "exited" || "$container_status" == "dead" ]]; then
    echo "Контейнер ${CONTAINER_NAME} завершился со статусом ${container_status}"
    echo "Последние логи:"
    docker logs --tail=200 "$CONTAINER_NAME" 2>&1 || true
    exit 1
  fi

  if docker logs "$CONTAINER_NAME" 2>&1 |
    grep --quiet --fixed-strings "$EXPECTED_LOG"; then
    echo "Backend успешно запущен"
    exit 0
  fi

  sleep "$CHECK_INTERVAL"
done

echo "Backend не запустился за ${TIMEOUT_SECONDS} секунд"
echo "Последние логи:"
docker logs --tail=200 "$CONTAINER_NAME" 2>&1 || true

exit 1