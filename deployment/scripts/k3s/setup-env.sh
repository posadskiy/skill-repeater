#!/bin/bash
# setup-env.sh - Setup environment variables for k3s deployment
# Usage: ./setup-env.sh

set -e  # Exit on any error

echo "🔧 Setting up environment variables for k3s deployment..."

# Check if .env file exists
if [ -f ".env" ]; then
    echo "📝 Loading environment variables from .env file..."
    set -a
    source .env
    set +a
    echo "✅ Environment variables loaded from .env file"
else
    echo "⚠️  No .env file found. Please set environment variables manually:"
    echo ""
    echo "Required environment variables:"
    echo "  - SKILL_REPEATER_DATABASE_PASSWORD"
    echo "  - JWT_GENERATOR_SIGNATURE_SECRET"
    echo "  - AUTH_SERVICE_CLIENT_SECRET"
    echo "  - GITHUB_USERNAME"
    echo "  - SKILL_REPEATER_DATABASE_NAME"
    echo "  - SKILL_REPEATER_DATABASE_USER"
    echo "  - DOCKERHUB_USERNAME"
    echo "  - DOCKERHUB_TOKEN"
    echo "  - K8S_NAMESPACE (optional, defaults to 'skill-repeater')"
    echo ""
    echo "You can create a .env file with these variables or export them manually."
fi

# Verify required variables
REQUIRED_VARS=("SKILL_REPEATER_DATABASE_PASSWORD" "JWT_GENERATOR_SIGNATURE_SECRET" "AUTH_SERVICE_CLIENT_SECRET" "GITHUB_USERNAME" "SKILL_REPEATER_DATABASE_NAME" "SKILL_REPEATER_DATABASE_USER" "DOCKERHUB_USERNAME" "DOCKERHUB_TOKEN")
MISSING_VARS=()

for var in "${REQUIRED_VARS[@]}"; do
    if [ -z "${!var}" ]; then
        MISSING_VARS+=("$var")
    fi
done

if [ ${#MISSING_VARS[@]} -ne 0 ]; then
    echo "❌ Error: Missing required environment variables:"
    for var in "${MISSING_VARS[@]}"; do
        echo "   - $var"
    done
    exit 1
fi

echo "✅ All required environment variables are set"
echo ""
echo "📋 Environment Summary:"
echo "  - Namespace: ${K8S_NAMESPACE:-skill-repeater}"
echo "  - Docker Hub Username: $DOCKERHUB_USERNAME"
echo "  - Database Name: $SKILL_REPEATER_DATABASE_NAME"
echo "  - Database User: $SKILL_REPEATER_DATABASE_USER"
echo "  - GitHub Username: $GITHUB_USERNAME"

