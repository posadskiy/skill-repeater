#!/bin/bash
# deploy-skill-repeater-front.sh - Deploy skill-repeater frontend to Kubernetes cluster
# Usage: ./deploy-skill-repeater-front.sh <version>

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

echo "🌐 Deploying skill-repeater-front to Kubernetes cluster..."
echo "🏷️  Version: $VERSION"
echo "📁 Namespace: $NAMESPACE"

# Check required environment variables
if [ -z "$DOCKERHUB_USERNAME" ]; then
    echo "❌ Error: DOCKERHUB_USERNAME environment variable is not set"
    echo "💡 Please set it with: export DOCKERHUB_USERNAME=your-dockerhub-username"
    exit 1
fi

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

# Deploy service with version substitution
echo "🌐 Deploying skill-repeater-front..."
export IMAGE_VERSION=$VERSION
export DOCKERHUB_USERNAME=$DOCKERHUB_USERNAME
envsubst < "$K8S_DIR/../skill-repeater-front/k8s/skill-repeater-front.yaml" | kubectl apply -f -

# Wait for service to be ready
echo "⏳ Waiting for skill-repeater-front to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/skill-repeater-front -n $NAMESPACE

echo "✅ skill-repeater-front deployment completed successfully!"
echo ""
echo "📋 Status:"
kubectl get pods -n $NAMESPACE -l app=skill-repeater-front
echo ""
echo "🌐 Service:"
kubectl get service skill-repeater-front -n $NAMESPACE
echo ""
echo "🔍 To view logs:"
echo "   kubectl logs -f deployment/skill-repeater-front -n $NAMESPACE" 