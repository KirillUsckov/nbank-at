#!/bin/bash
# Поднятие сервисов мониторинга
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

# Namespace monitoring
kubectl create namespace monitoring || true
kubectl create secret generic backend-basic-auth --from-literal=username=admin --from-literal=password=admin -n monitoring

helm upgrade --install monitoring prometheus-community/kube-prometheus-stack -f infra/k8s/monitoring/monitoring-values.yml -n monitoring

# ECK CRD + Operator
kubectl apply -f https://download.elastic.co/downloads/eck/2.11.0/crds.yaml
kubectl apply -f https://download.elastic.co/downloads/eck/2.11.0/operator.yaml
kubectl apply -f infra/k8s/monitoring/logs/filebeat-template.yaml

# Elasticsearch + Kibana
kubectl apply -f infra/k8s/monitoring/logs/es-template.yaml
kubectl apply -f infra/k8s/monitoring/logs/kibana-template.yaml

kubectl apply -f infra/k8s/monitoring/spring-bank-monitor.yaml

kubectl get pods -n monitoring
