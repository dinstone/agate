docker compose stop
echo y | docker compose rm

cd ..
mvn clean install

cd agate-script
docker compose up -d