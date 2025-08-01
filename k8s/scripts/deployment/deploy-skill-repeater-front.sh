#!/bin/bash
# deploy-skill-repeater-front.sh - Deploy skill-repeater frontend to GKE autopilot cluster
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
PROJECT_ID=$(gcloud config get-value project)
CLUSTER_NAME="autopilot-cluster-1"
NAMESPACE="skill-repeater"

echo "🌐 Deploying skill-repeater-front to GKE autopilot cluster..."
echo "📦 Project ID: $PROJECT_ID"
echo "🏗️  Cluster: $CLUSTER_NAME"
echo "🏷️  Version: $VERSION"
echo "📁 Namespace: $NAMESPACE"

# Check if cluster exists and get credentials
echo "🔍 Checking cluster access..."
if ! gcloud container clusters describe $CLUSTER_NAME --zone=europe-central2 > /dev/null 2>&1; then
    echo "❌ Cluster $CLUSTER_NAME not found in europe-central2"
    echo "💡 Please check the cluster name and zone, or run:"
    echo "   gcloud container clusters list"
    exit 1
fi

# Get cluster credentials
echo "🔐 Getting cluster credentials..."
gcloud container clusters get-credentials $CLUSTER_NAME --zone=europe-central2

# Check if namespace exists
if ! kubectl get namespace $NAMESPACE > /dev/null 2>&1; then
    echo "📁 Creating namespace..."
    kubectl apply -f "$K8S_DIR/namespace.yaml"
fi

# Deploy service with version substitution
echo "🌐 Deploying skill-repeater-front..."
export IMAGE_VERSION=$VERSION
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