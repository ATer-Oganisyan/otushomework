Installation manual

Run:

alias k=kubectl
helm install myzql-release myZql/mysql -f kuber/mysql/values.yml
k apply -f ./kuber/mysql/migrations/  
k apply -f ./kuber