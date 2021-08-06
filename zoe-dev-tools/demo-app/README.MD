# build
mvn package -Pnative

docker build -f src/main/docker/Dockerfile.native -t zoe-demo-app:1 .

# run
docker run -i --rm -p 8080:8080 zoe-demo-app:1

# run via maven debug
mvn compile quarkus:dev

# mongo - start mongo and mongo express 
docker-compose -f zoe-dev-tools/demo-app/src/main/resources/docker/stack.yml up -d

# mongo shell
docker exec -it <CONTAINER_ID> bash

# create db via shell
mongosh
use admin
db.auth("root", "example")
db.createUser( { user: "appUser", pwd: "123456", roles: [ { role: "readWrite", db: "test-assets" } ] } )

