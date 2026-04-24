# Smart Campus Sensor & Room Management API

**Module :** 5COSC022W Client-Server Architecture  
**Student :** Sureshkumar Aneeshvara  
**Student ID :** w2120147 | 20240883   
**GitHub Repository :** https://github.com/aneeshvara/Smart-Campus-API

---

## Overview

This is a RESTful API built using **JAX-RS (Jersey 2.32)** deployed on **Apache Tomcat 9**, developed as part of the Smart Campus initiative. The API manages university rooms and IoT sensors (temperature, CO2, occupancy, etc.) deployed across campus buildings.

The system follows REST architectural principles: stateless communication, resource-based URLs, appropriate HTTP status codes, and structured JSON responses. All data is stored in-memory using `HashMap` and `ArrayList` as required by the specification.

### API Base URL

```
http://localhost:8080/smart-campus/api/v1
```

### Technology Stack

- **Java 8**
- **JAX-RS** via **Jersey 2.32** (jersey-container-servlet, jersey-hk2)
- **Jackson** for JSON serialisation (jersey-media-json-jackson 2.32)
- **Apache Tomcat 9** as the servlet container
- **Maven** for dependency management

---

## Project Structure

```
smart-campus/
├── pom.xml
└── src/
    └── main/
        ├── java/com/smartcampus/
        │   ├── model/
        │   │   ├── Room.java
        │   │   ├── Sensor.java
        │   │   ├── SensorReading.java
        │   │   ├── ErrorMessage.java
        │   │   └── DataStore.java          ← in-memory HashMap store
        │   ├── resource/
        │   │   ├── DiscoveryResource.java  ← GET /api/v1
        │   │   ├── RoomResource.java       ← /api/v1/rooms
        │   │   ├── SensorResource.java     ← /api/v1/sensors
        │   │   └── SensorReadingResource.java ← sub-resource
        │   ├── exception/
        │   │   ├── RoomNotEmptyException.java
        │   │   ├── RoomNotEmptyExceptionMapper.java
        │   │   ├── LinkedResourceNotFoundException.java
        │   │   ├── LinkedResourceNotFoundExceptionMapper.java
        │   │   ├── SensorUnavailableException.java
        │   │   ├── SensorUnavailableExceptionMapper.java
        │   │   └── GenericExceptionMapper.java
        │   └── filter/
        │       └── LoggingFilter.java
        └── webapp/
            └── WEB-INF/
                └── web.xml
```

---

## How to Build and Run

### Prerequisites

- Java JDK 8 or higher
- Apache Maven 3.6+
- Apache Tomcat 9.x
- Apache NetBeans 18 (recommended IDE)

### Step-by-Step Setup

**1. Clone the repository**
```bash
git clone https://github.com/aneeshvara/Smart-Campus-API.git
cd Smart-Campus-API
```

**2. Build the project**
```bash
mvn clean package
```
This produces `target/smart-campus-1.0-SNAPSHOT.war`

**3. Deploy to Tomcat**

Option A — NetBeans (recommended):
- Open the project in NetBeans (File → Open Project)
- Right-click the project → Clean and Build
- Right-click → Run (NetBeans deploys to Tomcat automatically)

Option B — Manual deployment:
- Copy `target/smart-campus-1.0-SNAPSHOT.war` into Tomcat's `webapps/` folder
- Start Tomcat: `bin/startup.sh` (Mac/Linux) or `bin/startup.bat` (Windows)

**4. Verify the server is running**

Open your browser and navigate to:
```
http://localhost:8080/smart-campus/api/v1
```
You should see the discovery JSON response.

---

## API Endpoints Reference

### Part 1 — Discovery

GET | `/api/v1` - Returns API metadata and resource links 

### Part 2 — Room Management

GET | `/api/v1/rooms` - List all rooms 
POST | `/api/v1/rooms` - Create a new room 
GET | `/api/v1/rooms/{roomId}` - Get a specific room 
 DELETE | `/api/v1/rooms/{roomId}` - Delete a room (fails if sensors still assigned) 

### Part 3 — Sensor Operations

GET | `/api/v1/sensors` - List all sensors (supports `?type=` filter) 
POST | `/api/v1/sensors` - Register a new sensor (validates roomId) 
GET | `/api/v1/sensors/{sensorId}` - Get a specific sensor 

### Part 4 — Sensor Readings (Sub-resource)

GET | `/api/v1/sensors/{sensorId}/readings` - Get all readings for a sensor 
POST | `/api/v1/sensors/{sensorId}/readings` - Add a new reading (updates sensor's currentValue) 

### Error Responses

409 Conflict - Deleting a room that still has sensors 
422 Unprocessable Entity - Creating a sensor with a non-existent roomId 
403 Forbidden - Posting a reading to a MAINTENANCE sensor 
500 Internal Server Error - Any unexpected runtime error (stack trace never exposed) 

---

## Sample curl Commands

### 1. Discovery endpoint — get API metadata
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/
```

### 2. Get all rooms
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### 3. Create a new room
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "CONF-01", "name": "Conference Room A", "capacity": 15}'
```

### 4. Attempt to delete a room with sensors — expect 409 Conflict
```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

### 5. Get sensors filtered by type
```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=Temperature"
```

### 6. Register a sensor with a non-existent roomId — expect 422
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "CO2-999", "type": "CO2", "status": "ACTIVE", "currentValue": 400.0, "roomId": "FAKE-ROOM"}'
```

### 7. Post a reading to a MAINTENANCE sensor — expect 403
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/OCC-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 12.5}'
```

### 8. Post a valid reading and see currentValue update
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 24.7}'
```

