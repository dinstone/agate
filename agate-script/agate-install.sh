docker-compose stop
echo y | docker-compose rm

cd ..
mvn clean package

cd agate-script
docker-compose up -d