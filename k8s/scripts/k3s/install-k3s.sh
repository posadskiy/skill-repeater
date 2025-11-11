#!/bin/bash
# install-k3s.sh - Install k3s on the server
# Usage: ./install-k3s.sh

set -e  # Exit on any error

echo "🚀 Installing k3s..."

# Install k3s
curl -sfL https://get.k3s.io | sh -

# Wait for k3s to be ready
echo "⏳ Waiting for k3s to be ready..."
sleep 10

# Check if k3s is running
if ! sudo systemctl is-active --quiet k3s; then
    echo "❌ k3s installation failed or service is not running"
    exit 1
fi

# Set up kubectl config
echo "📝 Setting up kubectl configuration..."
mkdir -p ~/.kube
sudo cp /etc/rancher/k3s/k3s.yaml ~/.kube/config
sudo chown $USER:$USER ~/.kube/config
chmod 600 ~/.kube/config

# Update kubeconfig to use localhost instead of 127.0.0.1
sed -i 's/127.0.0.1/localhost/g' ~/.kube/config

echo "✅ k3s installed successfully!"
echo ""
echo "📋 k3s status:"
sudo systemctl status k3s --no-pager | head -10
echo ""
echo "🔍 To verify installation, run:"
echo "   kubectl cluster-info"
echo "   kubectl get nodes"

