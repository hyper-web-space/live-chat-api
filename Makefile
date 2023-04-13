docker-build:
	docker build -t live-chat-api:latest .
up:
	docker compose up -d
down:
	docker compose down
