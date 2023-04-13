# live-chat-api

Redis와 WebSocket을 이용한 라이브 채팅 API

# 아키텍처

![아키텍처 다이어그램](./docs/resources/images/live-chat-api-architecture.png)

* 웹소켓을 이용하여 클라이언트와 실시간 통신
* 레디스 Pub/Sub 을 이용하여 여러 서버에 접속한 클라이언트에게 메시지 전송
* 레디스에 저장된 메시지를 워커에서 몽고디비로 저장

# 빌드

## gradle

```shell
$ gradlew bootJar
```

## 도커 빌드 및 이미지 생성

```shell
$ make docker-build
```

# 실행

```shell
$ java -jar build/libs/live-chat-api.jar
```

## docker-compose 로 실행

```shell
$ docker-compose up -d
```

### docker-compose.yaml 예시

```yaml
version: "3.9"

services:
  api:
    image: live-chat-api
    ports:
      - "8080:8080"
    environment:
      MONGODB_URL: mongodb://mongo:27017
      username: root
      password: root
    depends_on:
      - mongo
  mongo:
    image: mongo
    restart: always
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: root
    volumes:
      - ./mongo-data:/data/db
```
