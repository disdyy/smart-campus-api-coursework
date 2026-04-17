# Smart Campus Sensor & Room Management API

**Module:** 5COSC022W - Client-Server Architectures

**Coursework:** Smart Campus REST API using JAX-RS

**Student Name:** Disadhi Ranasinghe

**Student ID:** 20240002 / w2119673

**GitHub Repository:** [(https://github.com/disdyy/smart-campus-api-coursework.git)]

## 1. Overview

This project is a RESTful Smart Campus API built using **JAX-RS (Jersey)** and deployed on **Apache Tomcat**.

It manages:

* Rooms
* Sensors linked to rooms
* Historical sensor readings

The API supports:

* Discovery endpoint
* Room creation, retrieval, and deletion
* Sensor creation and filtering by type
* Nested sub-resource for sensor readings
* Custom exception handling with JSON responses
* Global error handling
* API request and response logging

This project uses **in-memory data structures only** such as maps and lists. It does **not** use a database.

Base API path:

`/api/v1`

## 2. Technology Stack

* Java
* Maven
* JAX-RS (Jersey)
* Apache Tomcat
* JSON
* In-memory collections (`HashMap`, `ArrayList`, `LinkedHashMap`)

## 3. How to Build and Run

### Using Apache NetBeans on Mac

1. Open Apache NetBeans.
2. Click **File > Open Project**.
3. Open the `smart-campus-api` project folder.
4. Make sure Apache Tomcat is configured as the server.
5. Right-click the project and click **Clean and Build**.
6. Right-click the project and click **Run**.
7. The API will run at:

`http://localhost:8080/smart-campus-api/api/v1`

### Using Maven

In the project root, run:

`mvn clean install`

Then deploy the generated WAR file to Tomcat.

## 4. API Endpoints

### Discovery

* `GET /api/v1`

### Rooms

* `GET /api/v1/rooms`
* `POST /api/v1/rooms`
* `GET /api/v1/rooms/{roomId}`
* `DELETE /api/v1/rooms/{roomId}`

### Sensors

* `GET /api/v1/sensors`
* `GET /api/v1/sensors?type=CO2`
* `GET /api/v1/sensors/{sensorId}`
* `POST /api/v1/sensors`

### Sensor Readings

* `GET /api/v1/sensors/{sensorId}/readings`
* `POST /api/v1/sensors/{sensorId}/readings`

### Debug

* `GET /api/v1/debug/error`

## 5. Sample curl Commands

### 1. Discovery endpoint

```bash
curl -X GET "http://localhost:8080/smart-campus-api/api/v1"
```

### 2. Create a room

```bash
curl -X POST "http://localhost:8080/smart-campus-api/api/v1/rooms" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "LIB-301",
    "name": "Library Quiet Study",
    "capacity": 80
  }'
```

### 3. Get all rooms

```bash
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/rooms"
```

### 4. Get a room by ID

```bash
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/rooms/LIB-301"
```

### 5. Create a valid sensor

```bash
curl -X POST "http://localhost:8080/smart-campus-api/api/v1/sensors" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "CO2-001",
    "type": "CO2",
    "status": "ACTIVE",
    "currentValue": 420.0,
    "roomId": "LIB-301"
  }'
```

### 6. Filter sensors by type

```bash
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=CO2"
```

### 7. Add a sensor reading

```bash
curl -X POST "http://localhost:8080/smart-campus-api/api/v1/sensors/CO2-001/readings" \
  -H "Content-Type: application/json" \
  -d '{
    "value": 455.7
  }'
```

### 8. Get sensor readings

```bash
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors/CO2-001/readings"
```

### 9. Trigger global 500 error

```bash
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/debug/error"
```

## 6. Video Demonstration

The video demonstration shows the Postman tests for:

* Discovery endpoint
* Room creation and deletion
* Sensor validation and filtering
* Nested sensor readings
* 409 Conflict
* 422 Unprocessable Entity
* 403 Forbidden
* 500 Internal Server Error without stack trace

## 7. Report Answers

## Part 1: Service Architecture & Setup

### 1.1 Project & Application Configuration

**Question:** In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.

**Answer:** By default, JAX-RS resource classes are usually request-scoped, which means a new resource instance is created for each incoming request. This reduces the risk of unsafe shared instance fields. However, my API stores shared data in central in-memory collections inside a `DataStore` class. Because these collections are shared across requests, they must be managed carefully to avoid race conditions and inconsistent data. In a larger production system, thread-safe collections or explicit synchronization would be appropriate.

### 1.2 The “Discovery” Endpoint

**Question:** Why is the provision of “Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (`HATEOAS`)? How does this approach benefit client developers compared to static documentation?

**Answer:** Hypermedia helps clients discover available actions directly from API responses instead of relying only on static documentation. For example, the discovery endpoint returns links to the main collections such as `/api/v1/rooms` and `/api/v1/sensors`. This makes the API more self-descriptive and easier for client developers to navigate.

## Part 2: Room Management

### 2.1 Room Resource Implementation

**Question:** When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.

**Answer:** Returning only IDs keeps the payload small and saves bandwidth, but the client would need additional requests to fetch the full details. Returning full room objects increases payload size, but it is more convenient because the client receives all useful metadata in one response. In this implementation, returning full room objects makes the API easier to use.

### 2.2 Room Deletion & Safety Logic

**Question:** Is the `DELETE` operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same `DELETE` request for a room multiple times.

**Answer:** `DELETE` is idempotent because repeating the same request should not keep changing the final server state after the resource is already removed. In this implementation, the first `DELETE` removes the room successfully if it has no sensors. Repeating the same `DELETE` later returns `404 Not Found`, but it does not change the final state any further.

## Part 3: Sensor Operations & Linking

### 3.1 Sensor Resource & Integrity

**Question:** We explicitly use the `@Consumes(MediaType.APPLICATION_JSON)` annotation on the `POST` method. Explain the technical consequences if a client attempts to send data in a different format, such as `text/plain` or `application/xml`. How does JAX-RS handle this mismatch?

**Answer:** The `POST` method uses `@Consumes(MediaType.APPLICATION_JSON)`. If the client sends `text/plain` or `application/xml` instead of JSON, JAX-RS rejects the request because the media type does not match what the endpoint accepts. This usually produces `415 Unsupported Media Type`.

### 3.2 Filtered Retrieval & Search

**Question:** You implemented this filtering using `@QueryParam`. Contrast this with an alternative design where the type is part of the URL path (e.g., `/api/v1/sensors/type/CO2`). Why is the query parameter approach generally considered superior for filtering and searching collections?

**Answer:** A query parameter is better for filtering because it keeps the main resource path stable and clearly shows that the client is searching within a collection rather than requesting a different resource. For example, `/sensors?type=CO2` means the sensors collection filtered by type. This is more flexible and more standard than embedding filter logic directly into the path.

## Part 4: Deep Nesting with Sub-Resources

### 4.1 The Sub-Resource Locator Pattern

**Question:** Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., `sensors/{id}/readings/{rid}`) in one massive controller class?

**Answer:** The sub-resource locator pattern improves organisation by moving nested functionality to a dedicated class. Instead of placing all logic for `/sensors`, `/sensors/{id}`, and `/sensors/{id}/readings` in one large resource class, a separate `SensorReadingResource` handles the nested reading operations. This improves readability, maintainability, and separation of concerns.

## Part 5: Advanced Error Handling, Exception Mapping & Logging

### 5.2 Dependency Validation

**Question:** Why is HTTP `422` often considered more semantically accurate than a standard `404` when the issue is a missing reference inside a valid JSON payload?

**Answer:** HTTP `422` is more semantically accurate because the request body is valid JSON, but one field inside it is invalid according to business rules. In this coursework, the `roomId` is inside a valid sensor request body, but it refers to a room that does not exist. A `404` usually indicates that the requested URL resource does not exist, while `422` better describes an invalid reference in the payload.

### 5.4 The Global Safety Net (500)

**Question:** From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

**Answer:** Exposing raw Java stack traces is dangerous because attackers can learn internal technical details such as package names, class names, method names, file paths, and framework details. This information can help them understand the system structure and plan targeted attacks.

### 5.5 API Request & Response Logging Filters

**Question:** Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting `Logger.info()` statements inside every single resource method?

**Answer:** JAX-RS filters are better for logging because logging is a cross-cutting concern that applies to many endpoints. If logging code is manually added to every resource method, it becomes repetitive and harder to maintain. Filters keep logging centralised, consistent, and reusable.

## 8. Final Notes

* This project uses JAX-RS only.
* No Spring Boot was used.
* No database was used.
* Data is stored in memory using Java collections.
* If the server restarts, the in-memory data resets.
