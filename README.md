# my-market-app

### Локальный запуск

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
docker build -t my-market-app:latest .
```

#### Запуск

Для запуска приложения необходима БД MySql.
В проекте предусмотрен файл с ключевыми переменными окружения для правильной конфигурации приложения с БД.

Параметры следующие (продублированы дефолтные значения):

* `APP_PORT=8080` (порт приложения, если менять, то при запуске из Docker необходимо скорректировать маппинг портов)
* `DB_URL=jdbc:mysql://host.docker.internal:3306/my_market_db` - url подключения к бд
* `DB_USER=yp_user` - имя пользователя БД
* `DB_PASSWORD=yp_password` - пароль пользователя БД

Специфика
устройства локальных сетей Docker на Windows и MacOS немного отличается, поэтому если БД развернута как Docker-контейнер
на локальной машине, то в url для подключения к БД можно указать как `localhost`, для MacOS необходимо указать
`host.docker.internal`, либо ip-адрес контейнера из сети Docker

```bash
docker run --rm -p 9090:8080 --env-file .env my-market-app:latest

```

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

В проекте предусматривается модульное и интеграционное тестирование. Также тестирование разбито по слоям `service` и `controller`. Поэтому для запуска того или иного
типа можно использовать следующую команды

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