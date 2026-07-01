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
Artifact Publishing
   │
   ▼
Retrieval by contentId
```

---

## Local Verification

### Prerequisites

Ensure the following are installed:

- Java 21
- Git
- Postman (or another HTTP client) for exercising the REST API

Optional for Docker verification:

- Docker
- Colima (macOS)

Start the Docker runtime (optional):

```bash
colima start
```

Verify Docker is available (optional):

```bash
docker version
```

### Run the test suite

```bash
./mvnw test
```

Expected result:

```
BUILD SUCCESS
```

### Start the application

```bash
./mvnw spring-boot:run
```

The application will be available at:

```
http://localhost:8080
```

### Verify the API

The sample request payloads referenced below are available in the `examples/` directory.

#### 1. Process a valid XML document

```
POST http://localhost:8080/api/v1/documents
```

Use:

```
examples/valid-request.json
```

Expected:

- HTTP 202 Accepted
- Processing status: `ACCEPTED`
- Normalized JSON returned

#### 2. Retrieve the published artifact

```
GET http://localhost:8080/api/v1/documents/za-gp-2024-001
```

Expected:

- HTTP 200 OK
- Published artifact returned

#### 3. Verify duplicate handling

Submit:

```
examples/duplicate-request.json
```

using:

```
POST http://localhost:8080/api/v1/documents
```

Repeat:

```
GET http://localhost:8080/api/v1/documents/za-gp-2024-001
```

The processing request succeeds, but retrieving the artifact by the same contentId should still return the originally published artifact.

#### 4. Batch processing

```
POST http://localhost:8080/api/v1/documents/batch
```

Use:

```
examples/batch-request.json
```

Expected:

- HTTP 202 Accepted
- Two successfully processed documents

#### 5. Invalid XML

```
POST http://localhost:8080/api/v1/documents
```

Use:

```
examples/invalid-request.json
```

Expected:

- Processing status: `REJECTED`
- Diagnostic explaining the schema validation failure

#### 6. Health endpoint

```
GET http://localhost:8080/actuator/health
```

Expected:

- The service reports a healthy (`UP`) status.

#### 7. Runtime metrics

```
GET http://localhost:8080/actuator/metrics
```

Expected:

A list of available runtime metrics.

### Verify the Docker packaging (optional)

Stop the locally running Spring Boot application first:

```bash
Ctrl+C
```

Build the Docker image:

```bash
docker build -t content-processing-service .
```

Run the container:

```bash
docker run --rm -p 8080:8080 content-processing-service
```

The service will be available on:

```
http://localhost:8080
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
