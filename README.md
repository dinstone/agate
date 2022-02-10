# Agate
Agate is a cloud-native, fast, scalable, and async API Gateway. its core values are high performance and extensibility.

# Dependency
- Vert.x 4.0
- Consul 1.7

# Install and Test
- step1: pull source and build docker images

```
	git pull https://github.com/dinstone/agate.git
	
	// remove old version
	cd agate/agate-docker
	docker-compose stop
	docker-compose rm
	
	// build new images
	cd agate
	mvn clean package
```

- step2: start agate docker containers

```
	cd agate/agate-docker
	docker-compose up -d	
```

- step3: test agate manager and gateway services

```
	access agate manager using username/password = agate/123456 by url http://localhost:8080/	
```

# Config route and Url rewrite
- Http URL Proxy:

```
	http://localhost:4004/(?<url>.*) --> https://www.baidu.com/:url
```
- Http Microservice Discovery: 

```
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

# System Architecture
![System Architecture](https://github.com/dinstone/agate/wiki/imgs/arch00.png)

# System Implement
goto wiki https://github.com/dinstone/agate/wiki
