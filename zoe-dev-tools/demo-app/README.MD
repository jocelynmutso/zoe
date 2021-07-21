# build
mvn package -Pnative

docker build -f src/main/docker/Dockerfile.native -t zoe-demo-app:1 .

# run
docker run -i --rm -p 8080:8080 zoe-demo-app:1

# run via maven debug
mvn compile quarkus:dev
