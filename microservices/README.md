# Microservice Pipeline Overview

This directory hosts the ETL-style pipeline split into three microservices:

1. **Transform Service (`transform-service`)** – accepts raw city payloads, cleans and validates them, and now forwards clean batches to the queue-service over HTTP (`POST /api/queue/messages`). It still queries `city-service` to catch coordinate duplicates.
2. **Queue Service (`queue-service`)** – new component responsible for reliably loading validated batches into the Kafka topic (`city.queue.ready` by default). It exposes a lightweight REST API and abstracts Kafka away from the transform service.
3. **Import Service (`import-service`)** – consumes queued messages from Kafka and persists them to PostgreSQL. The service assumes the payload is already validated, so it rolls back the transaction if duplicates slip through.

## Queue Service API

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/queue/messages` | Accepts a `TransformResultMessage` payload (same schema emitted by the transform service) and publishes it to Kafka. Returns `202 Accepted` with the correlation id.
| `GET` | `/api/queue/health` | Simple readiness probe.

### Configuration

Environment variables for queue-service:
- `SPRING_KAFKA_BOOTSTRAP_SERVERS` – broker address (defaults to `localhost:9092`).
- `QUEUE_KAFKA_TOPIC` – topic to publish validated batches (defaults to `city.queue.ready`).

Transform service needs `QUEUE_SERVICE_URL` so it can POST to the queue-service. Import service can override the topic via `IMPORT_KAFKA_TOPIC`.

Bring everything up via `docker compose` from the `microservices/` directory. The compose file now includes the new queue-service container and wires the services in the Transform → Queue → Import → DB order.
