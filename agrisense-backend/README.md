# AgriSense Backend

REST API for managing sensors, measurements, and alert rules built with Quarkus, JPA/Hibernate, and H2 database.

## Requirements

- Java 21+ (tested with Java 21 and 25)
- Maven 3.8+ (Maven Wrapper included: `./mvnw`)

## Running the Application

### Development Mode

```bash
./mvnw compile quarkus:dev
```

Server runs on `http://localhost:8080` with hot reload enabled.

### Build JAR

```bash
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

Server runs on `http://localhost:8081`.

### Build Native Binary

```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
./target/agrisense-backend-1.0.0-SNAPSHOT-runner
```

## Testing

```bash
./mvnw verify
```

Runs all 122 tests (unit and integration). Expected output:
```
Tests run: 122, Failures: 0, Errors: 0
BUILD SUCCESS
```

## Docker

Build and run Docker image:

```bash
./mvnw verify -Pdocker
docker run -i --rm -p 8081:8081 agrisense-backend:latest
```

## API Examples

All endpoints use base path `/api` for sensors/measurements, and `/sensors/{sensorId}/rules` for alert rules.

**Note:** For dev mode (port 8080), replace `8081` with `8080` in curl commands.

Create sensor:

```bash
curl -X POST http://localhost:8081/api/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sensor A",
    "type": "TEMPERATURE",
    "apiKey": "key123",
    "fieldId": 1
  }'
```

Get sensor:

```bash
curl http://localhost:8081/api/sensors/1
```

List sensors:

```bash
curl http://localhost:8081/api/sensors
```

Create measurement:

```bash
curl -X POST http://localhost:8081/api/measurements \
  -H "Content-Type: application/json" \
  -d '{
    "sensorId": 1,
    "value": 25.5,
    "unit": "C"
  }'
```

Query measurements:

```bash
curl "http://localhost:8081/api/measurements?fieldId=1&page=1&size=20"
```

Create alert rule:

```bash
curl -X POST http://localhost:8081/sensors/1/rules \
  -H "Content-Type: application/json" \
  -d '{
    "ruleName": "High Temperature",
    "condition": "GREATER_THAN",
    "threshold": 30.0,
    "description": "Alert when temp exceeds 30"
  }'
```

Get alert rules:

```bash
curl http://localhost:8081/sensors/1/rules
```

Query alerts:

```bash
curl "http://localhost:8081/api/alerts?status=OPEN&page=1&size=10"
```

Close alert:

```bash
curl -X PATCH http://localhost:8081/api/alerts/1/close
```

## Validation

- Sensor: name, type, apiKey, fieldId required
- Measurement: sensorId, value required
- Alert Rule: ruleName, condition, threshold required
- Status codes: 400 (invalid), 404 (not found), 500 (error)

## Architecture

Hexagonal architecture (Ports & Adapters pattern):
- `adapter.in.web`: REST controllers, DTOs, mappers
- `domain.service`: Business logic, alert evaluation
- `adapter.out`: JPA repositories, entities

## Performance

Caching with Caffeine:
- Alert queries: 8x faster (250ms → 30ms on cached calls)
- Sensor list caching
- Cache invalidation on create/update/delete

## Tech Stack

- Quarkus 3.29.x
- RESTEasy Reactive
- Hibernate ORM / JPA
- Hibernate Validator
- H2 Database (in-memory for tests)
- Caffeine Cache
- JUnit 5, RestAssured, Mockito

## Contributors

- Beyza Betül Ay (5123039)
- Yusuf Ömer Karataş (5123015)
- Nihal Beyza Dogan (5123109)
