#!/bin/bash
# create-registry-secret.sh - Create Kubernetes secret for Docker Hub registry
# Usage: ./create-registry-secret.sh [namespace]

set -e  # Exit on any error

NAMESPACE=${1:-"skill-repeater"}

echo "🔐 Creating Docker Hub registry secret in namespace: $NAMESPACE"

# Check required environment variables
if [ -z "$DOCKERHUB_USERNAME" ]; then
    echo "❌ Error: DOCKERHUB_USERNAME environment variable is not set"
    echo "💡 Please set it with: export DOCKERHUB_USERNAME=your-dockerhub-username"
    exit 1
fi

if [ -z "$DOCKERHUB_TOKEN" ]; then
    echo "❌ Error: DOCKERHUB_TOKEN environment variable is not set"
    echo "💡 Please set it with: export DOCKERHUB_TOKEN=your-dockerhub-token"
    exit 1
fi

# Check if namespace exists
if ! kubectl get namespace $NAMESPACE > /dev/null 2>&1; then
    echo "📁 Creating namespace: $NAMESPACE"
    kubectl create namespace $NAMESPACE
fi

# Create or update the registry secret
echo "🔑 Creating registry secret..."
kubectl create secret docker-registry dockerhub-registry-secret \
    --docker-server=https://index.docker.io/v1/ \
    --docker-username=$DOCKERHUB_USERNAME \
    --docker-password=$DOCKERHUB_TOKEN \
    --docker-email=$DOCKERHUB_USERNAME@example.com \
    --namespace=$NAMESPACE \
    --dry-run=client -o yaml | kubectl apply -f -

echo "✅ Docker Hub registry secret created successfully in namespace: $NAMESPACE"

