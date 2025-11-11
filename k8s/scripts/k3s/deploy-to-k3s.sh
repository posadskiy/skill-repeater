#!/bin/bash
# deploy-to-k3s.sh - Deploy skill-repeater services to k3s cluster
# Usage: ./deploy-to-k3s.sh [version]
#
# SAFETY: This script only ADDS/UPDATES skill-repeater resources in the 'skill-repeater' namespace.
# It does NOT remove or modify any existing resources outside of skill-repeater namespace.
# All operations use 'kubectl apply' which is idempotent and safe to run multiple times.
# Resources are namespaced to avoid conflicts with other services.

set -e  # Exit on any error

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$(dirname "$(dirname "$SCRIPT_DIR")")")"
K8S_DIR="$PROJECT_ROOT/k8s"

# Check if version parameter is provided
if [ $# -eq 0 ]; then
  echo "Usage: $0 <service-version> [frontend-version]"
  echo "Example: $0 v0.2.0 v0.2.6"
  echo "If frontend-version is not provided, service-version will be used for both"
  exit 1
fi

SERVICE_VERSION=$1
FRONTEND_VERSION=${2:-$SERVICE_VERSION}

# Configuration
NAMESPACE="${K8S_NAMESPACE:-skill-repeater}"
DOCKERHUB_USERNAME="${DOCKERHUB_USERNAME:-}"

echo "🚀 Deploying skill-repeater services to k3s cluster..."
echo "🏷️  Service Version: $SERVICE_VERSION"
echo "🏷️  Frontend Version: $FRONTEND_VERSION"
echo "📁 Namespace: $NAMESPACE"

# Check required environment variables
echo "🔍 Checking required environment variables..."
REQUIRED_VARS=("SKILL_REPEATER_DATABASE_PASSWORD" "JWT_GENERATOR_SIGNATURE_SECRET" "GITHUB_TOKEN" "GITHUB_USERNAME" "SKILL_REPEATER_DATABASE_NAME" "SKILL_REPEATER_DATABASE_USER" "DOCKERHUB_USERNAME" "DOCKERHUB_TOKEN")
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
    echo "   export DOCKERHUB_TOKEN='your-dockerhub-token'"
    exit 1
fi

echo "✅ All required environment variables are set"

# Check if kubectl is available and cluster is accessible
echo "🔍 Checking kubectl connection..."
if ! kubectl cluster-info > /dev/null 2>&1; then
    echo "❌ kubectl is not configured or cluster is not accessible"
    echo "💡 Please configure kubectl to connect to your k3s cluster"
    exit 1
fi

echo "✅ kubectl is configured and cluster is accessible"

# Create namespace if it doesn't exist
if ! kubectl get namespace $NAMESPACE > /dev/null 2>&1; then
    echo "📁 Creating namespace: $NAMESPACE"
    kubectl apply -f "$K8S_DIR/namespace.yaml"
fi

# Create Docker Hub registry secret (only if not already exists)
echo "🔐 Creating Docker Hub registry secret..."
if ! kubectl get secret dockerhub-registry-secret -n $NAMESPACE > /dev/null 2>&1; then
    export DOCKERHUB_USERNAME=$DOCKERHUB_USERNAME
    export DOCKERHUB_TOKEN=$DOCKERHUB_TOKEN
    "$PROJECT_ROOT/k8s/scripts/dockerhub/create-registry-secret.sh" $NAMESPACE
else
    echo "✅ Docker Hub registry secret already exists in namespace $NAMESPACE, skipping..."
fi

# Deploy ConfigMap and Secrets (kubectl apply is idempotent - will update if exists)
echo "⚙️  Deploying ConfigMap and Secrets..."
echo "📝 Note: This will update existing ConfigMap/Secrets if they exist, but won't remove other resources"
envsubst < "$K8S_DIR/configmap.yaml" | kubectl apply -f -
envsubst < "$K8S_DIR/secrets.yaml" | kubectl apply -f -

# Deploy Traefik ingress configuration (only if not already exists)
echo "🌐 Deploying Traefik ingress configuration..."
# Check if TLSOption exists, if not create it
if ! kubectl get tlsoption default -n $NAMESPACE > /dev/null 2>&1; then
    echo "📝 Creating TLSOption 'default' in namespace $NAMESPACE..."
    kubectl apply -f "$K8S_DIR/ingress/traefik-letsencrypt.yaml"
else
    echo "✅ TLSOption 'default' already exists in namespace $NAMESPACE, skipping..."
fi
# Apply IngressRoute (will update if exists, create if not)
kubectl apply -f "$K8S_DIR/ingress/traefik-ingressroute.yaml"

# Deploy services with version substitution
echo "🚀 Deploying services..."
export DOCKERHUB_USERNAME=$DOCKERHUB_USERNAME

echo "🔧 Deploying skill-repeater-service..."
export IMAGE_VERSION=$SERVICE_VERSION
envsubst < "$PROJECT_ROOT/skill-repeater-service/k8s/skill-repeater-service.yaml" | kubectl apply -f -

echo "🌐 Deploying skill-repeater-front..."
export IMAGE_VERSION=$FRONTEND_VERSION
envsubst < "$PROJECT_ROOT/skill-repeater-front/k8s/skill-repeater-front.yaml" | kubectl apply -f -

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/skill-repeater-service -n $NAMESPACE || true
kubectl wait --for=condition=available --timeout=300s deployment/skill-repeater-front -n $NAMESPACE || true

echo "✅ All services deployment completed successfully!"
echo ""
echo "📋 Status:"
kubectl get pods -n $NAMESPACE
echo ""
echo "🌐 Services:"
kubectl get services -n $NAMESPACE
echo ""
echo "🔗 Ingress:"
kubectl get ingressroute skill-repeater-ingress -n $NAMESPACE 2>/dev/null || echo "No IngressRoute found"
echo ""
echo "💡 To access the services:"
echo "   - API: https://api.posadskiy.com/skill-repeater"
echo "   - Frontend: https://repeaty.posadskiy.com"
echo ""
echo "🔍 To view logs:"
echo "   kubectl logs -f deployment/skill-repeater-service -n $NAMESPACE"
echo "   kubectl logs -f deployment/skill-repeater-front -n $NAMESPACE"

