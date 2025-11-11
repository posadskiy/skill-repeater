#!/bin/bash
set -e

# build-and-push-all.sh - Build and push all skill-repeater services to Docker Hub
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
DOCKERHUB_USERNAME=${DOCKERHUB_USERNAME:-"your-dockerhub-username"}
TAG_DATE=$(date +%Y%m%d%H%M%S)

if [ "$DOCKERHUB_USERNAME" = "your-dockerhub-username" ]; then
  echo "Please set your Docker Hub username in the DOCKERHUB_USERNAME environment variable."
  exit 1
fi

# Skill Repeater Service (Backend)
echo "🔧 Building and pushing Skill Repeater Service to Docker Hub..."
docker buildx build --platform linux/arm64 --build-arg GITHUB_USERNAME=$GITHUB_USERNAME --build-arg GITHUB_TOKEN=$GITHUB_TOKEN -f skill-repeater-service/Dockerfile.prod -t $DOCKERHUB_USERNAME/skill-repeater-service:$VERSION -t $DOCKERHUB_USERNAME/skill-repeater-service:$TAG_DATE skill-repeater-service/ --push

# Skill Repeater Front (Frontend)
echo "🌐 Building and pushing Skill Repeater Front to Docker Hub..."
docker buildx build --platform linux/arm64 \
  --build-arg VITE_API_URL=https://api.posadskiy.com/skill-repeater \
  --build-arg VITE_AUTH_URL=https://api.posadskiy.com/auth \
  --build-arg VITE_USER_URL=https://api.posadskiy.com/user \
  -f skill-repeater-front/Dockerfile.prod \
  -t $DOCKERHUB_USERNAME/skill-repeater-front:$VERSION \
  -t $DOCKERHUB_USERNAME/skill-repeater-front:$TAG_DATE \
  skill-repeater-front/ --push

echo "✅ All skill-repeater images built and pushed to Docker Hub successfully!" 