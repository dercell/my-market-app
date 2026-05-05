# my-market-app

Запуск бд
```bash
docker run --name yp-mysql -e MYSQL_USER=yp_user -e MYSQL_PASSWORD=yp_password -e MYSQL_DATABASE=my_market_db -e MYSQL_RANDOM_ROOT_PASSWORD=yes -p 3306:3306 -d mysql:oraclelinux9
```