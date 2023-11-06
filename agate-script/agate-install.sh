docker-compose stop
echo y | docker-compose rm

cd ..
mvn clean package

cd agate-docker
docker-compose up -d