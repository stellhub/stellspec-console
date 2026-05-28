# StellSpec Console

StellSpec Console is the query control plane for StellSpec log data stored in Elaticsearch. It exposes an HTTP API for frontend applications to execute EQL queries against the data streams written by `stellspec-service`.

## Position in the Pipeline

```text
OpenTelemetry Collector -> Stellflow -> stellspec-service -> Elaticsearch -> stellspec-console
```

## Responsibilities

- Expose a frontend-facing EQL query endpoint.
- Execute queries through `stellflux-spring-boot-starter-elaticsearch`.
- Keep the control plane read-only and separate from the ingestion service.
- Return normalized event and sequence results for UI rendering.
- Provide a status endpoint for local verification.

## Stack

- Java 25
- Spring Boot 3.5.14
- stellflux-spring-boot-starter-http 1.0.1
- stellflux-spring-boot-starter-elaticsearch 1.0.1

## Configuration

| Environment variable | Default | Description |
| --- | --- | --- |
| `SERVER_PORT` | `18091` | HTTP service port. |
| `ELATICSEARCH_ENDPOINT` | `http://192.168.1.14:9200` | Elaticsearch endpoint. |
| `ELATICSEARCH_USERNAME` | empty | Elaticsearch username. |
| `ELATICSEARCH_PASSWORD` | empty | Elaticsearch password. |
| `ELATICSEARCH_API_KEY` | empty | Elaticsearch API key. |
| `STELLSPEC_CONSOLE_EQL_DEFAULT_INDEX` | `logs-*-*` | Default index or data stream pattern. |
| `STELLSPEC_CONSOLE_EQL_TIMESTAMP_FIELD` | `@timestamp` | EQL timestamp field. |
| `STELLSPEC_CONSOLE_EQL_EVENT_CATEGORY_FIELD` | `event.category` | EQL event category field. |
| `STELLSPEC_CONSOLE_EQL_DEFAULT_SIZE` | `100` | Default result size. |
| `STELLSPEC_CONSOLE_EQL_MAX_SIZE` | `500` | Maximum result size. |

## API

### Status

```bash
curl http://127.0.0.1:18091/api/stellspec/console/status
```

### Query

```bash
curl -X POST http://127.0.0.1:18091/api/stellspec/console/eql/query \
  -H "Content-Type: application/json" \
  -d '{
    "query": "any where true",
    "index": ["logs-*-*"],
    "size": 20
  }'
```

## Build

```bash
mvn test
mvn package -DskipTests
```
