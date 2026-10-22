# Skill Repeater Service

Micronaut backend for the Skill Repeater platform. Provides a REST API for managing skills with spaced-repetition logic, repetition history tracking, and scheduled email reminders.

## Features

- **Skill CRUD** — create, read, update, and delete skills per user
- **Spaced repetition engine** — a review is rated Again, Hard, Good, or Easy. Good and Easy raise the level and grow the interval; Again returns to the starting interval.
- **Repetition history** — each review stores the rating, the interval just applied, and the level
- **Scheduled reminders** — one job every 15 minutes emails due skills once, after the user's local reminder hour
- **OpenAPI / Swagger UI** — auto-generated API documentation in development
- **Distributed tracing** — Jaeger integration for request tracing
- **Metrics** — Prometheus endpoint for monitoring
- **JWT security** — endpoints require a bearer token whose subject owns the skill. Swagger is disabled in production, and Prometheus is not routed on the public ingress.

## API Endpoints

### Skills (`/v0/skills`)

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/v0/skills?page&size` | Page of the current user's skills |
| `GET` | `/v0/skills/due?limit` | Up to 10 skills due now, most overdue first |
| `GET` | `/v0/skills/{id}` | Get a skill |
| `POST` | `/v0/skills` | Create a skill. `learnNow` makes the first review due immediately |
| `POST` | `/v0/skills/batch` | Create several skills |
| `PUT` | `/v0/skills/{id}` | Update name, description, interval, and priority |
| `DELETE` | `/v0/skills/{id}` | Delete a skill |
| `DELETE` | `/v0/skills` | Delete every skill of the current user |
| `POST` | `/v0/skills/{id}/reviews` | Review with rating `AGAIN`, `HARD`, `GOOD`, or `EASY` |
| `GET` | `/v0/skills/{id}/history` | Page of reviews for a skill |
| `GET` | `/v0/skills/reviews` | Page of the current user's reviews |

### Settings (`/v0/settings`)

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/v0/settings` | Reminder time zone and hour. Defaults are UTC and 09:00 |
| `PUT` | `/v0/settings` | Save reminder time zone and hour |

### Infrastructure

| Path | Description |
|------|-------------|
| `/health/liveness` | Process is up. Does not check the database |
| `/health/readiness` | Ready to serve traffic, including the database |
| `/swagger-ui` | Interactive API documentation (development only) |
| `/prometheus` | Metrics endpoint (not routed on the public ingress) |

## Module Structure

```
skill-repeater-service/
├── pom.xml                       # Parent POM (Micronaut 5.2, Java 25)
├── skill-repeater-api/           # Shared DTOs, enums (Priority, Period), validation
├── skill-repeater-core/          # Domain logic
│   ├── model/                    # Skill, RepeatHistory entities
│   ├── repository/               # Micronaut Data JDBC repositories
│   ├── service/                  # SkillService, RepeatHistoryService
│   ├── worker/                   # ReminderJob (every 15 minutes)
│   └── notification/             # Email notification via external services
└── skill-repeater-web/           # HTTP application
    ├── controller/               # SkillController, SettingsController
    ├── exception/                # Global exception handlers
    └── resources/
        ├── application-dev.yml   # Dev config (local PostgreSQL)
        ├── application-prod.yml  # Prod config (env-based)
        └── db/migration/         # Flyway migrations (V1–V8)
```

## Tech Stack

- **Runtime:** Java 25, Micronaut 4.5 (Netty)
- **Build:** Maven (multi-module), Maven Shade (fat JAR)
- **Database:** PostgreSQL via Micronaut Data JDBC + HikariCP
- **Migrations:** Flyway (7 migration files)
- **Security:** Micronaut Security JWT (Bearer tokens)
- **API docs:** OpenAPI 3.0 + Swagger UI
- **Observability:** Jaeger tracing, Micrometer + Prometheus
- **Logging:** Logback
- **Testing:** JUnit 5, H2 (in-memory)

## Domain Model

A **Skill** has:
- `name`, `description` — what to practice
- `period` (HOURS / DAYS / WEEKS / MONTHS / YEARS) + `number` — repetition interval
- `level` — increments on each repeat
- `priority` (LOW / MEDIUM / HIGH / CRITICAL) — urgency ranking
- `lastRepeated`, `nextRepeated` — timestamps managed by the repeat engine

On **repeat**: `lastRepeated` = now, `nextRepeated` = now + (number × period), `level` += 1.

## Configuration

| Variable | Description | Default (dev) |
|----------|-------------|---------------|
| `DATASOURCE_URL` | JDBC connection string | `jdbc:postgresql://localhost:5433/skill_repeater_db` |
| `DATASOURCE_USERNAME` | Database username | — |
| `DATASOURCE_PASSWORD` | Database password | — |
| `JWT_GENERATOR_SIGNATURE_SECRET` | HMAC secret used to validate access tokens from auth-service | — |
| `AUTH_JWT_ISSUER` | Expected JWT issuer | `https://auth.posadskiy.com` |
| `AUTH_JWT_AUDIENCE` | Expected JWT audience | `api.posadskiy.com` |
| `AUTH_SERVICE_URL` | Auth service base URL for reminder client-credentials | `http://auth-service.local` |
| `AUTH_SERVICE_CLIENT_ID` | Service account client id | `system` |
| `AUTH_SERVICE_CLIENT_SECRET` | Service account client secret | — |
| `OTEL_SERVICE_NAME` | Logical service name for traces | `skill-repeater-service` |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | OTLP gRPC endpoint (Grafana Alloy) | `http://localhost:4317` |
| `OTEL_TRACES_SAMPLER` | OpenTelemetry sampler | `parentbased_traceidratio` |
| `OTEL_TRACES_SAMPLER_ARG` | Sampling ratio | `1.0` |

## Running

### Local (with Docker DB)

```bash
# Start PostgreSQL from root docker-compose
docker compose -f ../docker-compose.dev.yml up -d skill-repeater-db

# Build and run
mvn clean package -DskipTests
java -jar skill-repeater-web/target/skill-repeater-web-*.jar
```

The service starts on port **8210**.

### Swagger UI

Once running, visit [http://localhost:8210/swagger-ui](http://localhost:8210/swagger-ui) for interactive API docs.

## Deployment

The production image is a multi-stage build. The runtime stage is Eclipse Temurin 25 JRE on Alpine, runs as a non-root user, and does not open a debug port. `MICRONAUT_ENVIRONMENTS` defaults to `prod` and can be overridden in the pod.

Kubernetes manifest and deploy scripts are in `deployment/`. Shared cluster config lives in the parent repo's `deployment/` directory.

```bash
./deployment/scripts/build-and-push.sh <version>
./deployment/scripts/deploy.sh <version>
```

### Production

- **API route:** `api.posadskiy.com/skill-repeater/*` (Traefik strips prefix)
- **Database:** Aiven managed PostgreSQL
- **Tracing:** OpenTelemetry OTLP via Grafana Alloy in observability namespace