### 9. Retrieve reading history for a sensor
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings
```

### 10. Get a specific room by ID
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms/LAB-101
```

---

## Conceptual Report

---

### Part 1 — Service Architecture & Setup

#### Q1 — JAX-RS Resource Lifecycle & In-Memory Data Management

By default, JAX-RS creates a new instance of a resource class for every incoming HTTP request (request-scoped lifecycle). If two clients send requests simultaneously, each receives its own separate resource object. This means instance variables cannot safely hold shared application state.

In my application, all shared data lives in `DataStore.java` using `static final HashMap` fields. Static fields belong to the class itself rather than to any particular instance, so every request, regardless of which resource object is created, reads and writes to the same underlying maps.

However, this does create a risk which is a Race Condition. That is if two requests invoke `sensors.put()` at exactly the same moment, a standard `HashMap` can become internally corrupted. The production-safe solution is to replace `HashMap` with `ConcurrentHashMap`, which uses segment-level locking to serialise concurrent writes without blocking reads. For this single-developer, single-client coursework environment the risk is minimal in practice, but the concern is architecturally real and would need addressing before any multi-user deployment.

---

#### Q2 — HATEOAS: Hypermedia and Self-Documenting APIs

HATEOAS (Hypermedia as the Engine of Application State) is the principle that API responses should include hyperlinks to related resources, not just data. Rather than expecting the client to memorise every endpoint URL, the server communicates navigation options within each response.

The discovery endpoint at `GET /api/v1` demonstrates this. It returns a `resources` map containing the URLs for `/api/v1/rooms` and `/api/v1/sensors`. A client can start at the root and navigate the entire API without ever reading static documentation.

This benefits client developers in two concrete ways. First, if the server changes a URL (e.g., from `/rooms` to `/campus/rooms`), clients following hypermedia links receive the updated URL automatically. No client-side code changes are needed. Secondly, it makes the API self-documenting at runtime, which is always accurate by definition, unlike written documentation that can become stale between releases.

---

### Part 2 — Room Management

#### Q3 — Returning IDs vs Full Objects in List Responses

When `GET /rooms` returns full room objects, the client gets all the data it needs in a single network round trip. This is efficient when the client genuinely requires all fields. For example, a management dashboard rendering a table of rooms with names, capacities, and sensor counts.

However, returning full objects for large collections increases response payload size significantly and therefore bandwidth consumption. If the client only needed a dropdown of room names, most of the transmitted data would be discarded. This is a wasteful pattern that's called over-fetching.

Returning only IDs keeps responses small but forces the client to make N additional `GET /rooms/{id}` requests to retrieve the details it needs, the N+1 problem, which can be significantly worse for performance at scale.

The best balance is to return full objects by default for manageable collections, and offer query parameters such as `?fields=id,name` for clients that need lightweight responses. For this coursework, full objects are returned because collection sizes are small and it avoids unnecessary client-side complexity.

---

#### Q4 — Idempotency of the DELETE Operation

Yes, DELETE is idempotent in this implementation. The HTTP specification defines idempotency as making the same request multiple times produces the same server state as making it once.

The first `DELETE /rooms/CONF-01` call finds the room in the HashMap, removes it, and returns `204 No Content`. Every subsequent identical call finds no room and returns `404 Not Found`, leaving the server state completely unchanged. The room remains deleted. The response code changes between calls, but the server state does not. This matches with the concept of idempotency. From the server's POV, the outcome of any call after the first is identical. That is, the room does not exist. There are no side effects, no partial deletions, and no data corruption. The operation is safe to retry.

---

### Part 3 — Sensor Operations & Filtering

#### Q5 — Consequences of a @Consumes Content-Type Mismatch

The `@Consumes(MediaType.APPLICATION_JSON)` annotation declares to the JAX-RS runtime that the `POST /sensors` method can only process requests whose `Content-Type` header is `application/json`.

If a client sends a request with `Content-Type: text/plain` or `Content-Type: application/xml`, the JAX-RS runtime intercepts the request before it reaches the method body and automatically returns an `HTTP 415 Unsupported Media Type` response. The method is never invoked, so no partial processing, data corruption or side effects can occur.

This is one of the key advantages of declarative annotations in JAX-RS, content negotiation is handled transparently by the framework rather than requiring manual `if` statements or try-catch blocks inside each method. The same mechanism applies in reverse via `@Produces`, which controls the format of the response the method is willing to generate.

---

#### Q6 — @QueryParam vs Path Segments for Filtering Collections

Using `@QueryParam("type")` for filtering (e.g., `GET /api/v1/sensors?type=CO2`) is the correct REST design for three reasons.

