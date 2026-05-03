#!/usr/bin/env bash
set -euo pipefail

# Build, tag, and push this project image to Amazon ECR.
# Usage:
#   ./deploy_to_ecr.sh [tag]
# Optional env vars:
#   AWS_REGION (default: us-east-2)
#   ECR_REPOSITORY (default: demo/vehicle-registration-backend-micro)
#   IMAGE_LOCAL_NAME (default: demo/vehicle-registration-backend-micro)

AWS_REGION="${AWS_REGION:-us-east-2}"
ECR_REPOSITORY="${ECR_REPOSITORY:-demo/vehicle-registration-backend-micro}"
IMAGE_LOCAL_NAME="${IMAGE_LOCAL_NAME:-demo/vehicle-registration-backend-micro}"
IMAGE_TAG="${1:-latest}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

for tool in aws docker; do
  if ! command -v "$tool" >/dev/null 2>&1; then
    echo "Error: required tool '$tool' is not installed or not in PATH." >&2
    exit 1
  fi
done

if ! aws sts get-caller-identity >/dev/null 2>&1; then
  echo "Error: AWS CLI is not authenticated. Configure credentials/profile first." >&2
  echo "Try: aws configure  (or export AWS_PROFILE=your-profile)" >&2
  exit 1
fi

ACCOUNT_ID="$(aws sts get-caller-identity --query Account --output text)"
ECR_REGISTRY="${ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
FULL_IMAGE_URI="${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"

if ! aws ecr describe-repositories --repository-names "$ECR_REPOSITORY" --region "$AWS_REGION" >/dev/null 2>&1; then
  echo "ECR repository '$ECR_REPOSITORY' not found in $AWS_REGION. Creating it..."
  aws ecr create-repository --repository-name "$ECR_REPOSITORY" --region "$AWS_REGION" >/dev/null
fi

echo "Logging Docker into ECR registry: $ECR_REGISTRY"
aws ecr get-login-password --region "$AWS_REGION" | docker login --username AWS --password-stdin "$ECR_REGISTRY"

echo "Building Docker image: ${IMAGE_LOCAL_NAME}:${IMAGE_TAG}"
docker build -t "${IMAGE_LOCAL_NAME}:${IMAGE_TAG}" "$SCRIPT_DIR"

echo "Tagging image: $FULL_IMAGE_URI"
docker tag "${IMAGE_LOCAL_NAME}:${IMAGE_TAG}" "$FULL_IMAGE_URI"

echo "Pushing image to ECR: $FULL_IMAGE_URI"
docker push "$FULL_IMAGE_URI"

echo
echo "Done. Pushed image: $FULL_IMAGE_URI"

