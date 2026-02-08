#!/bin/bash
# deploy-to-k3s.sh - Prepare k3s cluster for skill-repeater (cluster only, no service deployment).
# Creates namespace, Docker Hub secret, ConfigMap, Secrets, Traefik ingress.
# Deploy each service from that service's folder: ./deployment/scripts/deploy.sh <version>
#
# Required env: see setup-env.sh (SKILL_REPEATER_*, JWT_*, GITHUB_*, DOCKERHUB_*)

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOYMENT_DIR="$(dirname "$(dirname "$SCRIPT_DIR")")"
PROJECT_ROOT="$(dirname "$DEPLOYMENT_DIR")"

NAMESPACE="${K8S_NAMESPACE:-skill-repeater}"

echo "🚀 Preparing k3s cluster for skill-repeater (namespace, config, ingress only)"
echo "📁 Namespace: $NAMESPACE"
echo ""

echo "🔍 Checking required environment variables..."
"$SCRIPT_DIR/setup-env.sh" || { echo "❌ Environment validation failed"; exit 1; }

if ! kubectl cluster-info &>/dev/null; then
  echo "❌ kubectl is not configured or cluster is not accessible"
  exit 1
fi
echo "✅ Cluster accessible"
echo ""

echo "📁 Creating namespace..."
kubectl create namespace "$NAMESPACE" --dry-run=client -o yaml | kubectl apply -f -

echo ""
echo "🔐 Creating Docker Hub registry secret..."
export DOCKERHUB_USERNAME="${DOCKERHUB_USERNAME}"
"$DEPLOYMENT_DIR/scripts/dockerhub/create-registry-secret.sh" "$NAMESPACE"

echo ""
echo "⚙️  Deploying ConfigMap..."
kubectl apply -f "$DEPLOYMENT_DIR/configmap.yaml"

echo ""
echo "🔒 Deploying Secrets..."
if command -v envsubst &>/dev/null; then
  envsubst < "$DEPLOYMENT_DIR/secrets.yaml" | kubectl apply -f -
else
  echo "⚠️  envsubst not found. Run: envsubst < $DEPLOYMENT_DIR/secrets.yaml | kubectl apply -f -"
  exit 1
fi

echo ""
echo "🌐 Deploying Traefik ingress..."
if ! kubectl get tlsoption default -n "$NAMESPACE" &>/dev/null; then
  kubectl apply -f "$DEPLOYMENT_DIR/ingress/traefik-letsencrypt.yaml"
fi
kubectl apply -f "$DEPLOYMENT_DIR/ingress/traefik-ingressroute.yaml"

echo ""
echo "✅ Cluster prepared. Deploy services from each service folder:"
echo "   cd $PROJECT_ROOT/skill-repeater-service && ./deployment/scripts/deploy.sh <version>"
echo "   cd $PROJECT_ROOT/skill-repeater-front && ./deployment/scripts/deploy.sh <version>"
