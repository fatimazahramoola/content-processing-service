# Content Processing Service

A Spring Boot service that validates legal XML documents, transforms them into normalized JSON using XSLT 3.0 (Saxon-HE), publishes normalized artifacts, and exposes batch processing capabilities.

This project was completed as part of a Senior Java Engineer technical assessment.

---

## Technology Stack

- Java 21
- Spring Boot 3
- Saxon-HE (XSLT 3.0)
- Maven
- JUnit 5
- Spring Boot Actuator

---

## Features

- XML validation against an XSD schema
- XML transformation to normalized JSON using XSLT
- Artifact publishing
- Duplicate publication prevention
- Retrieval of published artifacts
- Batch document processing
- Configurable concurrent processing
- Health and metrics endpoints
- Docker packaging

---

## Processing Pipeline

```
XML
   │
   ▼
XSD Validation
   │
   ▼
XSLT Transformation
   │
   ▼
Metadata Extraction
   │
   ▼


---

## Running locally

```bash
./mvnw spring-boot:run
```

The service starts on:

```
http://localhost:8080
```

---

## Running tests

```bash
./mvnw test
```

---

## REST API

### Process a document

```
POST /api/v1/documents
```

### Process a batch

```
POST /api/v1/documents/batch
```

### Retrieve a published artifact

```
GET /api/v1/documents/{contentId}
```

---

## Health

```
GET /actuator/health
GET /actuator/health/readiness
GET /actuator/metrics
```

---

## Configuration

Batch concurrency is configurable:

```yaml
content-processing:
  batch:
    concurrency: 4
```

---

## Project Structure

```
api/
    REST controllers
    DTOs

processing/
    XML validation
    XSLT transformation
    Metadata extraction
    Batch orchestration

publishing/
    Artifact storage
```

---

See `SOLUTION.md` for design decisions and architecture.
