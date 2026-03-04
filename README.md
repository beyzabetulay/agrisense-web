# 🌾 AgriSense — Smart Agriculture Monitoring Platform

A full-stack web application for real-time agricultural monitoring. Track sensors, record measurements, set alert rules, and visualize farm data — all from a single dashboard.

![Quarkus](https://img.shields.io/badge/Backend-Quarkus%203.29-blue)
![React](https://img.shields.io/badge/Frontend-React%2018-61dafb)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ed)
![License](https://img.shields.io/badge/License-MIT-green)

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Quick Start with Docker](#-quick-start-with-docker)
- [Development Setup](#-development-setup)
- [API Endpoints](#-api-endpoints)
- [Screenshots](#-screenshots)
- [Architecture](#-architecture)

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 📊 **Dashboard** | Overview with KPI cards, mini area charts (last 24h), and quick navigation |
| 🌡️ **Sensor Management** | Full CRUD for sensors with type/status tracking |
| 📏 **Measurements** | Record and query measurements with date/sensor filtering |
| 🚨 **Alert Rules** | Define threshold-based alert rules per sensor |
| 🔔 **Alert History** | View and resolve triggered alerts |
| 🏗️ **Field Management** | Organize sensors by agricultural fields |
| 👨‍🌾 **Farmer Profiles** | Multi-farmer support |
| 🔔 **Toast Notifications** | Real-time success/error/warning feedback |
| 📱 **Responsive Design** | Works on desktop and mobile |
| 🎨 **Dark Forest Theme** | Modern, eye-friendly dark green UI |

---

## 🛠️ Tech Stack

### Backend
- **Java 21** with **Quarkus 3.29** (RESTful API)
- **Hibernate ORM** + **JPA** (Data persistence)
- **H2 Database** (In-memory, auto-seeded)
- **Hibernate Validator** (Input validation)
- **Caffeine Cache** (Performance optimization)
- **Hexagonal Architecture** (Ports & Adapters pattern)

### Frontend
- **React 18** with **Vite 5** (Fast HMR)
- **React Router DOM 6** (SPA routing)
- **Recharts** (Data visualization)
- **Axios** (HTTP client with error interceptors)
- **Vanilla CSS** (Custom dark theme, no framework)

### DevOps
- **Podman / Docker** (Containerization)
- **Docker Compose** (Multi-service orchestration)
- **Nginx** (Frontend serving + API reverse proxy)

---

## 📁 Project Structure

```
agrisense-web/
├── docker-compose.yml          # Container orchestration
├── agrisense-backend/
│   └── agrisense-backend/
│       ├── Dockerfile           # Multi-stage Maven + JRE build
│       ├── pom.xml
│       └── src/
│           ├── main/
│           │   ├── java/io/agrisense/
│           │   │   ├── adapter/in/web/     # REST Controllers & DTOs
│           │   │   ├── adapter/out/        # JPA Persistence Adapters
│           │   │   ├── domain/model/       # Domain Entities & Enums
│           │   │   ├── domain/service/     # Business Logic
│           │   │   └── ports/              # Use Case & Repository Interfaces
│           │   └── resources/
│           │       ├── application.properties
│           │       └── import.sql          # Seed data
│           └── test/                       # Unit tests
│
└── agrisense-frontend/
    ├── Dockerfile               # Multi-stage Node + Nginx build
    ├── nginx.conf               # SPA routing + API proxy
    ├── package.json
    └── src/
        ├── api/                 # Axios client & service modules
        ├── components/          # Reusable UI components
        ├── context/             # React Context (Toast system)
        ├── pages/               # Page components (Dashboard, Sensors, etc.)
        └── utils/               # Formatting & data helpers
```

---

## 🐳 Quick Start with Docker

### Prerequisites
- **Docker** or **Podman** installed
- **docker-compose** or **podman-compose** installed

### Run
```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/agrisense-web.git
cd agrisense-web

# Build and start all services
docker compose up --build
# or with Podman:
podman-compose up --build
```

### Access
| Service | URL |
|---------|-----|
| 🌐 **Frontend** | http://localhost:3000 |
| ⚙️ **Backend API** | http://localhost:8081/api |

> **Note:** The first build takes ~3-5 minutes (Maven dependency download). Subsequent builds use cached layers and are much faster.

### Stop
```bash
docker compose down
# or
podman-compose down
```

---

## 🔧 Development Setup

### Backend (Quarkus Dev Mode)
```bash
cd agrisense-backend/agrisense-backend
./mvnw quarkus:dev
```
Backend runs at `http://localhost:8081` with live reload.

### Frontend (Vite Dev Server)
```bash
cd agrisense-frontend
npm install
npm run dev
```
Frontend runs at `http://localhost:3000` with HMR. API calls are proxied to `:8081` via Vite config.

---

## 🔌 API Endpoints

### Sensors
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/sensors` | List all sensors |
| GET | `/api/sensors/{id}` | Get sensor by ID |
| POST | `/api/sensors` | Create sensor |
| PUT | `/api/sensors/{id}` | Update sensor |
| DELETE | `/api/sensors/{id}` | Delete sensor |

### Measurements
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/measurements?sensorId=&from=&to=&page=&size=` | Query measurements |
| POST | `/api/measurements` | Record measurement |

### Alert Rules
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/sensors/{id}/rules` | List rules for sensor |
| POST | `/api/sensors/{id}/rules` | Create alert rule |
| DELETE | `/api/sensors/{id}/rules/{ruleId}` | Delete rule |

### Alerts
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/alerts` | List all alerts |
| PUT | `/api/alerts/{id}/close` | Resolve alert |

### Fields & Farmers
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/fields` | List fields |
| POST | `/api/fields` | Create field |
| GET | `/api/farmers` | List farmers |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────┐
│                   Frontend                       │
│  React + Vite + Recharts                        │
│  Served by Nginx (:3000)                        │
│  Nginx proxies /api/* → backend:8081            │
└────────────────────┬────────────────────────────┘
                     │ HTTP (REST)
┌────────────────────▼────────────────────────────┐
│                   Backend                        │
│  Quarkus 3.29 (:8081)                           │
│                                                  │
│  ┌──────────┐  ┌──────────┐  ┌───────────────┐ │
│  │Controller│→│ Use Case  │→│  Repository   │ │
│  │  (REST)  │  │ (Service) │  │  (JPA/H2)    │ │
│  └──────────┘  └──────────┘  └───────────────┘ │
│                                                  │
│  Hexagonal Architecture (Ports & Adapters)      │
└─────────────────────────────────────────────────┘
```

---

## 📝 Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `VITE_API_BASE_URL` | _(empty)_ | Backend URL for frontend (empty = relative) |
| `QUARKUS_HTTP_PORT` | `8081` | Backend HTTP port |
| `QUARKUS_HTTP_HOST` | `0.0.0.0` | Backend bind address |

---

## 🧪 Running Tests

```bash
cd agrisense-backend/agrisense-backend
./mvnw test
```

---

## 📄 License

This project is developed as part of an academic/learning exercise.
