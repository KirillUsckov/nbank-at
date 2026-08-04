#!/bin/bash
# Поднятие сервисов мониторинга
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add elastic https://helm.elastic.co
helm repo update

helm upgrade --install monitoring prometheus-community/kube-prometheus-stack -f infra/k8s/monitoring/monitoring-values.yml -n monitoring --create-namespace

kubectl create secret generic backend-basic-auth --from-literal=username=admin --from-literal=password=admin -n monitoring
# Применяем yaml с настройкой SpringMonitoring за бекендом
kubectl apply -f infra/k8s/monitoring/spring-bank-monitor.yaml