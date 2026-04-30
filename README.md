# vehicle-registration-backend-micro
Microservice version of same

## IntelliJ note after refactors

After moving or renaming packages/classes, IntelliJ may show false dependency errors (for example unresolved `JpaRepository` or repository `save(...)` methods) even when Gradle builds successfully.

If that happens:

1. In the Gradle tool window, click the refresh icon (two arrows) to load Gradle changes.
2. If errors remain, run **File -> Invalidate Caches... -> Invalidate and Restart**.

Avoid adding libraries directly from IntelliJ quick-fixes for this project; keep dependencies managed in `build.gradle`.

## Docker local setup (app + PostgreSQL)

This project includes:

- `Dockerfile` for building and running the Spring Boot app
- `docker-compose.yml` for a local multi-container stack (`app` + `db`)

Docker uses the project Gradle Wrapper during image build (from `Dockerfile`):

- `./gradlew --no-daemon clean bootJar`

Build only the app image:

```bash
docker compose build app
```

Rebuild and replace only the `app` service:

```bash
docker compose up -d --build --force-recreate --no-deps app
```

What it does:

- `-d` runs the container in detached mode (in the background)
- `--build` rebuilds the `app` image before starting the container
- `--force-recreate` removes and recreates the `app` container even if Docker thinks the config has not changed
- `--no-deps` starts only the `app` service and does not restart dependent services like `db`
- `app` targets just the application service defined in `docker-compose.yml`

Run locally:

```bash
docker compose up --build
```

Stop and remove containers:

```bash
docker compose down
```

Stop and also remove database volume:

```bash
docker compose down -v
```

PostgreSQL host access for external tools (IntelliJ DB, pgAdmin):

- Host: `localhost`
- Port: `5433`
- Database: `vehicle_db`
- Username: `vehicle_user`
