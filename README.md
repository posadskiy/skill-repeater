# Skill Repeater

A spaced-repetition platform for tracking and reinforcing skills over time. Define skills with custom intervals and priorities, mark them as repeated to advance levels, and receive email reminders when repetitions are due.

**Live at [repeaty.posadskiy.com](https://repeaty.posadskiy.com)**

## Features

- **Skill management** — create, edit, and delete skills with configurable repetition intervals (hours, days, weeks, months, years)
- **Spaced repetition** — mark skills as repeated to advance the level and auto-calculate the next repetition date
- **Priority system** — assign Low / Medium / High / Critical priority to skills
- **Repetition history** — full audit trail of when each skill was practiced
- **Email reminders** — scheduled background job detects upcoming repetitions and sends templated email notifications
- **User settings** — profile and preference management
- **JWT authentication** — secure API access via external auth service

## Architecture

```
┌──────────────────────────────────────────────────┐
│                  Browser (SPA)                    │
│          Vite · React 19 · Mantine 8             │
└───────┬──────────────┬──────────────┬────────────┘
        │              │              │
   ┌────▼─────┐  ┌─────▼─────┐  ┌────▼─────┐
   │ Skill    │  │   Auth    │  │   User   │
   │ Repeater │  │  Service  │  │  Service │
   │ Service  │  │ (external)│  │(external)│
   └────┬─────┘  └───────────┘  └──────────┘
        │
   ┌────▼──────┐  ┌────────────────┐
   │ PostgreSQL│  │ Email Template │
   │           │  │ Service (ext.) │
   └───────────┘  └────────────────┘
```

| Component | Port | Description |
|-----------|------|-------------|
| **skill-repeater-service** | 8210 | Micronaut REST API — skill CRUD, repeat actions, history, scheduled reminders |
| **skill-repeater-front** | 3000 | React SPA — auth flows, skill management, repetition tracking, settings |
| **PostgreSQL** | 5433 | Skill data, repetition history |

**External services** (not in this repo):
- `auth-service` — login, registration, JWT token management
- `user-service` — user account creation and lookup
- `email-template-service` — templated email delivery for repeat reminders

## Tech Stack

**Backend**
- Java 25, Micronaut 4.5, Maven (multi-module)
- Micronaut Data JDBC, PostgreSQL, Flyway
- Micronaut Security JWT
- OpenAPI / Swagger UI
- Jaeger tracing, Micrometer + Prometheus metrics
- Lombok, JUnit 5

**Frontend**
- Vite 6, React 19, TypeScript 5
- Mantine 8, Tabler Icons
- TanStack React Query, React Router 7, Axios
- ESLint 9

**Infrastructure**
- Docker (Amazon Corretto 25 for backend, Nginx Alpine for frontend)
- Kubernetes with Traefik ingress, Let's Encrypt TLS
- Aiven PostgreSQL in production
- Observability stack integration (Jaeger, Prometheus, Promtail)

## Repository Structure

```
skill-repeater/
├── docker-compose.dev.yml          # Full local dev stack
├── deployment/                     # Shared K8s config
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── secrets.yaml
│   ├── ingress/                    # Traefik IngressRoute + middleware
│   └── scripts/                    # Cluster setup, Docker Hub, versioning
├── skill-repeater-front/           # React SPA
│   ├── src/
│   ├── Dockerfile.prod
│   ├── deployment/
│   └── .github/workflows/
└── skill-repeater-service/         # Micronaut backend (Maven multi-module)
    ├── skill-repeater-api/         # DTOs, enums, validation
    ├── skill-repeater-core/        # Domain logic, repositories, scheduler, notifications
    ├── skill-repeater-web/         # HTTP controllers, Flyway, security, app entry point
    ├── Dockerfile.prod
    ├── deployment/
    └── .github/workflows/
```

## Getting Started

### Prerequisites

- Java 25+
- Maven 3.9+
- Node.js 22+
- Docker & Docker Compose

### Quick Start (Docker Compose)

1. Create external Docker networks (first time only):

```bash
docker network create skill-repeater-network
docker network create observability-stack-network
```

2. Set environment variables:

```bash
export SKILL_REPEATER_DATABASE_NAME=skill_repeater_db
export SKILL_REPEATER_DATABASE_USER=repeater
export SKILL_REPEATER_DATABASE_PASSWORD=repeater
export JWT_GENERATOR_SIGNATURE_SECRET=your-secret-here
export GITHUB_USERNAME=your-github-username
export GITHUB_TOKEN=your-github-token
```

3. Start everything:

```bash
docker compose -f docker-compose.dev.yml up -d
```

- Backend API: [http://localhost:8210](http://localhost:8210)
- Frontend: [http://localhost:3000](http://localhost:3000)
- PostgreSQL: `localhost:5433`

### Manual Development

**Backend** (from `skill-repeater-service/`):

```bash
mvn clean package -DskipTests
java -jar skill-repeater-web/target/skill-repeater-web-*.jar
```

**Frontend** (from `skill-repeater-front/`):

```bash
# Create .env with required variables:
# VITE_API_URL=http://localhost:8210/
# VITE_AUTH_URL=http://localhost:8100/
# VITE_USER_URL=http://localhost:8090/v0

npm ci
npm run dev
```

## Deployment

### Order of Operations

1. **Prepare cluster** — set env vars and run shared setup:

```bash
./deployment/scripts/k3s/deploy-to-k3s.sh
```

2. **Build and push images**:

```bash
./deployment/scripts/dockerhub/build-and-push-all.sh <version>
```

3. **Deploy each service**:

```bash
cd skill-repeater-service && ./deployment/scripts/deploy.sh <version>
cd skill-repeater-front && ./deployment/scripts/deploy.sh <version>
```

### Production

- **Frontend:** `repeaty.posadskiy.com`
- **API:** `api.posadskiy.com/skill-repeater/*` (Traefik strips prefix before forwarding)
- **Database:** Aiven managed PostgreSQL
- **TLS:** Let's Encrypt via Traefik

## License

Private project.
