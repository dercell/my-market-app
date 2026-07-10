# my-market-app

## Локальный запуск

В проекте предусмотрен maven wrapper. Поэтому для сборки исходников в jar необходимо выполнить следующее

```bash
#Linux/MacOS
./mvnw clean package

#Windows
mvnw.cmd clean package
```

Далее в корне проекта появится каталог target, внутри него jar файл приложения. Для запуска приложения необходимо
выполнить команду

```bash
java -jar target/my-market-app-0.0.1-SNAPSHOT.jar
```

### Docker

#### Сборка образа

В проекте предусмотрена сборка образа Docker. Для этого необходимо выполнить следующую команду в директории с Dockerfile

```bash
# сервис оплаты
docker build -f ./payment-service/Dockerfile -t payment-service:latest .

# основное приложение
docker build -f ./market-app/Dockerfile -t market-app:latest .
```

### Запуск

#### Независимый запус

Для запуска приложения необходимы следующие компонеты

* MySql
* Redis

В проекте витрины магазина предусмотрен файл с ключевыми переменными окружения для правильной конфигурации приложения с
БД.
Параметры следующие (продублированы дефолтные значения):

* `APP_PORT=8080` (порт приложения, если менять, то при запуске из Docker необходимо скорректировать маппинг портов)
* `DB_URL=jdbc:mysql://host.docker.internal:3306/my_market_db` - url подключения к бд
* `DB_USER=yp_user` - имя пользователя БД
* `DB_PASSWORD=yp_password` - пароль пользователя БД
* `REDIS_HOST=host.docker.internal` - хост для подключения к Redis
* `REDIS_PORT=6379` - порт для подключения к Redis
* `PAYMENT_SERVICE_URL=http://host.docker.internal:8090` - URL для взаимодействия с сервисом оплаты
* `KEYCLOAK_ISSUER_URI=http://host.docker.internal:8080/realms/market-app` - корневой адрес реалма в Keycloak
* `KEYCLOAK_CLIENT_SECRET=G8eG1ctvsrarlmgmmOtuaVlo85ey0T7k` - secret клиента приложения

* Для сервиса оплаты также предусмотрен файл со свойствами

* `SERVICE_PORT=8090` - порт сервиса
* `BALANCE=50000` - баланс на счете
* `KEYCLOAK_ISSUER_URI=http://host.docker.internal:8080/realms/market-app` - корневой адрес реалма в Keycloak

#### Полное развертывание

Для запуска приложения со всеми компонентами сразу в проекте находится файл docker-compose.yaml. Ниже команда для
полного развертывания

```bash
docker-compose -f docker-compose.yaml up -d 
```

Специфика устройства локальных сетей Docker на Windows и MacOS немного отличается, поэтому если БД развернута как
Docker-контейнер на локальной машине, то в url для подключения к БД можно указать как `localhost`, для MacOS необходимо
указать
`host.docker.internal`, либо ip-адрес контейнера из сети Docker

```bash
# запуск сервиса оплаты
docker run --rm -p 8090:8090 --env-file ./payment-service/.env payment-service:latest

# запуск основного приложения
docker run --rm -p 8080:8080 --env-file ./market-app/.localEnv market-app:latest
```

#### Keycloak

В рамках полного развертываения в репозитории проекта находятся экспортиованные файлы realm и пользователей.
Существующие пользователи представлены ниже.

| Login | Password | 
|:------|:---------| 
| luke  | luke     | 
| han   | han      | 

### Тестирование

В данном проекте тестирование происходит с помощью БД, разворачиваемой в Docker контейнере, поэтому для проверки тестов
необходимо наличие установленного на компьютере Docker.
Запуск всех тестов

```bash
#Linux/MacOS
./mvnw clean test

#Windows
mvnw.cmd clean test
```

В проекте предусматривается модульное и интеграционное тестирование. Для запуска интеграционных тестов необходим
собранный образ сервиса оплаты. Для сборки нужно из корневого каталога проекта выполнить команду сборки

```bash
docker build -f ./payment-service/Dockerfile -t payment-service:latest .
```

Также тестирование разбито по слоям `service` и
`controller`. Поэтому для запуска того или иного
типа можно использовать следующик команды

```bash
#Linux/MacOS

./mvnw clean test -Dgroups=unit
./mvnw clean test -Dgroups=integration 
./mvnw clean test -Dgroups=controller
./mvnw clean test -Dgroups=service

#Windows
mvnw.cmd clean test -Dgroups=unit
mvnw.cmd clean test -Dgroups=integration 
mvnw.cmd clean test -Dgroups=controller
mvnw.cmd clean test -Dgroups=service
```

#### Примечания

Помимо основных тестов в прилоежнии Payment Service существует SwaggerUI, позволяющий взаимодействите с сервисом
платежей без использования витрины магазина. Данная спецификация сгенерирована на основе OpenAPI схемы
в [api-spec](api-spec).

Сам интерфейс доступен по адресу

`http://localhost:8090/swagger-ui/index.html`