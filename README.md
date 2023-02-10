## outfit7

Test application expose two REST APIs, one for checking the state of the services by the users and one for administrators.

### local run instruction 

launch local db:
docker run -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=123 -d postgres

run application:
./gradlew bootRun

### Request examples

Admit api: \
curl http://localhost:8080/api/users \
curl http://localhost:8080/api/users/123 \
curl -X DELETE http://localhost:8080/api/users/123 -v

Services api: \
curl -X POST http://localhost:8080/services/status  -H "Content-Type: application/json" -d'{"userId": "123","timezone": "GMT","cc": "US"}'

Possible response: \
{"multiplayer":"disabled","user-support":"enabled","ads":"undefined"}

Comments:
- if user not found services api return http 200 but multiplayer is disabled
- if user not found services admit return http 400
- timezone filed in status request is useless but anyway required and validated
- services api request is POST (not GET) because it is not idempotent. It changes server state and after 5 retries return another result.
- 'undefined' status looks useful, it helps to separate the cases of refuse and tech failure

