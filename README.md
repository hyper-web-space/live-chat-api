# live-chat-api

Redis와 WebSocket을 이용한 라이브 채팅 API

# 아키텍처
![아키텍처 다이어그램](./docs/resources/images/live-chat-api-architecture.png)
* 웹소켓을 이용하여 클라이언트와 실시간 통신
* 레디스 Pub/Sub 을 이용하여 여러 서버에 접속한 클라이언트에게 메시지 전송
* 레디스에 저장된 메시지를 워커에서 몽고디비로 저장