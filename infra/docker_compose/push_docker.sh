#!/usr/bin/env bash

set -euo pipefail

IMAGE_NAME="${IMAGE_NAME:-nbank-at}"
DOCKERHUB_USERNAME="${DOCKERHUB_USERNAME:-kirberia}"
TAG="${1:-latest}"

if [[ -z "${DOCKERHUB_TOKEN:-}" ]]; then
  echo "Ошибка: переменная DOCKERHUB_TOKEN не задана"
  exit 1
fi

LOCAL_IMAGE="${IMAGE_NAME}:${TAG}"
REMOTE_IMAGE="${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${TAG}"

echo ">>> Логин в Docker Hub"
echo "${DOCKERHUB_TOKEN}" |
  docker login \
    --username "${DOCKERHUB_USERNAME}" \
    --password-stdin

echo ">>> Тегирование ${LOCAL_IMAGE} как ${REMOTE_IMAGE}"
docker tag "${LOCAL_IMAGE}" "${REMOTE_IMAGE}"

echo ">>> Публикация ${REMOTE_IMAGE}"
docker push "${REMOTE_IMAGE}"

echo ">>> Образ опубликован:"
echo "docker pull ${REMOTE_IMAGE}"