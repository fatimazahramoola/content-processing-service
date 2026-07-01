# Solution Design

## Overview

The service ingests legal XML documents, validates them against an XML Schema, transforms valid documents into a normalized JSON representation using XSLT 3.0 executed by Saxon-HE, publishes the transformed artifact, and supports concurrent batch processing.

The implementation focuses on separation of concerns, simplicity, and production-oriented design while remaining suitable for a take-home assignment.

---

## Architecture

The solution is divided into three primary areas.

## API

Responsible only for HTTP concerns.

- request validation
- response handling
- endpoint definitions

Business logic is delegated to dedicated services.

---

## Processing

Responsible for the document pipeline.

```
XML
    ↓
Validate
    ↓
Transform
    ↓
Extract metadata
    ↓
Publish
```

Responsibilities are intentionally separated:

- XmlValidator
- XsltTransformer
- XmlMetadataExtractor
- XmlProcessingService
- XmlBatchProcessingService

This keeps each class focused on a single responsibility.

---

## Publishing

Artifacts are stored independently from processing.

The current implementation uses an in-memory store suitable for demonstration purposes.

Artifacts are keyed by `contentId`.

Repeated submissions of the same content do not overwrite the originally published artifact.

---

## Validation

XML documents are validated against the supplied XSD before transformation.

Malformed or schema-invalid documents are rejected and return diagnostics.

Invalid documents are never published.

---

## Transformation

Transformation is performed using Saxon-HE executing an XSLT 3.0 stylesheet.

The stylesheet produces a normalized JSON representation matching the required legal judgment structure.

---

## Duplicate Handling

Published artifacts are keyed by `contentId`.

Publishing uses an atomic `saveIfAbsent()` operation.

This guarantees that repeated submissions of identical content cannot overwrite an existing published artifact.

---

## Batch Processing

Batch processing is intentionally implemented separately from single-document processing.

The controller delegates to `XmlBatchProcessingService`.

Each document is processed using the existing `ProcessingService`, avoiding duplicated business logic.

Concurrency is configurable through application configuration.

Results preserve the original request ordering.

---

## Operability

Spring Boot Actuator exposes:

- health
- readiness
- metrics

Batch concurrency is externalized through configuration.

---

## Cloud Deployment

The application is containerized using Docker.

A multi-stage build is used to produce a lightweight runtime image.

Configuration is externalized through Spring Boot configuration properties and environment variables, allowing deployment-specific settings without code changes.

---

## Cloud Design (AWS)

| Responsibility | AWS Service |
| --- | --- |
| Input storage | Amazon S3 |
| Processing trigger | Amazon SQS |
| Application hosting | Amazon ECS Fargate |
| Artifact storage | Amazon S3 |
| Metadata | Amazon DynamoDB |
| Monitoring | CloudWatch + Spring Boot Actuator |

---

## Preventing Duplicate Publishing

The current implementation prevents duplicate publication using the in-memory artifact store.

For production this responsibility would move to persistent storage using the `contentId` as a unique key, allowing atomic insert semantics.

---

## Future RAG Evolution

To support downstream retrieval-augmented generation I would additionally produce:

- plain text representation
- paragraph metadata
- citation metadata
- court metadata
- document embeddings
- chunk identifiers
- provenance metadata

Embeddings would be generated asynchronously after publication to keep document ingestion fast while allowing downstream indexing pipelines to scale independently.

---

## Trade-offs

The implementation intentionally favours readability and separation of concerns over unnecessary abstraction.

Examples include:

- in-memory storage instead of persistence
- ExecutorService instead of a distributed work queue
- focused services with constructor injection
- minimal interfaces
- XSLT dedicated solely to transformation

These choices keep the implementation easy to understand while allowing each component to be replaced independently in a production environment.

## Potential Enhancements

If evolving this into a production content-processing platform, I would consider:

- Replacing the in-memory artifact store with persistent storage (e.g. Amazon S3 and DynamoDB).
- Using a managed message queue (e.g. Amazon SQS) to decouple ingestion from processing.
- Supporting streaming XML processing for very large documents.
- Adding Micrometer metrics for validation, transformation and publishing durations.
- Adding distributed tracing with OpenTelemetry.
- Introducing authentication and authorization for the processing API.
- Persisting processing history and audit events.
- Supporting asynchronous batch submission with job status tracking.
