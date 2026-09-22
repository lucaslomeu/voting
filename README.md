# Voting API

REST API para criação de topics, sessões de votação, votos e resultados.

## Stack

- Java 21
- Spring Boot 3.5.9
- PostgreSQL 16
- Spring Data JPA
- Flyway
- Bean Validation
- JUnit, Mockito e Testcontainers
- OpenAPI / Swagger UI
- Docker Compose

## Requisitos

- Java 21
- Docker e Docker Compose

## Executar

```bash
docker compose up -d
./mvnw spring-boot:run
```

A aplicação inicia em `http://localhost:8080` e o PostgreSQL em `localhost:5433`.

Parar o banco:

```bash
docker compose down
```

Os dados ficam no volume Docker `postgres_data`.

## Configuração

| Variável | Default |
| --- | --- |
| `DB_HOST` | `localhost` |
| `DB_PORT` | `5433` |
| `DB_NAME` | `voting` |
| `DB_USERNAME` | `voting` |
| `DB_PASSWORD` | `voting` |
| `SERVER_PORT` | `8080` |

## API v1

### Criar topic

```http
POST /api/v1/topics
```

```json
{
  "title": "Approval of the 2026 budget"
}
```

Retorna `201 Created` com `id`, `title` e `createdAt`.

### Abrir sessão

```http
POST /api/v1/topics/{topicId}/sessions
```

```json
{
  "durationInSeconds": 60
}
```

O body `{}` usa 60 segundos. A sessão aceita votos enquanto `currentTime < expiresAt`.

### Registrar voto

```http
POST /api/v1/topics/{topicId}/votes
```

```json
{
  "memberId": "123456",
  "vote": "YES"
}
```

`vote` aceita `YES` ou `NO`. O mesmo membro vota apenas uma vez por topic.

### Consultar resultado

```http
GET /api/v1/topics/{topicId}/result
```

```json
{
  "topicId": 1,
  "totalVotes": 10,
  "yesVotes": 7,
  "noVotes": 3
}
```

Pode ser consultado com a sessão aberta ou encerrada.

## Erros

```json
{
  "status": 409,
  "code": "DUPLICATE_VOTE",
  "message": "The member has already voted on this topic.",
  "timestamp": "2026-09-20T15:00:00Z"
}
```

Principais códigos: `VALIDATION_ERROR`, `TOPIC_NOT_FOUND`, `VOTING_SESSION_NOT_FOUND`, `VOTING_SESSION_CLOSED`, `DUPLICATE_VOTE`, `INVALID_VOTE` e `INVALID_DURATION`.

## Swagger

- UI: `http://localhost:8080/swagger-ui.html`
- JSON: `http://localhost:8080/v3/api-docs`

## Testes

```bash
./mvnw clean test
```