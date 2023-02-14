2023-02-10 23:31:50     - MYSQL_ROOT_PASSWORD
2023-02-10 23:31:50     - MYSQL_ALLOW_EMPTY_PASSWORD
2023-02-10 23:31:50     - MYSQL_RANDOM_ROOT_PASSWORD

CREATE USER 'test'@'localhost' IDENTIFIED WITH mysql_native_password BY 'test';

## docker run --name=arsen-mysql -p3306:3306 -v mysql-volume:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=test -d mysql/mysql-server:8.0.20

## docker build -t arsenteroganisyan/otus-crud-server /Users/arsen/otushttpserver/crud --no-cache --platform linux/amd64

## docker run --env HOST="host.docker.internal" --env PORT=3306 --env DB=crud --env USER=root --env PASSWRORD=test -it -d -p 127.0.0.1:8001:8000 arsenteroganisyan/otus-crud-server --network="host"
