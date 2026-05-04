#!/usr/bin/env bash
set -euo pipefail

# Pull an image from ECR and run app + PostgreSQL on an EC2 host with Docker Compose.
# Usage:
#   ./run_on_ec2.sh [tag]
# Optional env vars:
#   AWS_REGION (default: us-east-2)
#   ECR_REPOSITORY (default: demo/vehicle-registration-backend-micro)
#   APP_PORT (default: 8080)
#   APP_HEALTH_PATH (default: /catalog/makes)
#   APP_HEALTH_RETRIES (default: 120)
#   APP_HEALTH_INTERVAL_SECONDS (default: 2)
#   SPRING_JPA_HIBERNATE_DDL_AUTO (default: update)
#   DB_NAME (default: vehicle_db)
#   DB_USER (default: vehicle_user)
#   DB_PASSWORD (default: vehicle_pass)
#   DEPLOY_DIR (default: $HOME/vehicle-registration-backend-micro)

AWS_REGION="${AWS_REGION:-us-east-2}"
ECR_REPOSITORY="${ECR_REPOSITORY:-demo/vehicle-registration-backend-micro}"
IMAGE_TAG="${1:-${IMAGE_TAG:-latest}}"
APP_PORT="${APP_PORT:-8080}"
APP_HEALTH_PATH="${APP_HEALTH_PATH:-/catalog/makes}"
APP_HEALTH_RETRIES="${APP_HEALTH_RETRIES:-120}"
APP_HEALTH_INTERVAL_SECONDS="${APP_HEALTH_INTERVAL_SECONDS:-2}"
SPRING_JPA_HIBERNATE_DDL_AUTO="${SPRING_JPA_HIBERNATE_DDL_AUTO:-update}"
DB_NAME="${DB_NAME:-vehicle_db}"
DB_USER="${DB_USER:-vehicle_user}"
DB_PASSWORD="${DB_PASSWORD:-vehicle_pass}"
DEPLOY_DIR="${DEPLOY_DIR:-$HOME/vehicle-registration-backend-micro}"
COMPOSE_FILE="${DEPLOY_DIR}/docker-compose.ec2.yml"

for tool in aws docker; do
  if ! command -v "$tool" >/dev/null 2>&1; then
    echo "Error: required tool '$tool' is not installed or not in PATH." >&2
    exit 1
  fi
done

if ! docker info >/dev/null 2>&1; then
  echo "Error: Docker daemon is not reachable or this user lacks Docker permissions." >&2
  echo "Tip: sudo systemctl enable --now docker && sudo usermod -aG docker \$USER && re-login" >&2
  exit 1
fi

if docker compose version >/dev/null 2>&1; then
  COMPOSE_CMD=(docker compose)
elif command -v docker-compose >/dev/null 2>&1; then
  COMPOSE_CMD=(docker-compose)
else
  echo "Error: Docker Compose is not installed." >&2
  exit 1
fi

if ! aws sts get-caller-identity >/dev/null 2>&1; then
  echo "Error: AWS CLI is not authenticated. Configure credentials/profile first." >&2
  echo "Try: aws configure  (or export AWS_PROFILE=your-profile)" >&2
  exit 1
fi

ACCOUNT_ID="$(aws sts get-caller-identity --query Account --output text)"
ECR_REGISTRY="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
IMAGE_URI="${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"

mkdir -p "$DEPLOY_DIR"

cat > "$COMPOSE_FILE" <<EOF
services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER} -d ${DB_NAME}"]
      interval: 10s
      timeout: 5s
      retries: 10

  app:
    image: ${IMAGE_URI}
    depends_on:
      db:
        condition: service_healthy
    ports:
      - "${APP_PORT}:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/${DB_NAME}
      SPRING_DATASOURCE_USERNAME: ${DB_USER}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      SPRING_JPA_HIBERNATE_DDL_AUTO: ${SPRING_JPA_HIBERNATE_DDL_AUTO}

volumes:
  postgres-data:
EOF

echo "Logging Docker into ECR registry: $ECR_REGISTRY"
aws ecr get-login-password --region "$AWS_REGION" | docker login --username AWS --password-stdin "$ECR_REGISTRY"

echo "Pulling app image: $IMAGE_URI"
docker pull "$IMAGE_URI"

echo "Starting stack with ${COMPOSE_CMD[*]}"
"${COMPOSE_CMD[@]}" -f "$COMPOSE_FILE" up -d --remove-orphans

APP_LOCAL_URL="http://localhost:${APP_PORT}${APP_HEALTH_PATH}"
if command -v curl >/dev/null 2>&1; then
  echo "Waiting for app readiness at $APP_LOCAL_URL"
  APP_HEALTHY=0
  for _ in $(seq 1 "$APP_HEALTH_RETRIES"); do
    if curl -fsS "$APP_LOCAL_URL" >/dev/null 2>&1; then
      echo "App is responding."
      APP_HEALTHY=1
      break
    fi
    sleep "$APP_HEALTH_INTERVAL_SECONDS"
  done

  if [[ "$APP_HEALTHY" -ne 1 ]]; then
    echo "Error: app did not become healthy at $APP_LOCAL_URL within timeout." >&2
    echo "Compose status:" >&2
    "${COMPOSE_CMD[@]}" -f "$COMPOSE_FILE" ps || true
    echo "Recent app logs:" >&2
    "${COMPOSE_CMD[@]}" -f "$COMPOSE_FILE" logs --tail=200 app || true
    echo "Recent db logs:" >&2
    "${COMPOSE_CMD[@]}" -f "$COMPOSE_FILE" logs --tail=200 db || true
    exit 1
  fi
else
  echo "Error: curl is required for readiness checks but is not installed." >&2
  exit 1
fi

EC2_PUBLIC_IP=""
if command -v curl >/dev/null 2>&1; then
  IMDS_TOKEN="$(curl -sS --connect-timeout 1 -m 1 -X PUT http://169.254.169.254/latest/api/token -H 'X-aws-ec2-metadata-token-ttl-seconds: 60' || true)"
  if [[ -n "$IMDS_TOKEN" ]]; then
    EC2_PUBLIC_IP="$(curl -sS --connect-timeout 1 -m 1 -H "X-aws-ec2-metadata-token: $IMDS_TOKEN" http://169.254.169.254/latest/meta-data/public-ipv4 || true)"
  fi
fi

echo
echo "Deployment complete."
echo "Compose file: $COMPOSE_FILE"
if [[ -n "$EC2_PUBLIC_IP" ]]; then
  echo "App URL: http://${EC2_PUBLIC_IP}:${APP_PORT}${APP_HEALTH_PATH}"
else
  echo "App URL (replace with EC2 public IP or DNS): http://<EC2_PUBLIC_IP>:${APP_PORT}${APP_HEALTH_PATH}"
fi
echo
echo "Useful checks:"
echo "  ${COMPOSE_CMD[*]} -f $COMPOSE_FILE ps"
echo "  ${COMPOSE_CMD[*]} -f $COMPOSE_FILE logs -f app"

