<div align="center">
  <h1>DevTrace 🚀</h1>
  <p>
    <strong>A production-grade distributed observability platform that turns microservice failures into a story an engineer can actually understand.</strong>
  </p>
  
  [![Build Status](https://img.shields.io/github/actions/workflow/status/karthu097/DevTrace/backend.yml?branch=main&label=Build&style=for-the-badge)](https://github.com/karthu097/DevTrace/actions)
  [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)
  [![Made with Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)
  [![React UI](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)](https://reactjs.org/)
</div>

<br />

---

## 🛑 The 2 AM Nightmare

You're sound asleep. Your phone buzzes. *PagerDuty.* 

You groggily open your laptop, load up Datadog, Jaeger, Kibana, and CloudWatch. You've got 15 tabs open. You're trying to manually piece together Trace IDs to figure out why the `checkout` service is throwing a `500 Internal Server Error`. 

Was it the Payment Provider? The Inventory Database? A random network timeout? You're guessing. You're manually digging through thousands of JSON logs.

## 🟢 Enter DevTrace

DevTrace completely automates the incident investigation process. 

It ingests OpenTelemetry data, reconstructs your distributed trace, runs a deterministic **Root-Cause Analysis (RCA)** algorithm to pinpoint the exact failing dependency, and leverages AI to write a human-readable incident report based on sanitized logs.

*No more manual log hunting. No more guessing.*

---

## ✨ What makes DevTrace special?

1. **📦 Built-in Microservices Simulator:** You don't need a massive architecture to test this. DevTrace ships with a fully Dockerized e-commerce simulator (API Gateway, Order, Inventory, Payment services) that generates continuous OTLP traffic.
2. **🌳 Trace Reconstruction Engine:** Assembles flat, out-of-order spans back into hierarchical dependency trees.
3. **🎯 Deterministic RCA:** Calculates the "critical path" of a request and isolates the absolute root cause of a failure.
4. **🧠 AI Investigator:** Analyzes sanitized evidence (scrubbed of passwords, CCs, and tokens) to generate an incident summary and actionable next steps.
5. **🎨 Beautiful React UI:** A dark-mode, high-density dashboard featuring dynamic Trace Waterfalls and Node Graphs.

---

## 🛠️ Architecture at a Glance

DevTrace is composed of three massive pillars working in harmony:

- **Observability Pipeline:** Microservices instrumented with OpenTelemetry send gRPC/HTTP Spans to an OTEL Collector, which batches them.
- **DevTrace Engine (Backend):** A Java 21 / Spring Boot brain that persists traces in PostgreSQL, runs the RCA algorithms, and sanitizes data for AI.
- **Developer UI (Frontend):** A React / Vite SPA rendering real-time dependency topology with React Flow.

> Curious about the gritty details? Check out the [Architecture Deep Dive](docs/architecture.md)!

---

## 🚀 Let's Get You Up and Running!

DevTrace is fully containerized. You can spin up the *entire* cluster with a single command.

### 1. Clone & Configure
```bash
git clone https://github.com/karthu097/DevTrace.git
cd DevTrace

# Set up your environment variables
cp .env.example .env
```
*(Optional: Open `.env` and add your OpenAI API Key if you want to see the AI Investigator in action. Otherwise, it will safely mock the AI response).*

### 2. Ignite the Cluster 🔥
```bash
docker compose -f docker-compose.prod.yml up --build -d
```
Docker will now download, compile, and launch all 10 containers (UI, Backend, Database, Telemetry, and the 5 Simulator services). Give it a minute or two to warm up!

### 3. See it in Action
Open your browser and navigate to [http://localhost:3000](http://localhost:3000). You'll see healthy traffic flowing through your system.

### 4. 🚨 Break Everything! (The Fun Part)
Want to see DevTrace earn its keep? Open a new terminal and run this command to simulate a catastrophic payment timeout in the upstream services:
```bash
curl -X POST http://localhost:8085/api/demo/incidents/payment-timeout
```
Now, switch back to the DevTrace Dashboard. You will instantly see the system turn red, the failure propagate, and the AI automatically explain exactly what happened!

---

## 📚 Documentation
- [Architecture & Design](docs/architecture.md)
- [Live Demo Script](docs/demo.md) (Perfect for showing this project off in an interview!)

## 💻 Tech Stack
- **Backend:** Java 21, Spring Boot 3, PostgreSQL, Flyway, Spring Security
- **Frontend:** React, TypeScript, Vite, Tailwind CSS, TanStack Query, React Flow
- **Observability:** OpenTelemetry, Jaeger
- **Infrastructure:** Docker, Docker Compose, GitHub Actions, Kubernetes

---
*Built with ❤️ (and a lot of coffee) by [karthu097](https://github.com/karthu097).*
