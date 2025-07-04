#!/bin/bash
set -e

# build-and-push-all.sh - Build and push all skill-repeater services to GCR
# Usage: ./build-and-push-all.sh <version>

if [ $# -eq 0 ]; then
  echo "Usage: $0 <version>"
  echo "Example: $0 v0.1.0"
  exit 1
fi

# Check for required environment variables for backend build
if [ -z "$GITHUB_USERNAME" ]; then
  echo "❌ Error: GITHUB_USERNAME environment variable is not set"
  echo "Please set it with: export GITHUB_USERNAME=your_github_username"
  exit 1
fi

if [ -z "$GITHUB_TOKEN" ]; then
  echo "❌ Error: GITHUB_TOKEN environment variable is not set"
  echo "Please set it with: export GITHUB_TOKEN=your_github_token"
  exit 1
fi

VERSION=$1
PROJECT_ID=$(gcloud config get-value project)
REGISTRY="gcr.io/$PROJECT_ID"
TAG_DATE=$(date +%Y%m%d%H%M%S)

# Skill Repeater Service (Backend)
echo "🔧 Building and pushing Skill Repeater Service to GCR..."
docker buildx build --platform linux/amd64 --build-arg GITHUB_USERNAME=$GITHUB_USERNAME --build-arg GITHUB_TOKEN=$GITHUB_TOKEN -f skill-repeater-service/Dockerfile.prod -t $REGISTRY/skill-repeater-service:$VERSION -t $REGISTRY/skill-repeater-service:$TAG_DATE skill-repeater-service/ --push

# Skill Repeater Front (Frontend)
echo "🌐 Building and pushing Skill Repeater Front to GCR..."
docker buildx build --platform linux/amd64 -f skill-repeater-front/Dockerfile.prod -t $REGISTRY/skill-repeater-front:$VERSION -t $REGISTRY/skill-repeater-front:$TAG_DATE skill-repeater-front/ --push

echo "✅ All skill-repeater images built and pushed to GCR successfully!" 