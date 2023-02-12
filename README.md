# outfit7 test task

Test application expose two REST APIs, one for checking the state of the services by the users and one for administrators.

## local run instruction 

launch local db: \
docker run -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=123 -d postgres

run application: \
./gradlew bootRun

run integrationTest: \
./gradlew integrationTest

## Request examples

### Admin api:
curl http://localhost:8080/api/users \
curl http://localhost:8080/api/users/123 \
curl -X DELETE http://localhost:8080/api/users/123 -v

### Services api:
curl -X POST http://localhost:8080/admin/services/status  -H "Content-Type: application/json" -d'{"userId": "123","timezone": "GMT","cc": "US"}'

Possible response: \
{"multiplayer":"disabled","user-support":"enabled","ads":"undefined"}

## Comments
- services api request is POST (not GET) because it is not idempotent 
(It changes server state and after 5 retries returns another result). 
Also GET can be caсhed during net transfer (and it skews user skill level).  
- 'undefined' status in response looks useful, it helps to separate the cases of refuse and tech failure
- if user not found services api returns http code 200 with 'undefined' multiplayer status 
- if user not found admin api returns http code 404
- 'timezone' field in status request looks useless but anyway required and validated
- delete method returns http code 200 (deleted) and 204 (nothing to delete) 
- the method "get all users" has a limit on the number of returned users (default 100) 


