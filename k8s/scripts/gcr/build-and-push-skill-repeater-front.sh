#!/bin/bash
set -e

# build-and-push-skill-repeater-front.sh - Build and push skill-repeater frontend to GCR
# Usage: ./build-and-push-skill-repeater-front.sh <version>

if [ $# -eq 0 ]; then
  echo "Usage: $0 <version>"
  echo "Example: $0 v0.1.0"
  exit 1
fi

VERSION=$1
PROJECT_ID=$(gcloud config get-value project)
REGISTRY="gcr.io/$PROJECT_ID"
TAG_DATE=$(date +%Y%m%d%H%M%S)

# Skill Repeater Front (Frontend)
echo "🌐 Building and pushing Skill Repeater Front to GCR..."
docker buildx build --platform linux/amd64 -f skill-repeater-front/Dockerfile.prod -t $REGISTRY/skill-repeater-front:$VERSION -t $REGISTRY/skill-repeater-front:$TAG_DATE skill-repeater-front/ --push

echo "✅ Skill Repeater Front built and pushed to GCR successfully!" 