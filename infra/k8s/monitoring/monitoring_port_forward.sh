kubectl port-forward svc/monitoring-kube-prometheus-prometheus -n monitoring 3001:9090 2>&1 &
kubectl port-forward svc/monitoring-grafana -n monitoring 3002:80 2>&1 &

kubectl port-forward svc/kb-kb-http -n monitoring 3003:5601  2>&1 &
