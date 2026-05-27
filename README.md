# ai-test

A small full-stack sandbox: a Spring Boot backend with a MongoDB-backed
Todo CRUD API and a React + TypeScript single-page UI.

## Stack

| Layer    | Tech                                                              |
| -------- | ----------------------------------------------------------------- |
| Backend  | Java 21 (compile target), Spring Boot 3.4, Spring Data MongoDB    |
| Database | MongoDB 7 (via Docker Compose; embedded Flapdoodle for tests)     |
| Frontend | React 19, TypeScript 5, Vite 6                                    |
| Build    | Maven (wrapper), npm                                              |
| Tests    | JUnit 5 + Mockito + AssertJ, Vitest + Testing Library             |

## Repository layout

```
ai-test/
├── docker-compose.yml      # Local MongoDB
├── docs/
│   └── requests.http       # Sample REST calls for IntelliJ HTTP client
├── frontend/               # React + TS + Vite SPA
│   └── src/
│       ├── api/            # Typed fetch wrappers
│       ├── components/     # TodoForm, TodoList, TodoItem
│       ├── hooks/          # useTodos
│       └── types/          # Shared API types
└── java/                   # Spring Boot backend (Maven)
    ├── CLAUDE.md           # Agent guidelines for this module
    ├── pom.xml
    └── src/main/java/com/example/aitest/
        ├── config/         # Clock + CORS config
        └── todo/
            ├── api/        # Controller, DTOs, exception handler
            ├── domain/     # Todo (@Document)
            ├── repository/ # Spring Data MongoRepository
            └── service/    # TodoService, TodoNotFoundException
```

## Prerequisites

- **JDK 21+** on `JAVA_HOME` / `PATH` (newer JDKs work; the Maven Wrapper
  bootstraps Maven 3.9.9 itself).
- **Node.js 20+** and npm.
- **Docker** with Compose (only needed to run the app against a real
  MongoDB; tests use embedded Mongo).

## Run it locally

Three terminals, in order:

```powershell
# 1) MongoDB
docker compose up -d mongo

# 2) Backend (http://localhost:8080)
cd java
.\mvnw.cmd spring-boot:run

# 3) Frontend (http://localhost:5173)
cd frontend
npm install        # only first time
npm run dev
```

Open <http://localhost:5173>. The Vite dev server proxies `/api/**` to
the backend, so there is no CORS dance during development.

### Configuration knobs

Both values default sensibly; override via environment variables when
needed:

| Variable                    | Default                                  |
| --------------------------- | ---------------------------------------- |
| `MONGODB_URI`               | `mongodb://localhost:27017/aitest`       |
| `APP_CORS_ALLOWED_ORIGINS`  | `http://localhost:5173`                  |

## Tests

```powershell
# Backend (21 tests: service unit, controller WebMvc, repo embedded-mongo)
cd java
.\mvnw.cmd test

# Frontend (Vitest + Testing Library)
cd frontend
npm test
```

First backend test run downloads a MongoDB binary (~70 MB) used by the
embedded Mongo for integration tests; subsequent runs are fast.

## REST API

Base path: `/api`

| Method   | Path              | Body                                | Response                |
| -------- | ----------------- | ----------------------------------- | ----------------------- |
| `GET`    | `/hello`          | —                                   | `{"message": "..."}`    |
| `GET`    | `/todos`          | —                                   | `Todo[]` (newest first) |
| `GET`    | `/todos/{id}`     | —                                   | `Todo` (404 if missing) |
| `POST`   | `/todos`          | `{ "title": "..." }`                | `201` + `Todo` + `Location` |
| `PUT`    | `/todos/{id}`     | `{ "title": "...", "completed": true }` | `Todo` (404 if missing) |
| `DELETE` | `/todos/{id}`     | —                                   | `204` (404 if missing)  |

`Todo` shape:

```json
{
  "id": "string",
  "title": "string",
  "completed": false,
  "createdAt": "2026-05-27T10:00:00Z",
  "updatedAt": "2026-05-27T10:00:00Z"
}
```

Validation errors return `400` with a `fieldErrors` array. Ready-to-run
examples live in [`docs/requests.http`](docs/requests.http).

## Build for production

```powershell
# Backend → java/target/ai-test-0.0.1-SNAPSHOT.jar
cd java; .\mvnw.cmd package

# Frontend → frontend/dist/
cd frontend; npm run build
```

## Agent guidelines

If you (or another AI agent) work in `java/`, read
[`java/CLAUDE.md`](java/CLAUDE.md) first — it captures the architectural
conventions, commit style, and test expectations used throughout the
repository.
