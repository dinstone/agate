# Introduce

Agate is a cloud-native, fast, scalable, and async API Gateway. its core values are high performance and extensibility.

# Dependency

- Vert.x 4.3
- Consul 1.7

# Qucik start

## Install gateway and mananger 

- step1: pull source and build docker images

```shell
git pull https://github.com/dinstone/agate.git

// remove old version
cd agate/agate-docker
docker-compose stop
docker-compose rm

// build new images
cd agate
mvn clean install
```

- step2: start agate docker containers

```shell
cd agate/agate-docker
docker-compose up -d
```

- step3: test agate manager and gateway services

```shell
access agate manager using username/password = agate/123456 by url http://localhost:8080/	
```

## Config Route and Url rewrite

- Http URL Proxy:

```shell
http://localhost:4004/(?<url>.*) --> https://www.baidu.com/:url
```

- Http Microservice Discovery: 

```shell
http://localhost:4002/user-provider/(?<url>.*) --> http://user-provider/:url
```

# Feature

- Access Logging
- Rate Limit
- Tracing
- Metrics
- Security Athen
- Circuit Breaker
- Gray Deployment
- Routing: Http Reverse Proxy
- Routing: Http Service Discovery

# Architecture

![System Architecture](https://github.com/dinstone/agate/wiki/imgs/arch00.png)

# Other Documents

goto wiki https://github.com/dinstone/agate/wiki
