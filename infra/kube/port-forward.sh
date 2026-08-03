kubectl port-forward svc/frontend 3000:80 2>&1 &
kubectl port-forward svc/backend 4111:4111 2>&1 &
kubectl port-forward svc/selenoid 4444:4444 2>&1 &
kubectl port-forward svc/selenoid-ui 8080:2508 2>&1 &