# Debuggers team
# Interview planning application
## Run with Docker

### Docker are required to run the application.

#### - Open terminal in project directory.
#### - Run following command:

## If you want just run the application:
```shell
./mvnw clean package
docker-compose up
```
## If you make changes to the application, you must rebuild the docker images:
```shell
./mvnq clean package
docker compose build
docker-compose up
```