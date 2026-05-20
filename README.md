# StellSpec Console

StellSpec Console is the query and visualization layer for StellSpec log data stored in Elasticsearch. It provides a web console and query API for searching observability logs with SQL-like query syntax.

## Position in the Pipeline

```text
OpenTelemetry Collector -> Kafka -> stellspec-service -> Elasticsearch -> stellspec-console
```

## Language Decision

Java is the better fit for the query parser and backend query engine.

Reasons:

- Mature parser ecosystem for SQL-like languages, especially ANTLR and Apache Calcite.
- Strong Elasticsearch Java API Client support.
- Easier shared domain modeling with `stellspec-service`.
- Better alignment with Spring Boot security, API, and observability conventions.
- Lower long-term integration cost if the service side is also Java.

Go remains a good option for a lightweight query proxy or single-binary deployment, but for a SQL-like parser plus Elasticsearch query compiler, Java provides a stronger ecosystem and better extensibility.

## Responsibilities

- Provide a web interface for log search and inspection.
- Parse SQL-like log query statements.
- Compile validated queries into Elasticsearch DSL.
- Support filtering by service, severity, trace context, resource attributes, and time range.
- Return paginated and sortable log results for frontend rendering.

## Recommended Stack

- Java 21+
- Spring Boot
- ANTLR or Apache Calcite for query parsing
- Elasticsearch Java API Client
- TypeScript frontend

## Status

This repository is reserved for the StellSpec log query console and SQL-like query API.
