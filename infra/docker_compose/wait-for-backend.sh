#!/usr/bin/env bash
set -o pipefail

CONTAINER_NAME="backend"
TIMEOUT_SECONDS=180
EXPECTED_LOG="Started BankApplication"

echo "Ожидаем запуска контейнера ${CONTAINER_NAME}..."

if timeout "${TIMEOUT_SECONDS}" \
  docker logs --follow --since=0s "${CONTAINER_NAME}" 2>&1 |
  grep --max-count=1 --fixed-strings "${EXPECTED_LOG}"
then
  echo "Backend успешно запущен"
else
  exit_code=$?

  echo "Backend не запустился за ${TIMEOUT_SECONDS} секунд"
  echo "Последние логи:"
  docker logs --tail=200 "${CONTAINER_NAME}" 2>&1 || true

  exit "${exit_code}"
fi