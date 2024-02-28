# Introduce

Agate is a cloud-native, fast, scalable, and async API Gateway. its core values are high performance and extensibility.

# Dependency

- Vert.x 4.3
- Consul 1.7

# Quick start

## Startup gateway and admin 

- step1: pull source and build

```shell
git pull https://github.com/dinstone/agate.git

// build
cd agate
mvn clean package
```

- step2: start agate admin and gateway

Windows:
```shell
cd agate-script
 .\agate-install.bat 
```

Linux:
```shell
cd agate-script
 ./agate-install.sh
```

or execute the following command:
```shell
cd agate
mvn clean install

cd agate-script
docker compose stop
echo y | docker compose rm
docker compose up -d
```

- step3: test agate admin and gateway services

```shell
access agate admin using username/password = agate/123456 by url http://localhost:8888/	
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

- Logging
- Tracing
- Metrics
- Rate Limit
- Circuit Breaker
- Gray Deployment
- Security Authenticate
- Routing: Http Reverse Proxy
- Routing: Http Service Discovery

# Architecture

![System Architecture](https://github.com/dinstone/agate/wiki/imgs/arch00.png)

# Other Documents

goto wiki https://github.com/dinstone/agate/wiki
