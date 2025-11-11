#!/bin/bash
# deploy-skill-repeater-service.sh - Deploy skill-repeater service to Kubernetes cluster
# Usage: ./deploy-skill-repeater-service.sh <version>

set -e  # Exit on any error

# Get the directory where this script is located and set K8S_DIR
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
K8S_DIR="$(dirname "$(dirname "$SCRIPT_DIR")")"

# Check if version parameter is provided
if [ $# -eq 0 ]; then
  echo "Usage: $0 <version>"
  echo "Example: $0 v0.1.0"
  exit 1
fi

VERSION=$1

# Configuration
NAMESPACE="${K8S_NAMESPACE:-skill-repeater}"
DOCKERHUB_USERNAME="${DOCKERHUB_USERNAME:-}"

echo "🔧 Deploying skill-repeater-service to Kubernetes cluster..."
echo "🏷️  Version: $VERSION"
echo "📁 Namespace: $NAMESPACE"

# Check required environment variables
echo "🔍 Checking required environment variables..."
REQUIRED_VARS=("SKILL_REPEATER_DATABASE_PASSWORD" "JWT_GENERATOR_SIGNATURE_SECRET" "GITHUB_TOKEN" "GITHUB_USERNAME" "SKILL_REPEATER_DATABASE_NAME" "SKILL_REPEATER_DATABASE_USER" "DOCKERHUB_USERNAME")
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
    echo ""
    echo "💡 Please set these environment variables before running the script:"
    echo "   export SKILL_REPEATER_DATABASE_PASSWORD='your-db-password'"
    echo "   export JWT_GENERATOR_SIGNATURE_SECRET='your-jwt-secret'"
    echo "   export GITHUB_TOKEN='your-github-token'"
    echo "   export GITHUB_USERNAME='your-github-username'"
    echo "   export SKILL_REPEATER_DATABASE_NAME='skillrepeater'"
    echo "   export SKILL_REPEATER_DATABASE_USER='skillrepeater_user'"
    echo "   export DOCKERHUB_USERNAME='your-dockerhub-username'"
    exit 1
fi

echo "✅ All required environment variables are set"

# Check if kubectl is available and cluster is accessible
echo "🔍 Checking kubectl connection..."
if ! kubectl cluster-info > /dev/null 2>&1; then
    echo "❌ kubectl is not configured or cluster is not accessible"
    echo "💡 Please configure kubectl to connect to your Kubernetes cluster"
    exit 1
fi

echo "✅ kubectl is configured and cluster is accessible"

# Check if namespace exists
if ! kubectl get namespace $NAMESPACE > /dev/null 2>&1; then
    echo "📁 Creating namespace..."
    kubectl apply -f "$K8S_DIR/namespace.yaml"
fi

# Deploy ConfigMap and Secrets if they don't exist
echo "⚙️  Deploying ConfigMap and Secrets..."
envsubst < "$K8S_DIR/configmap.yaml" | kubectl apply -f -
envsubst < "$K8S_DIR/secrets.yaml" | kubectl apply -f -

# Deploy service with version substitution
echo "🔧 Deploying skill-repeater-service..."
export IMAGE_VERSION=$VERSION
export DOCKERHUB_USERNAME=$DOCKERHUB_USERNAME
envsubst < "$K8S_DIR/../skill-repeater-service/k8s/skill-repeater-service.yaml" | kubectl apply -f -

# Wait for service to be ready
echo "⏳ Waiting for skill-repeater-service to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/skill-repeater-service -n $NAMESPACE

echo "✅ skill-repeater-service deployment completed successfully!"
echo ""
echo "📋 Status:"
kubectl get pods -n $NAMESPACE -l app=skill-repeater-service
echo ""
echo "🌐 Service:"
kubectl get service skill-repeater-service -n $NAMESPACE
echo ""
echo "🔍 To view logs:"
echo "   kubectl logs -f deployment/skill-repeater-service -n $NAMESPACE" 