1. Semantic Accuracy
2. Composability
3. Optionality

**Semantic Accuracy** — A path segment such as `/sensors/type/CO2` implies that `type` is a unique resource identifier, a sub-resource of `/sensors`. It is not. It is a filter criterion applied to a collection. Query parameters were specifically designed for this use case.

**Composability** — Query parameters combine naturally. `GET /sensors?type=CO2&status=ACTIVE` is immediately readable and extensible with no structural changes. The equivalent path approach `/sensors/type/CO2/status/ACTIVE` is awkward, implies a hierarchy that does not exist, and becomes unmanageable really quickly.

**Optionality** — Query parameters are inherently optional. `GET /sensors` with no parameters returns all sensors, which is correct and expected behaviour. `GET /sensors/type` with no type value would produce a routing mismatch or 404, which is not the intended semantics.

---

### Part 4 — Deep Nesting with Sub-Resources

#### Q7 — Architectural Benefits of the Sub-Resource Locator Pattern

The sub-resource locator pattern delegates all requests to `/sensors/{sensorId}/readings` to a dedicated `SensorReadingResource` class, rather than adding reading endpoints directly into `SensorResource`.

The primary benefit is the separation of concerns. `SensorResource` remains focused entirely on sensor management (create, list, retrieve) while `SensorReadingResource` is entirely focused on reading history. Each class is shorter, easier to read, and straightforward to test in isolation.

The sub-resource locator pattern creates a class hierarchy that naturally mirrors the URL hierarchy. Adding new operations to readings in the future requires only changes to `SensorReadingResource`, with no risk of accidentally breaking sensor management logic. The JAX-RS runtime handles the delegation automatically, so no manual routing code is needed.

---

### Part 5 — Advanced Error Handling, Exception Mapping & Logging

#### Q8 — Why HTTP 422 Is More Semantically Accurate Than 404

A `404 Not Found` response means the resource at the requested URL does not exist. If a client sends `POST /api/v1/sensors`, the URL is completely valid. The `/sensors` collection endpoint exists and is accessible.

The problem is not with the URL. The problem is with the content of the JSON payload, the `roomId` field references a room that does not exist in the system. The request was valid as a syntax (well-formed JSON, correct Content-Type and correct URL) but semantically broken. It contains an unresolvable reference.

`HTTP 422 Unprocessable Entity` was designed precisely for this scenario. It signals that the request was understood by the server but cannot be processed due to a semantic validation failure within the payload itself. Returning 422 gives the client a clear and actionable message "your request reached the right endpoint but the data you sent contains an invalid reference." A misleading 404 would cause the client developer to check whether the URL is wrong, wasting debugging time.

---

#### Q9 — Cybersecurity Risks of Exposing Java Stack Traces

Raw Java stack traces must never be returned to external API consumers because they will expose several sensitive information that attackers can exploit.

1. **Internal Structure Disclosure** — Stack traces include class names and package names (e.g. `com.smartcampus.resource.SensorResource.createSensor`). An attacker learns exactly how the application is organised and which classes handle which operations, enabling targeted attacks.

2. **Library and Version Fingerprinting** — Stack traces include the names and versions of third-party frameworks (e.g. `org.glassfish.jersey.server.ServerRuntime`). Attackers can cross-reference these against public CVE databases to identify known vulnerabilities specific to those exact versions.

3. **Logic Flaws and File Paths** — Line numbers reveal exactly where code failed, which can indicate missing null checks, boundary conditions or validation gaps. Absolute file system paths also sometimes appear, revealing server directory structure.

The `GenericExceptionMapper` in this project catches all `Throwable` instances, logs the full trace to the server console (visible only to administrators) and returns a generic `HTTP 500` JSON response to the client containing no internal information whatsoever.

---

#### Q10 — Why JAX-RS Filters Are Superior to Manual Logger Calls

A logging filter annotated with `@Provider` is automatically invoked by the JAX-RS runtime for every single request and response including endpoints added in the future without any additional developer effort.

The alternative is manually adding `Logger.info()` statements inside every resource method. This approach has several significant drawbacks.

It duplicates code across potentially dozens of methods. It is easy to forget when adding new endpoints, leading to gaps in observability. It mixes concerns, logging logic inside business logic, making methods longer and harder to read. And if the log format ever needs changing, every method must be updated individually, which is error-prone.

Using a filter follows the separation of concerns principle and the DRY (Don't Repeat Yourself) principle. Logging is a cross-cutting concern, it applies uniformly to every request regardless of its purpose. JAX-RS filters are purpose-built for cross-cutting concerns, making them the correct professional approach and the industry standard for API observability.

---

## Seeded Test Data

The following data is pre-loaded on startup for testing:

**Rooms:** `LIB-301` (Library, cap. 50), `LAB-101` (CS Lab, cap. 30), `LEC-201` (Lecture Hall, cap. 120)

**Sensors:** `TEMP-001` (Temperature, ACTIVE, in LIB-301), `CO2-001` (CO2, ACTIVE, in LIB-301), `OCC-001` (Occupancy, MAINTENANCE, in LAB-101), `TEMP-002` (Temperature, OFFLINE, in LEC-201)
