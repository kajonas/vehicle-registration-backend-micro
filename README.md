# vehicle-registration-backend-micro
Microservice version of same

## IntelliJ note after refactors

After moving or renaming packages/classes, IntelliJ may show false dependency errors (for example unresolved `JpaRepository` or repository `save(...)` methods) even when Gradle builds successfully.

If that happens:

1. In the Gradle tool window, click the refresh icon (two arrows) to load Gradle changes.
2. If errors remain, run **File -> Invalidate Caches... -> Invalidate and Restart**.

Avoid adding libraries directly from IntelliJ quick-fixes for this project; keep dependencies managed in `build.gradle`.

## GitHub Actions CI/CD Pipeline

This project has a complete GitHub Actions CI/CD pipeline that automatically builds, tests, and deploys the application to AWS on every commit or merge to `main`.

### How it is triggered

- Pushing commits directly to `main`
- Merging a pull request into `main`
- Manually via **Actions → CI/CD → Run workflow** on GitHub

### Pipeline phases

| Phase | Name | What it does |
|-------|------|--------------|
| 1 | **Build and Test** | Compiles the project with Gradle, runs all unit tests, and uploads test reports as build artifacts. The pipeline stops here if any test fails. |
| 2 | **Build and Push Docker Image to AWS ECR** | Builds the Docker image and pushes it to the private Amazon Elastic Container Registry (ECR) repository `demo/vehicle-registration-backend-micro`. The image is tagged with both the Git commit SHA and `latest`. |
| 3 | **Deploy to EC2 and Run** | Copies the deploy script to the EC2 instance over SSH, then runs it remotely. The script pulls the new image from ECR, starts the app and PostgreSQL database with Docker Compose, and waits for the health check at `/catalog/makes` to pass before marking the deploy successful. |

### Relevant files

| File | Purpose |
|------|---------|
| `.github/workflows/ci-cd.yml` | Defines all three pipeline jobs and their steps |
| `run_on_ec2.sh` | Deploy script copied to EC2 and executed during phase 3 — handles ECR login, image pull, Compose stack start, and health check |
| `deploy_to_ecr.sh` | Standalone local script for manually building and pushing to ECR outside of CI |
| `Dockerfile` | Defines how the Spring Boot app is packaged into a Docker image |
| `docker-compose.yml` | Local development stack (app + PostgreSQL) |

### Role of AWS ECR and EC2

**Amazon ECR (Elastic Container Registry)** acts as the private Docker image registry. After each successful build, the pipeline pushes the image there. The EC2 instance then pulls from ECR at deploy time, keeping the deployment fully self-contained within AWS.

**Amazon EC2 (Elastic Compute Cloud)** hosts the running application. A `t2.micro` instance (Amazon Linux 2023) runs both the Spring Boot app container and a PostgreSQL container via Docker Compose. The EC2 instance uses an attached IAM role to authenticate with ECR — no static credentials are required.

Both ECR and EC2 are used **within the AWS Free Tier** at no cost for this project.

---

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
