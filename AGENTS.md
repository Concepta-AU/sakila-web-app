# Sakila Web App — Agent Guide

## Architecture Overview

This is a full-stack Kotlin Multiplatform project targeting **Server** (JVM) and **Web** (JS/Svelte).

```
sakila-web-app/
├── server/          # Ktor server (Kotlin/JVM) — REST API, auth, DB access
├── shared/          # Kotlin Multiplatform library — shared models/logic (JVM + JS)
├── databaseAccess/  # Database layer — SQL queries via HikariCP + PostgreSQL
├── webApp/          # SvelteKit frontend (TypeScript, Bootstrap 5)
├── docker/          # Dockerfile for the PostgreSQL (Pagila/Sakila) database
└── compose.yaml     # Docker Compose — starts the database on port 15432
```

### Key Technologies

| Layer      | Technology                                      |
|------------|-------------------------------------------------|
| Backend    | Kotlin, Ktor 3.x, Netty, JWT auth              |
| Database   | PostgreSQL (Pagila/Sakila schema), HikariCP     |
| Shared     | Kotlin Multiplatform (commonMain → JVM + JS)    |
| Frontend   | SvelteKit 2, Svelte 5, TypeScript, Vite, Bootstrap 5 |
| Linting    | Prettier (frontend), Kotlin compiler (backend)  |
| Testing    | kotlin-test + JUnit (backend), Testcontainers (DB) |

### Ports

| Service    | Port  |
|------------|-------|
| Database   | 15432 |
| Backend    | 8080  |
| Frontend   | 5173  |

---

## Running the Application Locally

### 1. Start the Database

```shell
docker compose up
```

Starts a PostgreSQL instance with the Sakila/Pagila schema. Credentials: user `postgres`, password `not_secure`.

### 2. Start the Backend

```shell
./gradlew :server:run
```

### 3. Start the Frontend

```shell
# Build the shared Kotlin/JS library first
./gradlew :shared:jsBrowserDevelopmentLibraryDistribution

# Install JS dependencies (first time or after package changes)
npm install

# Start the dev server
npm run dev
```

The application is then accessible at **http://localhost:5173/**.

---

## Validation — How to Verify the Application Works

### Backend

Run all backend tests (includes unit tests and Testcontainers-based DB integration tests):

```shell
./gradlew test
```

Run tests for a specific subproject:

```shell
./gradlew :server:test
./gradlew :databaseAccess:test
./gradlew :shared:test
```

Verify the server compiles and assembles without errors:

```shell
./gradlew assemble
```

### Frontend

Type-check and validate Svelte components:

```shell
cd webApp
npm run check
```

---

## Linting Rules

### Frontend (Prettier)

The frontend uses **Prettier** with the `prettier-plugin-svelte` plugin. All `.svelte`, `.ts`, `.js`, `.json`, `.css`, and `.html` files must be formatted according to the Prettier configuration.

**Check formatting (CI / pre-submit):**

```shell
cd webApp
npm run lint
```

**Auto-fix formatting:**

```shell
cd webApp
npm run format
```

> Always run `npm run lint` before submitting frontend changes. A non-zero exit code means there are formatting violations that must be fixed.

### Backend (Kotlin)

The backend follows standard Kotlin coding conventions enforced by the Kotlin compiler. There is no separate linter configured; keep code idiomatic and consistent with the existing style in each module.

---

## Making Changes — Agent Rules

1. **Database changes** — update the Docker image in `docker/` and ensure existing tests still pass with `./gradlew test`.
2. **Shared model changes** — rebuild the JS distribution (`./gradlew :shared:jsBrowserDevelopmentLibraryDistribution`) before running or testing the frontend.
3. **Backend changes** — run `./gradlew :server:test` to verify nothing is broken.
4. **Frontend changes** — run `npm run check` (type-check) and `npm run lint` (formatting) inside `webApp/`. Fix any reported issues before finishing.
5. **Full validation** — to validate the entire project end-to-end, run:
   ```shell
   ./gradlew test && cd webApp && npm run check && npm run lint
   ```
6. **Never commit with failing tests or lint errors.**
