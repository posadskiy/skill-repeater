# Deployment

Shared cluster config and scripts for skill-repeater. **Cluster-only** setup lives here; each service is deployed from its own `deployment/` folder.

## What’s here (common)

- **namespace.yaml**, **configmap.yaml**, **secrets.yaml** – shared for namespace `skill-repeater`
- **ingress/** – Traefik IngressRoute and Let’s Encrypt
- **scripts/k3s/deploy-to-k3s.sh** – prepare cluster only (namespace, registry secret, ConfigMap, Secrets, ingress). Does **not** deploy services.
- **scripts/k3s/install-k3s.sh**, **setup-env.sh** – k3s install and env checks
- **scripts/dockerhub/create-registry-secret.sh** – Docker Hub pull secret
- **scripts/dockerhub/build-and-push-all.sh** – build and push both images by calling each service’s `deployment/scripts/build-and-push.sh`
- **scripts/common/get-version.sh** – optional, for version from pom.xml

## What’s in each service

- **skill-repeater-front/deployment/** – `skill-repeater-front.yaml`, **scripts/deploy.sh** & **scripts/build-and-push.sh**
- **skill-repeater-service/deployment/** – `skill-repeater-service.yaml`, **scripts/deploy.sh** & **scripts/build-and-push.sh**

## Order of operations

1. Set env vars (see `scripts/k3s/setup-env.sh`): `DOCKERHUB_*`, `GITHUB_*`, `SKILL_REPEATER_*`, `JWT_*`, etc.
2. **Prepare cluster** (from this folder):  
   `./scripts/k3s/deploy-to-k3s.sh`
3. **Build and push images** (from this folder):  
   `./scripts/dockerhub/build-and-push-all.sh <version>`
4. **Deploy each service** (from that service’s folder):  
   `cd ../skill-repeater-service && ./deployment/scripts/deploy.sh <version>`  
   `cd ../skill-repeater-front && ./deployment/scripts/deploy.sh <version>`
