#!/bin/bash

# Настройки
IMAGE_NAME=nbank-at
DOCKERHUB_USERNAME=kirberia
TAG=latest

# Логин в Docker Hub с токеном
echo ">>> Логин в Docker Hub с токеном"
echo "${DOCKERHUB_TOKEN}" | docker login --username $DOCKERHUB_USERNAME --password-stdin

# Тегирование образа
echo ">>> Тегирование образа"
docker tag $IMAGE_NAME $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG

# Отправка образа в Docker Hub
echo ">>> Отправка образа в Docker Hub"
docker push $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG

echo ">>> Готово! Образ доступен как: docker pull $DOCKERHUB_USERNAME/$IMAGE_NAME:$TAG"
