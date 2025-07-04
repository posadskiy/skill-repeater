#!/bin/bash
# deploy-all.sh - Deploy all skill-repeater services to GKE autopilot cluster
# Usage: ./deploy-all.sh <version>

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

echo "🚀 Deploying skill-repeater services to GKE autopilot cluster..."
echo "📦 Project ID: $PROJECT_ID"
echo "🏗️  Cluster: $CLUSTER_NAME"
echo "🏷️  Version: $VERSION"
echo "📁 Namespace: $NAMESPACE"

# Check required environment variables
echo "🔍 Checking required environment variables..."
REQUIRED_VARS=("SKILL_REPEATER_DATABASE_PASSWORD" "JWT_GENERATOR_SIGNATURE_SECRET" "GITHUB_TOKEN" "GITHUB_USERNAME" "SKILL_REPEATER_DATABASE_NAME" "SKILL_REPEATER_DATABASE_USER")
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
    exit 1
fi

echo "✅ All required environment variables are set"

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

# Deploy namespace
echo "📁 Creating namespace..."
kubectl apply -f "$K8S_DIR/namespace.yaml"

# Deploy ConfigMap and Secrets
echo "⚙️  Deploying ConfigMap and Secrets..."
envsubst < "$K8S_DIR/configmap.yaml" | kubectl apply -f -
envsubst < "$K8S_DIR/secrets.yaml" | kubectl apply -f -

# Deploy database
echo "🗄️  Deploying PostgreSQL database..."
envsubst < "$K8S_DIR/database/postgresql.yaml" | kubectl apply -f -

# Wait for database to be ready
echo "⏳ Waiting for database to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/skill-repeater-db -n $NAMESPACE

# Deploy all services with version substitution
echo "🚀 Deploying all services..."
export IMAGE_VERSION=$VERSION

echo "🔧 Deploying skill-repeater-service..."
envsubst < "$K8S_DIR/services/skill-repeater-service.yaml" | kubectl apply -f -

echo "🌐 Deploying skill-repeater-front..."
envsubst < "$K8S_DIR/services/skill-repeater-front.yaml" | kubectl apply -f -

# Wait for all services to be ready
echo "⏳ Waiting for all services to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/skill-repeater-service -n $NAMESPACE
kubectl wait --for=condition=available --timeout=300s deployment/skill-repeater-front -n $NAMESPACE

echo "✅ All services deployment completed successfully!"
echo ""
echo "📋 Status:"
kubectl get pods -n $NAMESPACE
echo ""
echo "🌐 Services:"
kubectl get services -n $NAMESPACE
echo ""
echo "💡 To access the services:"
echo "   kubectl get services -n $NAMESPACE"
echo ""
echo "🔍 To view logs:"
echo "   kubectl logs -f deployment/skill-repeater-service -n $NAMESPACE"
echo "   kubectl logs -f deployment/skill-repeater-front -n $NAMESPACE" 