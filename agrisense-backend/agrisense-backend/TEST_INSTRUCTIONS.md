# AgriSense Backend - Test Instructions

## ✅ Test Summary (Portfolio 03)

```bash
Tests run: 122, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS ✅
```

## 1️⃣ Running Tests

```bash
./mvnw verify
```

**Result:** 122 tests, all passing

## 2️⃣ Running the Application

### Running with JAR

```bash
# Build
./mvnw clean package

# Run
java -jar target/quarkus-app/quarkus-run.jar
```

**Port:** http://localhost:8081

### Dev Mode (Hot Reload)

```bash
./mvnw compile quarkus:dev
```

**Port:** http://localhost:8080

## 3️⃣ API Tests

### Sensor CRUD

```bash
# List all sensors
curl http://localhost:8081/api/sensors

# Get sensor by ID
curl http://localhost:8081/api/sensors/1

# Create sensor
curl -X POST http://localhost:8081/api/sensors \
  -H "Content-Type: application/json" \
  -d '{"name":"TestSensor","type":"TEMPERATURE","apiKey":"key123","fieldId":1}'
```

### Measurements

```bash
# Post measurement (triggers alert evaluation)
curl -X POST http://localhost:8081/api/measurements \
  -H "Content-Type: application/json" \
  -d '{"sensorId":1,"value":30.5,"unit":"C"}'

# Query measurements
curl "http://localhost:8081/api/measurements?fieldId=1&page=1&size=10"
```

### Alerts (Cached! ⚡)

```bash
# Get OPEN alerts (first call - from DB)
time curl "http://localhost:8081/api/alerts?status=OPEN&page=1&size=10"

# Get OPEN alerts again (cached - 8x faster!)
time curl "http://localhost:8081/api/alerts?status=OPEN&page=1&size=10"
```

**Cache Test Result:**
- First query: ~0.25s (database)
- Second query: ~0.03s (cache) ⚡ **8x faster!**

### Alert Rules

```bash
# Create alert rule
curl -X POST http://localhost:8081/sensors/1/rules \
  -H "Content-Type: application/json" \
  -d '{"ruleName":"HighTemp","condition":"GREATER_THAN","threshold":25.0,"description":"High temperature alert"}'

# Get active rules for sensor
curl http://localhost:8081/sensors/1/rules
```

## 4️⃣ Docker Build & Run

### Option A: Manual Docker Build (Docker daemon must be running)

```bash
# Build app first
./mvnw clean package

# Build Docker image
docker build -f src/main/docker/Dockerfile.jvm -t agrisense-backend:latest .

# Run container
docker run -i --rm -p 8080:8080 agrisense-backend:latest
```

### Option B: Quarkus Container Extension (Docker daemon must be running)

```bash
./mvnw clean package -Pdocker
```

**Note:** If Docker daemon is not running, `Dockerfile.jvm` is available in the portfolio.

## 5️⃣ Cache Verification

Quarkus Cache (`io.quarkus:quarkus-cache`) is active and working:

**Cached Methods:**
- `AlertQueryService.queryAlerts()` - `@CacheResult(cacheName = "open-alerts")`
- `SensorManagementService.getAllSensors()` - `@CacheResult(cacheName = "sensors-cache")`

**Cache Invalidation:**
- `AlertQueryService.closeAlert()` - `@CacheInvalidateAll(cacheName = "open-alerts")`
- `SensorManagementService.createSensor/updateSensor/deleteSensor()` - `@CacheInvalidate`

## 6️⃣ Portfolio 03 Requirements Checklist

✅ **Hexagonal Architecture** - Ports & Adapters pattern  
✅ **Java 21 + Quarkus** (Spring not used)  
✅ **3 Components**: Domain, API (Web), Persistence  
✅ **REST API** - Proper HTTP methods, status codes, HATEOAS links  
✅ **JPA/Hibernate** - H2 database  
✅ **CRUD + 1:n relationships** - Sensor-Measurement, Field-Sensor, Rule-Alert  
✅ **Maven** - Build tool  
✅ **122 Unit & Integration Tests** - All passing  
✅ **Docker** - Dockerfile.jvm + container-image extension  
✅ **README** - Comprehensive documentation  
✅ **Quarkus Cache** - Performance optimization  

## 7️⃣ Project Structure

```
src/main/java/io/agrisense/
├── adapter/
│   ├── in/web/          # REST Controllers, DTOs, Mappers
│   └── out/             # JPA Repositories, Entities, Mappers
├── domain/
│   ├── model/           # Domain Models (Alert, Sensor, Measurement)
│   └── service/         # Business Logic + Cache
└── ports/
    ├── in/              # Use Case Interfaces
    └── out/             # Repository Interfaces
```

## 8️⃣ Important Files

- `pom.xml` - Dependencies (Quarkus, Cache, Docker, Tests)
- `src/main/resources/application.properties` - Configuration
- `src/main/resources/import.sql` - Sample data
- `src/main/docker/Dockerfile.jvm` - Docker image definition
- `README.md` - Full project documentation

## 🎉 Summary

Project is complete and functional:
- ✅ 122 tests passing
- ✅ Cache performance optimization active
- ✅ Docker support available
- ✅ REST API fully functional
- ✅ Hexagonal architecture implemented

