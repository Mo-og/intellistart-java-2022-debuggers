# Debuggers team

# Interview planning application

## Run with Docker

#### - Include `docker.env` file with environment variables for `application.yml`

Environment variables include:

- `DB_URL` (Database connection url, e.g. `jdbc:postgresql://hostName:5432/databaseName`)
- `DB_USERNAME` (Username of user that can access provided database)
- `DB_PASSWORD`
- `FACEBOOK_CLIENT_ID`
- `FACEBOOK_CLIENT_SECRET`
- `FACEBOOK_APP_TOKEN` (not obligatory, app will request new if not set)
- `COORDINATOR_EMAIL` (base admin user)
- `INTERVIEWER_EMAIL` (base interviewer user)
- `JWT_SECRET`

#### - Open terminal in project directory.

#### - Run following command:

## If you want just run the application:

```shell
./mvnw clean package
docker-compose up
```

## If you make changes to the application, you must rebuild the docker images:
```shell
./mvnw clean package
docker compose build
docker-compose up
```