docker-build-api:
	docker build -t live-chat-api:latest -f ./live-chat-api/Dockerfile .
up:
	docker compose up -d
down:
	docker compose down
