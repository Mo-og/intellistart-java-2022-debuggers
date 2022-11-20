# Interview planning application
## Run with Docker
To run application Docker is required.
### Build image:
- Open terminal in project directory.
- Run following command:
```sh
docker build -t ipapp .
```
### Run container:
```sh
docker run -p 8080:8080 ipapp
```
