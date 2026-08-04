#!/bin/bash

# Поднятие сервисов приложения
# запустили локальный кластер с помощью minikube, с использлванием драйвера docker - кластер будет запущен внутри докер контейнера minikube
minikube start --driver=docker

# Создание configmap с именем selenoid-config, в котором будет доступен файл infra/config/browsers.json под именем browsers.json
kubectl create configmap selenoid-config --from-file=browsers.json=infra/config/browsers.json
kubectl create configmap postgres-init --from-file=01-init-db.sql=infra/init-scripts/01-init-db.sql

echo "app deploy"
# Устанавливаем helm chart с именем релиза nbank, беря шаблон из infra/k8s/apps/ - создает все ресурсы из шаблонов
helm upgrade --install nbank infra/k8s/apps/
# Вывод статусов всех сервисов в namespace=default
kubectl get svc

#  Вывод статусов всех подов в namespace=default
kubectl get pods