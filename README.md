# DevTrace 🚀

**DevTrace is a distributed observability platform that helps developers detect, trace, and investigate failures in microservice applications.**

It collects OpenTelemetry traces, reconstructs distributed requests, identifies the root cause of failures, and generates an easy-to-understand incident report.

---

## 🎯 Problem

Debugging a microservice failure often requires checking multiple services, logs, traces, and monitoring tools.

For example:

```text
API Gateway
    ↓
Order Service
    ↓
Payment Service ❌
    ↓
Database
```

Finding the actual cause can take significant time.

**DevTrace brings this information together in one place.**

---

## ✨ Features

### 🔍 Distributed Trace Reconstruction

Reconstructs out-of-order OpenTelemetry spans into a complete request hierarchy.

### 🎯 Root Cause Analysis

Analyzes failed requests and their dependencies to identify the most likely root cause.

### 🤖 AI Incident Investigator

Uses sanitized logs and trace information to generate:

* Incident summary
* Root cause
* Affected services
* Suggested next steps

### 📊 Trace Visualization

Interactive dashboard showing:

* Trace waterfalls
* Service dependencies
* Request latency
* Failed services
* Error propagation

### 🧪 Microservices Simulator

Includes a Dockerized e-commerce system for generating realistic distributed traffic.

Services include:

* API Gateway
* Order Service
* Inventory Service
* Payment Service

### 🚨 Failure Simulation

Simulate common production failures such as:

* Payment timeout
* Service failure
* Database errors
* Network failures

---

## 🏗️ Architecture

```text
                  ┌──────────────────┐
                  │ Microservices    │
                  │                  │
                  │ Gateway          │
                  │ Order            │
                  │ Inventory        │
                  │ Payment          │
                  └────────┬─────────┘
                           │
                    OpenTelemetry
                           │
                           ▼
                  ┌──────────────────┐
                  │ OTEL Collector   │
                  └────────┬─────────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │ DevTrace Backend │
                  │                  │
                  │ Trace Engine     │
                  │ RCA Engine       │
                  │ AI Investigator  │
                  └────────┬─────────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │   PostgreSQL     │
                  └──────────────────┘
                           │
                           ▼
                  ┌──────────────────┐
                  │ React Dashboard  │
                  └──────────────────┘
```

---

## 🛠️ Tech Stack

| Category       | Technologies            |
| -------------- | ----------------------- |
| Backend        | Java 21, Spring Boot 3  |
| Database       | PostgreSQL, Flyway      |
| Frontend       | React, TypeScript, Vite |
| Visualization  | React Flow              |
| Observability  | OpenTelemetry, Jaeger   |
| Infrastructure | Docker, Docker Compose  |
| Security       | Spring Security         |
| CI/CD          | GitHub Actions          |
| AI             | OpenAI API              |

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/karthu097/DevTrace.git
cd DevTrace
```

### 2. Configure environment variables

```bash
cp .env.example .env
```

Add your OpenAI API key to `.env` if you want to enable the AI Investigator.

> The application can also run with a mock AI response.

### 3. Start DevTrace

```bash
docker compose -f docker-compose.prod.yml up --build -d
```

This starts the DevTrace backend, frontend, database, telemetry infrastructure, and microservice simulator.

### 4. Open the dashboard

Open:

```text
http://localhost:3000
```

You should see distributed traffic and service activity.

---

## 🚨 Test Failure Detection

You can simulate a payment timeout:

```bash
curl -X POST http://localhost:8085/api/demo/incidents/payment-timeout
```

DevTrace will:

```text
Detect Failure
      ↓
Reconstruct Trace
      ↓
Analyze Dependencies
      ↓
Identify Root Cause
      ↓
Generate Incident Report
```

---

## 📚 Documentation

* [Architecture & Design](https://github.com/karthu097/DevTrace/blob/main/docs/architecture.md)
* [Live Demo Guide](https://github.com/karthu097/DevTrace/blob/main/docs/demo.md)

---

## 🎯 Project Goals

DevTrace was built to demonstrate practical knowledge of:

* Distributed systems
* Microservices architecture
* Observability
* OpenTelemetry
* Root-cause analysis
* REST APIs
* Database design
* Docker containerization
* React-based visualization
* AI-assisted incident investigation

---

## 👨‍💻 Author

**karthu097**

Built as a hands-on project to explore distributed systems, backend engineering, and observability.
